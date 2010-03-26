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

import conferr.gui.MainFrame;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.xml.transform.TransformerException;
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
public class FaultInjectionEngine extends ObservableBean implements Runnable  {
    
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

    private Vector<FaultInjectionResult> results = new Vector<FaultInjectionResult>();    
    
    private Vector<FaultInjectionResult> tempResults = new Vector<FaultInjectionResult>();

    private HashMap<String, ConfigurationFile> createConfigurationFilesMap() {
        HashMap<String, ConfigurationFile> configsObjs = new HashMap<String, ConfigurationFile>();

        for (ConfigurationFile file : plan.getConfigurationFiles()) {
            configsObjs.put(file.getName(), file);
        }

        return configsObjs;
    }

    private HashMap<String, Document> createDocumentsMap(ErrorGenerator s) throws Exception {
        HashMap<String, Document> configs = new HashMap<String, Document>();

        for (ConfigurationFile file : plan.getConfigurationFiles()) {
            try {
                //FIXME: this condition is not always correct
                if (s.getConfigurationTransform(file).getTransformInstance() == null) {
                    configs.put(file.getName(), file.getDocument());
                } else {

                    Transform t = s.getConfigurationTransform(file).getTransformInstance();

                    configs.put(file.getName(), t.filter(file.getDocument(), s.getConfigurationTransform(file)));
                }

                if (configs.get(file.getName()) == null) {
                    throw new Exception("An error occoured while parsing " + file.getName());
                }
            } catch (FileNotFoundException ex) {
                throw new Exception("Input file not found for " + file.getName());
            } catch (TransformerException ex) {
                throw new Exception("Error transforming " + file.getName());
            } catch (ImpossibleConfigurationException ex) {
                throw new Exception("Error transforming " + file.getName() + " (impossible configuration)");
            }
        }
        return configs;
    }
    
    private void saveResult(FaultInjectionResult result) {

        long diff = System.currentTimeMillis() - startTime;

        if (getPercentage() > 0)
            setEta(diff / getPercentage() * ( 100 - getPercentage()) / 1000);
                               
        tempResults.add(result);
        
        final Vector<FaultInjectionResult> t = new Vector<FaultInjectionResult>(tempResults); 
        
        setPercentage((int) (((double) tempResults.size() / (double) numExperiments) * 100));
        
        /* change content of the results form the event loop thread. */
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                setResults(t);
            }
        });
    }
        
    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        int old = this.percentage;
        this.percentage = percentage;
        pcs.firePropertyChange("percentage", old, percentage);
    }

    public String getStatus() {
        return status;
    }

    public long getEta() {
        return eta;
    }
    
    public void setEta(long eta) {
        Long old = this.eta;
        this.eta = eta;
        pcs.firePropertyChange("eta", old, (Long) eta);
    }
    
    public void setStatus(String status) {
        
        String old = this.status;
        this.status = status;
        pcs.firePropertyChange("status", old, this.status);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        Boolean old = this.running;
        this.running = running;
        pcs.firePropertyChange("running", (Boolean) old, (Boolean) running);
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        String old = outputDir;
        this.outputDir = outputDir;
        pcs.firePropertyChange("outputDir", old, outputDir);
    }

    public synchronized Vector<FaultInjectionResult> getResults() {
        return results;
    }

    public synchronized void setResults(Vector<FaultInjectionResult> results) {
        Vector<FaultInjectionResult> old = this.results;
        this.results = results;
        pcs.firePropertyChange("results", old, results);
    }

    public FaultInjectionPlan getPlan() {
        return plan;
    }

    public void setPlan(FaultInjectionPlan plan) {
        FaultInjectionPlan old = this.plan;
        this.plan = plan;
        pcs.firePropertyChange("plan", old, plan);
    }
    
    
    public synchronized void start( Vector<ErrorGenerator> sets) {
        if (! isRunning() ) {
            setRunning(true);
            
            setResults(new Vector<FaultInjectionResult>());
            tempResults = new Vector<FaultInjectionResult>();
            setPercentage(0);
            setStatus("Starting...");
            this.sets = sets;
            thread = new Thread(this);
            thread.start();
        }
    }
    
    public void stop() {
        if ( isRunning() ) {
            thread.interrupt();
        }
    }
    
    public void run() {
        
        for (ErrorGenerator errorGenerator : sets) {

            int confid = 0;
            int done = 0;

            numExperiments = 0;
            startTime = System.currentTimeMillis();       

            
            HashMap<String, ConfigurationFile> configsObjs = createConfigurationFilesMap();
            
            HashMap<String, Document> configs;
            
            try {
                configs = createDocumentsMap(errorGenerator);
            } catch (Exception ex ) {
                ex.printStackTrace();
                setStatus(ex.getMessage());
                setRunning(false);                            
                return;
            }

            numExperiments += errorGenerator.getFaultScenarioSet().getFaultTemplateInstance().getDescription(configs, errorGenerator.getFaultScenarioSet()).numberOfFaults();         
            
            try {

                Runner r = plan.getRunnerInstance();

                if (r == null) {
                    setStatus("An error occoured while initializing the runner");
                    setRunning(false);
                    return;
                }

                
                new java.io.File(plan.getAbsolutePath(outputDir) + "/" + errorGenerator.getName() ).mkdir();                

                FaultScenarioEnumeration e = errorGenerator.getFaultScenarioSet().getFaultTemplateInstance().faults(configs, new Random().nextLong(), errorGenerator.getFaultScenarioSet()) ;
                    
                int i = 0;
                
                outer: while ( e.hasMoreElements() ) {

                    FaultScenario f = e.nextElement();
                    
                    if (f == null) {
                        break;
                    }
                    
                    Map<String, Document> modified = f.getDocument();

                    HashMap<ConfigurationFile, Document> modifiedObjs = new HashMap<ConfigurationFile, Document>();                   
                    
                    Map<String, Document> unfilteredModified = new HashMap<String, Document>();
                    
                    for ( Map.Entry<String, Document> entry : modified.entrySet()) {                                               
                        
                        ConfigurationFile file = configsObjs.get(entry.getKey());
                        ConfigurationTransform configurationTransform = errorGenerator.getConfigurationTransform(file);
                        Transform transform = configurationTransform.getTransformInstance();
                        
                        if (transform != null) {                                
                            try {
                                Document unfilteredDocument = transform.unfilter(entry.getValue(), configurationTransform);                                
                                unfilteredModified.put(entry.getKey(), unfilteredDocument);                                          
                            } catch (ImpossibleConfigurationException ex) {
                                FaultInjectionResult result = new FaultInjectionResult(f.getDescription(), 
                                        new RunnerResult(ex.toString(), "", Runner.IMPOSSIBLE_CONFIGURATION, new TestResult[0])
                                        , errorGenerator, outputDir + "/" + confid + "/"  + i , modifiedObjs);

                                i++;
                                saveResult(result);
                                e.notifyInjectionResult(result);
                                continue outer;
                            }
                            
                        } else {              
                            unfilteredModified.put(entry.getKey(), entry.getValue());  
                        }
                    }
                    
                    for ( Map.Entry<String, Document> entry : unfilteredModified.entrySet()) {                                               
                        
                        ConfigurationFile file = configsObjs.get(entry.getKey());                        
                        
                        modifiedObjs.put(file, entry.getValue());
                        
                        Handler h = file.getHandlerInstance();

                        if (r == null) {
                            setStatus("An error occoured while initializing the configuration handler for " + file.getName() );
                            e.close();
                            setRunning(false);
                            return;
                        }
                        
                    
                        FileWriter writer = new FileWriter(plan.getAbsolutePath(file.getOutput()));
                        try {
                            

                            h.serializeConfiguration(entry.getValue(), writer, file);
                                           
                            
                        } catch (ImpossibleConfigurationException ex) {
                            FaultInjectionResult result = new FaultInjectionResult(f.getDescription(), 
                                    new RunnerResult(ex.toString(), "", Runner.IMPOSSIBLE_CONFIGURATION, new TestResult[0])
                                    , errorGenerator, outputDir + "/" + confid + "/"  + i , modifiedObjs);

                            i++;
                            saveResult(result);
                            e.notifyInjectionResult(result);
                            continue outer;
                        }

                        writer.close();
                    }
                    
                    new java.io.File(plan.getAbsolutePath(outputDir) + "/" + errorGenerator.getName() + "/"  + i ).mkdir();

                    setStatus("Injecting fault #" + (i + 1) + " of scenario " + errorGenerator.getName());                                      

                    RunnerResult ret = r.run(plan.getAbsolutePath(outputDir) + "/" + errorGenerator.getName() + "/"  + i, plan);                                      

                    done++;

                    if (Thread.currentThread().isInterrupted()) {
                        e.close();
                        setRunning(false);
                        return;
                    }
                    
                    FaultInjectionResult result = new FaultInjectionResult(f.getDescription(), ret, errorGenerator, outputDir + "/" + confid + "/"  + i , modifiedObjs);

                    saveResult(result);
                    e.notifyInjectionResult(result);
                    
                    i++;
                   
                }                
                 
                e.close();
                        
                confid++;
                
            } catch (InterruptedException ex) {                
                setRunning(false);
            } catch (Exception ex) {
                setStatus("Error: " + ex);
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
        
        setRunning(false);
    }
    
    
    public void writeToFile(String file) {
        
        Element report = new Element("report");
        
        for (FaultInjectionResult r : results) {
            Element result = r.toElement();                                    
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
