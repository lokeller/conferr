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
package ch.epfl.dslab.conferrng.engine;

import ch.epfl.dslab.conferrng.arugula.Configuration;
import ch.epfl.dslab.conferrng.arugula.ConfigurationWithError;
import org.jdom.CDATA;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;

/**
 * 
 * Results of the execution of the SUT.
 * 
 */
public class InjectionResult {

    /**
     * It was not possible to start the SUT
     */
    public final static int STARTUP_ERROR = 0;
    /**
     * It was not possible to shutdown the SUT
     */
    public final static int SHUTDOWN_ERROR = 1;
    /**
     * Injection could be carried out succesfully
     */
    public final static int OK = 2;
    /**
     * An internal error occoured (in conferr)
     */
    public final static int INTERNAL_ERROR = 3;
    /**
     * The configuration could not be serialized
     */
    public final static int IMPOSSIBLE_CONFIGURATION = 4;
    /**
     * An error occoured during the benchmarking
     */
    public final static int BENCHMARK_ERROR = 5;
    private String startupLog;
    private String shutdownLog;
    private int errorType;
    private BenchmarkResult[] benchmarkResults;
    private String description;
    private ConfigurationWithError configuration;
    private String outputDir;
    private final boolean shouldItSucceed;

    /**
     *
     * Create a new injection result
     *
     * @param description the description of the fault that was injected
     * @param configuration the configuration that was used to run the system
     * @param outputDir the directory that stores the output of the error injection
     * @param startupLog a description of the startup error
     * @param shutdownLog a description of the shutdown error
     * @param errorType the error type if an error occoured, InjectionResul.OK if no error occoured
     * @param testResults the results of the benchmarks performed
     */
    public InjectionResult(String description, ConfigurationWithError configuration, String outputDir, String startupLog, String shutdownLog, int errorType, BenchmarkResult[] testResults, boolean shouldItSucceed) {
        super();
        this.description = description;
        this.startupLog = startupLog;
        this.shutdownLog = shutdownLog;
        this.errorType = errorType;
        this.benchmarkResults = testResults;
        this.configuration = configuration;
        this.outputDir = outputDir;
        this.shouldItSucceed = shouldItSucceed;
    }

    public boolean isAccordingToSpecification() {
        return shouldItSucceed ? errorType == OK : errorType != OK;
    }

    /**
     *
     * Create a new injection result
     *
     * @param startupLog a description of the startup error
     * @param shutdownLog a description of the shutdown error
     * @param errorType the error type if an error occoured, InjectionResul.OK if no error occoured
     * @param testResults the results of the benchmarks performed
     */
    public InjectionResult(String startupLog, String shutdownLog, int errorType, BenchmarkResult[] testResults) {
        super();
        this.startupLog = startupLog;
        this.shutdownLog = shutdownLog;
        this.errorType = errorType;
        this.benchmarkResults = testResults;
        shouldItSucceed = false;
    }

    /**
     * 
     * Returns the description given by the error generator of the injected fault
     * 
     * @return the description of the fault injected
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * Returns the configuration that was used to do this error injection
     *
     * @return the faulty configuration
     */
    public Configuration getConfiguration() {
        return configuration.getConfiguration();
    }

    /**
     * 
     * Returns the directory used to store output for this fault injection
     * 
     * @return the directory that was used for this fault injection
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Returns a description of the startup error
     *
     * @return a description of the startup error
     */
    public String getShutdownLog() {
        return shutdownLog;
    }

    /**
     * Returns a description of the shutwdown error
     *
     * @return a description of the shutwdown error
     */
    public String getStartupLog() {
        return startupLog;
    }

    /**
     * Returns whether an error occoured during the fault injection
     * 
     * @return InjectionResult.OK if no error occoured, otherwise the error code
     */
    public int getErrorType() {
        return errorType;
    }

    /**
     * Returns the results of the benchmark done during this fault injection
     *
     * @return the benchmark results
     */
    public BenchmarkResult[] getBenchmarkResults() {
        return benchmarkResults;
    }

    public boolean getIsResilient() {
        return isAccordingToSpecification();
    }

    /**
     * Returns a plain text report of the fault injection
     *
     * @return a plain text report
     */
    public String toPlainText() {
        String ret = "Startup\n";
        ret += "--------------------------\n";
        ret += startupLog;
        ret += "--------------------------\n";
        ret += "Tests\n";
        ret += "--------------------------\n";
        if (benchmarkResults == null) {
            return ret;
        }
        for (BenchmarkResult r : benchmarkResults) {
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

    /**
     *
     * Returns a HTML report of the fault injection
     *
     * @return a string containing an HTML report
     */
    public String toHtml() {
        String ret = "<html><h1>Startup</h1>";
        ret += "<pre>" + startupLog + "</pre>";
        ret += "<h1>Tests</h1>";
        if (benchmarkResults == null) {
            return ret;
        }
        for (BenchmarkResult r : benchmarkResults) {
            ret += "<h2>" + r.getName();
            if (r.getErrorType() != BenchmarkResult.OK) {
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

    /**
     * Returns an XML fragment representing this injection result
     *
     * @return an XML element
     */
    public Element toXML() {

        Element element = new Element("result");
        element.setAttribute("errorType", getErrorType() + "");

        Element desc = new Element("description");
        desc.addContent(new CDATA(description));

        element.addContent(desc);

        Element startup = new Element("startup-log");
        startup.addContent(new CDATA(startupLog));

        element.addContent(startup);

        Element shutdown = new Element("shutdown-log");
        shutdown.addContent(new CDATA(shutdownLog));

        element.addContent(shutdown);

        Element configEl = new Element("configuration");

        Configuration executedConfiguration = configuration.getConfiguration();
        for (String fileName : executedConfiguration.getInputFileNameIterator()) {
            Element file = new Element("file");
            file.setAttribute("name", fileName);
            file.addContent(((Document) executedConfiguration.getModifiedDocumentForInputFile(fileName).clone()).getRootElement().detach());
            configEl.addContent(file);
        }

        element.addContent(configEl);

        Element benchmarksEl = new Element("benchmarks");

        if (benchmarkResults != null) {
            for (BenchmarkResult res : benchmarkResults) {
                benchmarksEl.addContent(res.toXML());
            }
        }

        element.addContent(((Content)configEl.clone()).detach());


        Element resilient = new Element("resilient");
        resilient.addContent(new Text("" + isAccordingToSpecification()));
        element.addContent(resilient);
        return element;

    }
}
