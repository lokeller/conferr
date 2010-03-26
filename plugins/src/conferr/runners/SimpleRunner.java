/*

Copyright (c) 2008, Dependable Systems Lab, EPFL
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, 
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, 
      this list of conditions and the following disclaimer in the documentation 
      and/or other materials provided with the distribution.
    * Neither the name of the Dependable Systems Lab, EPFL nor the names of its 
      contributors may be used to endorse or promote products derived from this 
      software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package conferr.runners;

import conferr.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleRunner implements Runner {

    public final static String startupString = "startup-script";
    public final static String shutdownString = "shutdown-script";
    public final static String testDirectoryString = "test-directory";
    
    public Vector<Parameter> getDefaultParameters() {
        
        Vector<Parameter> parameters = new Vector<Parameter>();
        
        parameters.add(new Parameter(startupString, "", Parameter.SCRIPT_FILE));
        parameters.add(new Parameter(shutdownString, "", Parameter.SCRIPT_FILE));
        parameters.add(new Parameter(testDirectoryString, "", Parameter.DIRECTORY));
            
        return parameters;
        
    }        

    public RunnerResult run(String outputDir , FaultInjectionPlan plan) throws InterruptedException {
        
        String [] envp = { "OUTPUTDIR=" + outputDir}; 
        
        StringBuffer output = new StringBuffer();
        StringBuffer outputSh = new StringBuffer();       
        
        File dirF = new File(plan.getAbsolutePath(plan.getBaseDirectory()));
        
        try {

            int startupErr = runCommand(plan.getAbsolutePath(plan.getRunnerParameterValue("startup-script")), envp, output, output, dirF);

            if (startupErr != 0) {

                shutdown(envp, outputSh,dirF, plan);
                return new RunnerResult(output.toString(), outputSh.toString(), STARTUP_ERROR, new TestResult[0]);
            }

            File[] fileList = new File(plan.getAbsolutePath(plan.getRunnerParameterValue("test-directory"))).listFiles();
            
            if (fileList == null) {
                
                output.append("Unable to find tests...\n");
                
                shutdown(envp, outputSh, dirF, plan);               
                return new RunnerResult(output.toString(), outputSh.toString(),INTERNAL_ERROR, new TestResult[0]);
            }
            
            Vector<TestResult>  results = new Vector<TestResult>();          
            
            int i = 0;                                  
            
            for (File f : fileList) {

                try { 
                    StringBuffer stdOut = new StringBuffer();
                    StringBuffer stdErr = new StringBuffer();
                    int testErr = runCommand(f.getPath(), envp, stdOut, stdErr,dirF);

                    results.add( new TestResult(f.getName(), stdOut.toString(), stdErr.toString(), (testErr == 0 ? OK : TEST_ERROR)));

                } catch (IOException ex) {
                    
                    System.err.println("Unable to run " + f.getName());
                }
                
                i++;
            }

            if (shutdown(envp, outputSh, dirF, plan) != 0) {
                return new RunnerResult(output.toString(), outputSh.toString(),SHUTDOWN_ERROR, results.toArray(new TestResult[results.size()]));
            } else {
                
                boolean err = false;
                for (TestResult t : results) {
                    if (t.getReturnValue() == TEST_ERROR) {
                        err = true;
                        break;                                
                    }
                }
                
                return new RunnerResult(output.toString(), outputSh.toString(),(err ? TEST_ERROR : OK), results.toArray(new TestResult[results.size()]));
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
            output.append(ex.toString());
            return new RunnerResult(output.toString(), outputSh.toString(),INTERNAL_ERROR, null);
        }
        
    }
    
    
    private int runCommand( String command, String [] envp, final StringBuffer output, final StringBuffer errorOutput, File dir) throws IOException, InterruptedException {

        final Process p = Runtime.getRuntime().exec(command, envp, dir);       

        new Thread(new Runnable() {

            public void run() {
                try {
                    String s;
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while ((s = stdInput.readLine()) != null) {
                        output.append(s + "\n");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SimpleRunner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();

        new Thread(new Runnable() {

            public void run() {
                try {
                    String s;
                    BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    while ((s = stdErr.readLine()) != null) {
                        errorOutput.append(s + "\n");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SimpleRunner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();

        p.waitFor();

        return p.exitValue();
    }


    
    private int shutdown(String [] envp, StringBuffer output, File dir, FaultInjectionPlan plan) throws IOException, InterruptedException {
        return runCommand(plan.getAbsolutePath(plan.getRunnerParameterValue("shutdown-script")), envp, output, output, dir);
    }

    

}
