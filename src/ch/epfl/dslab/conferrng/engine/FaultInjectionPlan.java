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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.ProcessingInstruction;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 *  This class represent a fault injection plan against a given piece of software.
 *  It stores multiple error generators, configuration files descriptions and all
 *  the parameters required to run the fault injection.
 * 
 */
public class FaultInjectionPlan extends ObservableBean implements Iterable<ConfigurationWithError>, PropertyChangeListener {

    public static final String PROP_ERRORGENERATORS = "errorGenerators";
    public static final String PROP_JARS = "jars";
    private String name = "New fault injection plan";
    private String file;

    private Vector<ErrorGenerator> errorGenerators = new Vector<ErrorGenerator>();
    private Vector<String> jars = new Vector<String>();
    private RunnerPlugin runner = null;

    private FileResolver resolver = new FileResolver();

    /**
     * Returns the name of this fault injection plan
     *
     * @return a string
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this injection plan
     *
     * @param name a string
     */
    public void setName(String name) {
        String old = this.name;
        this.name = name;
        pcs.firePropertyChange("name", old, name);
    }

    /**
     * Return all the error generator plugins that are going to be used in this
     * plan
     *
     * @return a vector of error generator plugins
     */
    public Vector<ErrorGenerator> getErrorGenerators() {
        return errorGenerators;
    }

    /**
     * Add a new error generator plugin to the plan
     *
     * @param plugin an error generator plugin
     */
    public void addErrorGenerator(ErrorGenerator plugin) {

        if(plugin==null)
            return;

        Vector<ErrorGenerator> newErrorGenerators = new Vector<ErrorGenerator>(errorGenerators);
        newErrorGenerators.add(plugin);

        setErrorGenerators(newErrorGenerators);

    }

    public void addErrorGenerator(ErrorGenerator plugin, int pos) {

        if(plugin==null)
            return;

        Vector<ErrorGenerator> newErrorGenerators = new Vector<ErrorGenerator>(errorGenerators);
        newErrorGenerators.insertElementAt(plugin, pos);

        setErrorGenerators(newErrorGenerators);

    }

    public void clearErrorGenerators() {
        setErrorGenerators(new Vector<ErrorGenerator>());
    }

    /**
     * Removes an error generator plugin from the plan
     *
     * @param plugin an error generator currently in the plan
     */
    public void removeErrorGenerator(ErrorGenerator plugin) {

        Vector<ErrorGenerator> newErrorGenerators = new Vector<ErrorGenerator>(errorGenerators);
        newErrorGenerators.remove(plugin);

        setErrorGenerators(newErrorGenerators);
    }

    /**
     * Sets the list of error generator plugins used in the plan
     *
     * @param errorGenerators a list of plugins
     */
    private void setErrorGenerators(Vector<ErrorGenerator> errorGenerators) {

        Vector<ErrorGenerator> oldGenerators = this.errorGenerators;

        for ( ErrorGenerator e : oldGenerators) {
            e.removePropertyChangeListener(this);
        }

        this.errorGenerators = errorGenerators;

        for ( ErrorGenerator e : errorGenerators) {
            e.addPropertyChangeListener(this);
        }

        pcs.firePropertyChange(PROP_ERRORGENERATORS, oldGenerators, this.errorGenerators);
    }

    /**
     * Returns the path of the file containing this plan
     *
     * @return the path used to save/load this plan
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the path used to save this plan
     *
     * @param file the path to a file
     */
    public void setFile(String file) {
        String old = file;
        this.file = file;

        /* update the current directory for the filename resolver */
        if ( file != null) {
            resolver.setCurrentDirectory(new File(file).getParent());
        } else {
            resolver.setCurrentDirectory(System.getProperty("user.dir"));
        }
        
        pcs.firePropertyChange("file", old, file);
    }

    /**
     * Returns the JAR files that contain the plugins for this plan
     *
     * @return a vector of paths to the JARs
     */
    public Vector<String> getJars() {
        return jars;
    }

    /**
     * Sets the JAR files list from which the plugins of this plan are loaded
     *
     * @param jars a vector of paths to JARs files
     */
    public void setJars(Vector<String> jars) {
        Vector<String> old = this.jars;
        this.jars = jars;

        pcs.firePropertyChange(PROP_JARS, old, jars);

    }

    /**
     * Add a JAR containing plugins to the plan
     *
     * @param jar the path to the JAR file
     */
    public void addJar(String jar) {
        Vector<String> newJars = new Vector<String>(jars);
        newJars.add(jar);
        setJars(newJars);
    }

    /**
     * Remove JAR file from the plan
     *
     * @param jar a path to a JAR file that has to be removed
     */
    public void removeJar(String jar) {
        Vector<String> newJars = new Vector<String>(jars);
        newJars.remove(jar);
        setJars(newJars);
    }

    private String stringToXMLString(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * Load the settings for this plan from the specified file
     *
     * @param pfile a path to an XML file created with saveToFile
     * @throws JDOMException
     * @throws IOException
     */
    public void loadFromFile(String pfile) throws JDOMException, IOException {                
        loadFromFile(pfile,null);
    }

    /**
     *
     * Load a project using the specified list of jars instead of what is found
     * in the XML file
     *
     * @param pfile a path to an XML file created with saveToFile
     * @param jars the path of the jars (if null the jars will be loaded from the XML file)
     * @throws JDOMException
     * @throws IOException
     */
    public void loadFromFile(String pfile, Vector<String> jars) throws JDOMException, IOException {

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(pfile);

        setFile(pfile);

        setName(doc.getRootElement().getAttributeValue("name"));
        resolver.setBaseDirectory(loadBaseDir(doc));

        if ( jars == null) {
           setJars(loadJars(doc));
        } else {
           setJars(jars);
        }

        try {
            if (doc.getRootElement().getChild("runner").getChild("plugin") != null) {
                setRunnerPlugin((RunnerPlugin) PluginFactory.fromXML(doc.getRootElement().getChild("runner").getChild("plugin"), this));
            }
        } catch (Exception ex) {
            Logger.getLogger(FaultInjectionPlan.class.getName()).log(Level.SEVERE, null, ex);
        }

        PluginFactory.setNewJars(this);

        Element ssetEl = doc.getRootElement().getChild("error-generators");
        setErrorGenerators(loadErrorGenerators(ssetEl));

    }

    public String loadBaseDir(Document doc) {
        return doc.getRootElement().getAttributeValue("base-directory");
    }

    public Vector<String> loadJars(Document doc) {
        Vector<String> newJars = new Vector<String>();

        for (Object o : doc.getRootElement().getChild("jars").getChildren("jar")) {
            newJars.add(((Element) o).getAttributeValue("ref"));
        }
        return newJars;
    }

    private Vector<ErrorGenerator> loadErrorGenerators(Element ssetEl) {

        String eName = new ErrorGenerator(this, null).getFactory().getName();
        Vector<ErrorGenerator> newErrGen = new Vector<ErrorGenerator>();

        for (Object o : ssetEl.getChildren(eName)) {
            try {
                Element scenarioSetElement = (Element) o;
                ErrorGenerator s = (ErrorGenerator) PluginFactory.getOperator(scenarioSetElement, this);
                newErrGen.add(s);
            } catch (Exception ex) {
                Logger.getLogger(FaultInjectionPlan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return newErrGen;
    }

    /**
     *
     * Saves the plan to an XML file
     *
     * @param pfile the name of the file that should be created
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void saveToFile(String pfile) throws FileNotFoundException, IOException {

        setFile(pfile);

        Document doc = new Document();

        ProcessingInstruction sty = new ProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"plan.xsl\"");
        doc.addContent(sty);

        doc.setRootElement(new Element("plan"));

        doc.getRootElement().setAttribute("name", stringToXMLString(name));
        doc.getRootElement().setAttribute("base-directory", stringToXMLString(resolver.getBaseDirectory()));

        Element runnerElement = new Element("runner");

        if (runner != null) {
            runnerElement.addContent(runner.toXML());
        }

        doc.getRootElement().addContent(runnerElement);

        Element jarsElement = new Element("jars");

        for (String jar : jars) {
            Element jarEl = new Element("jar");
            jarEl.setAttribute("ref", stringToXMLString(jar));
            jarsElement.addContent(jarEl);
        }

        doc.getRootElement().addContent(jarsElement);

        Element errorGeneratorsXML = saveErrorGeneratorsToFile();

        doc.getRootElement().addContent(errorGeneratorsXML);

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

        outputter.output(doc, new FileOutputStream(pfile));

    }

    public Element saveErrorGeneratorsToFile() {

        Element scenarioSetsElement = new Element("error-generators");

        for (ErrorGenerator s : errorGenerators) {
            scenarioSetsElement.addContent(s.toXML());
        }

        return scenarioSetsElement;
    }

    /**
     *
     * Clear all settings of this plan
     *
     */
    public void clear() {
        setFile(null);
        setName("New fault injection plan");
        resolver.setBaseDirectory(".");
        setErrorGenerators(new Vector<ErrorGenerator>());
        setJars(new Vector<String>());

        setRunnerPlugin(null);

    }

    /**
     *
     * Sets the runner plugin for this plan
     *
     * @param plugin an instance of a runner plugin
     */
    public void setRunnerPlugin(RunnerPlugin plugin) {
        RunnerPlugin oldRunnerParameters = runner;
        this.runner = plugin;

        pcs.firePropertyChange("runnerPlugin", oldRunnerParameters, plugin);
    }

    /**
     * Returns the current runner plugin for this plan
     *
     * @return the current runner plugin instance
     */
    public RunnerPlugin getRunnerPlugin() {
        return runner;
    }
    private List<ConfigurationWithError> configurationErrors = new Vector<ConfigurationWithError>();

    public void computeConfigurationWithErrors() {
        Vector<ConfigurationWithError> newErrors = new Vector<ConfigurationWithError>();
        for (ErrorGenerator eg : errorGenerators) {
            if ( eg.getErrors().size() == 0) {
                eg.initRandom();
                Collection<ConfigurationWithError> result = eg.apply(null);
                if (result != null) {
                    System.err.println("+++++++++++NOT NULL");
                    newErrors.addAll(result);
                }else{
                    System.err.println("+++++++++++NULL");
                }
            }
        }
        setConfigurationErrors(newErrors);
    }

    @Override
    public Iterator<ConfigurationWithError> iterator() {
        return configurationErrors.iterator();
    }

    public static final String PROP_CONFIGURATIONERRORS = "configurationErrors";

    /**
     * Get the value of configurationErrors
     *
     * @return the value of configurationErrors
     */
    public List<ConfigurationWithError> getConfigurationErrors() {
        return configurationErrors;
    }

    /**
     * Set the value of configurationErrors
     *
     * @param configurationErrors new value of configurationErrors
     */
    public void setConfigurationErrors(List<ConfigurationWithError> configurationErrors) {
        System.err.println("############ "+configurationErrors.size());
        List oldConfigurationErrors = this.configurationErrors;
        this.configurationErrors = configurationErrors;
        pcs.firePropertyChange(PROP_CONFIGURATIONERRORS, oldConfigurationErrors, configurationErrors);
    }

    public FileResolver getResolver() {
        return resolver;
    }

    public String getRelativePath(String path) {
        return resolver.getRelativePath(path);
    }

    public String getAbsolutePath(String path) {
        return resolver.getAbsolutePath(path);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        computeConfigurationWithErrors();
    }
    


}
