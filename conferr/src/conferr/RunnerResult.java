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

package conferr;

/**
 * 
 * Results of the execution of the SUT.
 * 
 */

public class RunnerResult {

    private String startupLog;
    private String shutdownLog;
    private int retVal;
    private TestResult[] testResults;

    public RunnerResult(String startupLog, String shutdownLog, int retVal, TestResult[] testResults) {
        super();
        this.startupLog = startupLog;
        this.shutdownLog = shutdownLog;
        this.retVal = retVal;
        this.testResults = testResults;
    }

    public String getShutdownLog() {
        return shutdownLog;
    }

    public void setShutdownLog(String shutdownLog) {
        this.shutdownLog = shutdownLog;
    }

    public String getStartupLog() {
        return startupLog;
    }

    public void setStartupLog(String startupLog) {
        this.startupLog = startupLog;
    }

    public String getCompleteLog() {
        String ret = "Startup\n";
        ret += "--------------------------\n";
        ret += startupLog;
        ret += "--------------------------\n";
        ret += "Tests\n";
        ret += "--------------------------\n";
        if (testResults == null) {
            return ret;
        }
        for (TestResult r : testResults) {
            ret += r.getName() + "\n";
            ret += r.getResult();
            ret += "----\n";
            ret += r.getErrors();
            ret += "----\n";
        }

        ret += "Shutdown\n";
        ret += "--------------------------\n";
        ret += shutdownLog;
        ret += "--------------------------\n";

        return ret;
    }

    public String getCompleteLogHtml() {
        String ret = "<html><h1>Startup</h1>";
        ret += "<pre>" + startupLog + "</pre>";
        ret += "<h1>Tests</h1>";
        if (testResults == null) {
            return ret;
        }
        for (TestResult r : testResults) {
            ret += "<h2>" + r.getName();
            if (r.getReturnValue() != Runner.OK) {
                ret += " <font color=\"#FF0000\">FAILED</font>";
            }
            ret += "</h2>";
            if (!r.getResult().equals("")) {
                ret += "<h3>Output</h3><pre>" + r.getResult() + "</pre>";
            }
            if (!r.getResult().equals("")) {
                ret += "<h3>Errors</h3><pre>" + r.getErrors() + "</pre>";
            }
        }

        ret += "<html><h1>Shutdown</h1>";
        ret += "<pre>" + shutdownLog + "</pre>";

        ret += "</html>";

        return ret;
    }

    public int getRetVal() {
        return retVal;
    }

    public void setRetVal(int retVal) {
        this.retVal = retVal;
    }

    public TestResult[] getTestResults() {
        return testResults;
    }

    public void setTestResults(TestResult[] testResults) {
        this.testResults = testResults;
    }
}
