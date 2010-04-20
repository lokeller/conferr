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

package ch.epfl.dslab.conferrng.plugins.runners;


import ch.epfl.dslab.conferrng.engine.AbstractPlugin;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import ch.epfl.dslab.conferrng.engine.RunnerPlugin;
import ch.epfl.dslab.conferrng.engine.InjectionResult;
import ch.epfl.dslab.conferrng.engine.BenchmarkResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleRunner extends AbstractPlugin implements RunnerPlugin {

    public final static String startupString = "startup-script";
    public final static String shutdownString = "shutdown-script";
    public final static String testDirectoryString = "test-directory";
    
    public SimpleRunner(FaultInjectionPlan plan) {
        super(plan);

        addParameter(new Parameter(startupString, "", Parameter.FILE));
        addParameter(new Parameter(shutdownString, "", Parameter.FILE));
        addParameter(new Parameter(testDirectoryString, "", Parameter.DIRECTORY));
        
    }        

    @Override
    public InjectionResult run(String outputDir) throws InterruptedException {

        String [] envp = { "OUTPUTDIR=" + outputDir}; 
        
        StringBuffer output = new StringBuffer();
        StringBuffer outputSh = new StringBuffer();       
        
        File dirF = new File(plan.getAbsolutePath(plan.getResolver().getBaseDirectory()));
        
        try {

            int startupErr = runCommand(plan.getAbsolutePath(getParameterValue("startup-script")), envp, output, output, dirF);

            if (startupErr != 0) {

                shutdown(envp, outputSh,dirF, plan);
                return new InjectionResult(output.toString(), outputSh.toString(), InjectionResult.STARTUP_ERROR, new BenchmarkResult[0]);
            }

            File[] fileList = new File(plan.getAbsolutePath(getParameterValue("test-directory"))).listFiles();
            System.err.println("+++++++RUNNING "+Arrays.toString(fileList));
            
            if (fileList == null) {
                
                output.append("Unable to find tests...\n");
                
                shutdown(envp, outputSh, dirF, plan);               
                return new InjectionResult(output.toString(), outputSh.toString(),InjectionResult.INTERNAL_ERROR, new BenchmarkResult[0]);
            }
            
            Vector<BenchmarkResult>  results = new Vector<BenchmarkResult>();
            
            int i = 0;                                  
            
            for (File f : fileList) {

                if ( ! f.canExecute()) continue;
                
                try { 
                    StringBuffer stdOut = new StringBuffer();
                    StringBuffer stdErr = new StringBuffer();
                    int testErr = runCommand(f.getPath(), envp, stdOut, stdErr,dirF);

                    System.err.println("########## "+stdOut);
                    System.err.println("+++++++++++" + stdErr);
                    results.add( new BenchmarkResult(f.getName(), stdOut.toString(), stdErr.toString(), (testErr == 0 ? BenchmarkResult.OK : BenchmarkResult.ERROR)));

                } catch (IOException ex) {
                    
                    System.err.println("Unable to run " + f.getName());
                }
                
                i++;
            }

            if (shutdown(envp, outputSh, dirF, plan) != 0) {
                return new InjectionResult(output.toString(), outputSh.toString(),InjectionResult.SHUTDOWN_ERROR, results.toArray(new BenchmarkResult[results.size()]));
            } else {
                
                boolean err = false;
                for (BenchmarkResult t : results) {
                    if (t.getErrorType() == BenchmarkResult.ERROR) {
                        err = true;
                        break;                                
                    }
                }
                
                return new InjectionResult(output.toString(), outputSh.toString(),(err ? InjectionResult.BENCHMARK_ERROR : InjectionResult.OK), results.toArray(new BenchmarkResult[results.size()]));
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
            output.append(ex.toString());
            return new InjectionResult(output.toString(), outputSh.toString(),InjectionResult.INTERNAL_ERROR, null);
        }
        
    }
    
    
    private int runCommand( String command, String [] envp, final StringBuffer output, final StringBuffer errorOutput, File dir) throws IOException, InterruptedException {

        final Process p = Runtime.getRuntime().exec(command, envp, dir);       

        System.err.println("+++++++++TEST: "+command);
        new Thread(new Runnable() {

            public void run() {
                try {
                    String s;
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while ((s = stdInput.readLine()) != null) {
                        output.append(s + "\n");
                        System.err.println(">>>>>>OUTPUT "+s);
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
                        System.err.println(">>>>>>ERROR OUTPUT "+s);
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
        return runCommand(plan.getAbsolutePath(getParameterValue("shutdown-script")), envp, output, output, dir);
    }
    

}
