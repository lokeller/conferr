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

import ch.epfl.dslab.conferrng.arugula.ConfigurationWithError;
import ch.epfl.dslab.conferrng.arugula.ErrorGenerator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ProcessingInstruction;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * This class coordinates the fault injection. It can be configured to use
 * faults from a given set of error generators. The fault injection can be
 * monitored by using getEta, getPercentage and getDescription. The report
 * of the last fault injection can be obtained by calling getResults.
 *  
 */
public class FaultInjectionEngine extends ObservableBean implements Runnable {

    private int percentage;
    private long eta;
    private String status;
    private long startTime;
    private long numExperiments;
    private boolean running;
    private Thread thread;
    private String outputDir = "";
    private FaultInjectionPlan plan;
    private Vector<ErrorGenerator> sets;
    private Vector<InjectionResult> results = new Vector<InjectionResult>();
    private Vector<InjectionResult> tempResults = new Vector<InjectionResult>();

    private void saveResult(InjectionResult result) {

        long diff = System.currentTimeMillis() - startTime;

        if (getPercentage() > 0) {
            setEta(diff / getPercentage() * (100 - getPercentage()) / 1000);
        }

        tempResults.add(result);

        final Vector<InjectionResult> t = new Vector<InjectionResult>(tempResults);

        setPercentage((int) (((double) tempResults.size() / (double) numExperiments) * 100));

        /* change content of the results form the event loop thread. */
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                setResults(t);
            }
        });
    }

    /**
     *
     * Returns the percentage of faults that have already been injected
     *
     * @return a number between 0 and 100
     */
    public int getPercentage() {
        return percentage;
    }

    private void setPercentage(int percentage) {
        int old = this.percentage;
        this.percentage = percentage;
        pcs.firePropertyChange("percentage", old, percentage);
    }

    /**
     * Returns the current status of the injection engine
     *
     * @return a human readable string
     */
    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {

        String old = this.status;
        this.status = status;
        pcs.firePropertyChange("status", old, this.status);
    }

    /**
     *
     * Returns an estimation of the time remaining before the end of the
     * injection
     *
     * @return returns the expected time to complete in ms
     */
    public long getEta() {
        return eta;
    }

    private void setEta(long eta) {
        Long old = this.eta;
        this.eta = eta;
        pcs.firePropertyChange("eta", old, (Long) eta);
    }

    /**
     * Returs true if the engine is currently executing an error injection plan
     *
     * @return true if the engine is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        Boolean old = this.running;
        this.running = running;
        pcs.firePropertyChange("running", (Boolean) old, (Boolean) running);
    }

    /**
     *
     * Returns the directory where the injection plan results will be stored
     *
     * @return the path to a directory
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Sets the directory where the injection plan results will be stored
     *
     * @param outputDir a path to a directory
     */
    public void setOutputDir(String outputDir) {
        String old = this.outputDir;
        this.outputDir = outputDir;
        pcs.firePropertyChange("outputDir", old, outputDir);
    }

    /**
     *
     * Returns the list of results for the injections happened up to now
     *
     * @return a list of results for the injections performed until now
     */
    public synchronized Vector<InjectionResult> getResults() {
        return results;
    }

    private synchronized void setResults(Vector<InjectionResult> results) {
        Vector<InjectionResult> old = this.results;
        this.results = results;
        pcs.firePropertyChange("results", old, results);
    }

    /**
     *
     * Returns the plan that will be executed by engine
     *
     * @return the plan currently loaded in the engine
     */
    public FaultInjectionPlan getPlan() {
        return plan;
    }

    /**
     *
     * Changes the plan that has to be executed by the engine
     *
     * @param plan a fault injection plan
     */
    public void setPlan(FaultInjectionPlan plan) {
        FaultInjectionPlan old = this.plan;
        this.plan = plan;
        pcs.firePropertyChange("plan", old, plan);
    }

    /**
     *
     * Starts the error injection
     *
     * @param sets a subset of error generator plugins that should be used for
     * the error injection
     */
    public synchronized void start(Vector<ErrorGenerator> sets) {
        if (!isRunning()) {
            setRunning(true);

            setResults(new Vector<InjectionResult>());
            tempResults = new Vector<InjectionResult>();
            setPercentage(0);
            setStatus("Starting...");
            this.sets = sets;
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     *
     * Interrupt the fault injection
     *
     */
    public void stop() {
        if (isRunning()) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {

        numExperiments = 0;
        startTime = System.currentTimeMillis();

        int confid = 0;
        int done = 0;
        
        for (ConfigurationWithError configWithError : plan) {
            
            try {
                configWithError.writeConfiguration();
                RunnerPlugin r = plan.getRunnerPlugin();

                if (r == null) {
                    setStatus("An error occoured while initializing the runner");
                    setRunning(false);
                    return;
                }

                String newFolder = "Configuration" + (confid);
                File f = new java.io.File(plan.getAbsolutePath(outputDir) + "/" + newFolder);
                f.mkdir();
                String fullDir = f.getAbsolutePath();

                setStatus("Injecting fault #" + (confid + 1) + " of scenario " + configWithError.getDescription());

                InjectionResult ret = r.run(plan.getAbsolutePath(outputDir) + "/" + newFolder);

                done++;

                if (Thread.currentThread().isInterrupted()) {
                    setRunning(false);
                    return;
                }

                InjectionResult result = new InjectionResult(configWithError.getDescription(),
                        configWithError,
                        fullDir,
                        ret.getStartupLog(),
                        ret.getShutdownLog(),
                        ret.getErrorType(),
                        ret.getBenchmarkResults(),
                        configWithError.getShouldSucceed());

                saveResult(result);

                confid++;

            } catch (InterruptedException ex) {
                setRunning(false);
            } catch (Exception ex) {
                setStatus("Error: " + ex);
                Logger.getLogger(FaultInjectionEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        setRunning(false);
    }

    /**
     * Writes a report of the current injection to a file
     *
     * @param file the path to a file
     */
    public void writeReportToFile(String file) {

        Element report = new Element("report");

        for (InjectionResult r : results) {
            Element result = r.toXML();
            report.addContent(result);
        }

        Document d = new Document();
        ProcessingInstruction sty = new ProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"report.xsl\"");
        d.addContent(sty);
        d.addContent(report);

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());

        FileWriter writer = null;

        try {

            writer = new FileWriter(file);
            out.output(d, writer);

        } catch (IOException ex) {
            Logger.getLogger(FaultInjectionEngine.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(FaultInjectionEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
