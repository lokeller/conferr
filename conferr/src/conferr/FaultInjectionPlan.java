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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
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
public class FaultInjectionPlan extends DefaultPluginContainer implements PropertyChangeListener {
    
    
    private String name = "New fault injection plan";
    
    private String file;
    
    private String baseDirectory = ".";
    
    private Vector<ConfigurationFile> configurationFiles = new Vector<ConfigurationFile>();
    
    private Vector<ErrorGenerator> errorGenerators = new Vector<ErrorGenerator>();
    
    private Vector<String> jars = new Vector<String>();
    
    public FaultInjectionPlan() {
        pcs.addPropertyChangeListener(this);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        pcs.firePropertyChange("name", old, name);
    }

    public Vector<ConfigurationFile> getConfigurationFiles() {
        return configurationFiles;
    }
    
    public void addConfigurationFile(ConfigurationFile file) {              
        Vector<ConfigurationFile> old = configurationFiles;
        setConfigurationFiles(new Vector<ConfigurationFile>(old));
        configurationFiles.add(file);
        pcs.firePropertyChange("configurationFiles", old, configurationFiles);
    }

    public void removeConfigurationFile(ConfigurationFile file) {              
        Vector<ConfigurationFile> old = configurationFiles;
        setConfigurationFiles(new Vector<ConfigurationFile>(old));
        configurationFiles.remove(file);
        pcs.firePropertyChange("configurationFiles", old, configurationFiles);
    }

    public Vector<ErrorGenerator> getErrorGenerators() {
        return errorGenerators;
    }
        
    public void addErrorGenerator(ErrorGenerator scenarioSet) {              
        Vector<ErrorGenerator> old = errorGenerators;
        setErrorGenerators(new Vector<ErrorGenerator>(old));
        errorGenerators.add(scenarioSet);
        pcs.firePropertyChange("errorGenerators", old, errorGenerators);
    }
    
    
    public void removeErrorGenerator(ErrorGenerator scenarioSet) {              
        Vector<ErrorGenerator> old = errorGenerators;
        setErrorGenerators(new Vector<ErrorGenerator>(old));
        errorGenerators.remove(scenarioSet);
        pcs.firePropertyChange("errorGenerators", old, errorGenerators);
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        String old = this.baseDirectory;
        this.baseDirectory = baseDirectory;
        pcs.firePropertyChange("baseDirectory", old, this.baseDirectory);
    }

    public String getRunner() {
        return getPluginClass();
    }

    public Runner getRunnerInstance() {
        return (Runner) getPluginInstance();

    }
    
    public void setRunner(String runner) {
        setPluginClass(runner);
    }

    public Vector<Parameter> getRunnerParams() {
        return getParams();
    }
    
    public void setRunnerParams(Vector<Parameter> runnerParams) {
        
        setParams(runnerParams);
        
    }

    public void setErrorGenerators(Vector<ErrorGenerator> errorGenerators) {
        Vector<ErrorGenerator> old = this.errorGenerators;
        this.errorGenerators = errorGenerators;
        pcs.firePropertyChange("errorGenerators", old, this.errorGenerators);
    }

    public void setConfigurationFiles(Vector<ConfigurationFile> configurationFiles) {
        Vector<ConfigurationFile> old = this.configurationFiles;
        this.configurationFiles = configurationFiles;
        pcs.firePropertyChange("configurationFiles", old, this.configurationFiles);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        String old = file;        
        this.file = file;
        pcs.firePropertyChange("file", old, file);
    }
        
  
    private ErrorGenerator findErrorGeneratorByName(String name) {
        for (ErrorGenerator s : errorGenerators) {
            if (s != null && s.getName().equals(name)) return s;            
        }
        return null;
    }
    
    private ConfigurationFile findConfigurationFileSetByName(String name) {
        for (ConfigurationFile s : configurationFiles) {
            if (s != null && s.getName().equals(name)) return s;            
        }
        return null;
    }
    
       public String getRunnerParameterValue(String name) {
        return getParameterValue(name);
    }
    
    public String getAbsolutePath(String path) {
        
        if (baseDirectory == null) return path;
        if (new File(path).isAbsolute()) return path;
        else if ( getBaseDirectory().equals(".") && file != null) return new File(file).getParent() + File.separator + path;
        else return getBaseDirectory() + File.separator + path;
    }

    public String getRelativePath(String path) {
        try {

            File baseFile = new File(getAbsolutePath(getBaseDirectory()));

            if (path.startsWith(baseFile.getCanonicalPath())) {
                return path.substring(baseFile.getCanonicalPath().length() + 1);
            } else {
                return path;
            }
        } catch (IOException ex) {
          return path;
        }
    }

    public Vector<String> getJars() {
        return jars;
    }

    public void setJars(Vector<String> jars) {
        Vector<String> old = this.jars;
        this.jars = jars;
        
        pcs.firePropertyChange("jars", old, jars);
        
    }
    
    public void addJar(String jar) {
        Vector<String> newJars = new Vector<String>(jars);
        newJars.add(jar);
        setJars(newJars);
    }
    
    public void removeJar(String jar) {
        Vector<String> newJars = new Vector<String>(jars);
        newJars.remove(jar);
        setJars(newJars);
    }
    
    private Vector<ConfigurationFile> loadConfigFiles(Element configsEl) {

        Vector<ConfigurationFile> configFiles = new Vector<ConfigurationFile>();

        for (Object o : configsEl.getChildren("config-file")) {
            configFiles.add(ConfigurationFile.fromElement((Element) o , this));
        }

        return configFiles;
    }
    
    public String stringToXMLString(String str) {
        if (str == null) return "";
        else return str;
    }       
    
    public static Vector<String> loadJarListFromFile( String pfile ) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
		
        Document doc = builder.build(pfile);               
                
        Vector<String> jars = new Vector<String>();
        
        for (Object o : doc.getRootElement().getChild("jars").getChildren("jar")) {            
            jars.add(((Element) o).getAttributeValue("ref"));
        }
        
        return jars;
        
    }
    
    public void loadBaseDirFromFile( String pfile ) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
		
        Document doc = builder.build(pfile);               
        
        
           
    }

    public void loadFromFile(String pfile, Vector<String> jars ) throws JDOMException, IOException {
		
	setFile(pfile);
        
        SAXBuilder builder = new SAXBuilder();
		
        Document doc = builder.build(pfile);

        setName(doc.getRootElement().getAttributeValue("name"));
        
        setBaseDirectory(doc.getRootElement().getAttributeValue("base-directory"));

        setJars(jars);
        
        setRunner(doc.getRootElement().getChild("runner").getAttributeValue("class-name"));
                    
        for (Object o : doc.getRootElement().getChild("runner").getChildren("param")) {
            
            Element e = (Element) o;
            
            setParameterValue(e.getAttributeValue("name"), e.getAttributeValue("value"));
                        
        }	
        
        Element ssetEl = doc.getRootElement().getChild("error-generators");
        
        setErrorGenerators(loadErrorGenerators(ssetEl));

        Element configsEl = doc.getRootElement().getChild("config-files");
        Vector<ConfigurationFile> configFiles = loadConfigFiles(configsEl);
		
        setConfigurationFiles(configFiles);
        
        loadConfigurationFilters(doc.getRootElement().getChild("filters"));
    }




    private Vector<ErrorGenerator> loadErrorGenerators(Element ssetEl) {

        Vector<ErrorGenerator> newErrGen = new Vector<ErrorGenerator>();

        for (Object o : ssetEl.getChildren("error-generator")) {

            Element scenarioSetElement = (Element) o;

            ErrorGenerator sset = new ErrorGenerator(this);

            sset.setName(scenarioSetElement.getAttributeValue("name"));

            FaultScenarioSet s = FaultScenarioSet.fromElement(this, scenarioSetElement.getChild("template"), null);

            sset.setFaultScenarioSet(s);
            
            newErrGen.add(sset);
        }

        return newErrGen;
    }
    

    
    private void loadConfigurationFilters(Element filters) {

            if (filters == null) return;
        
            for (Object o2 : filters.getChildren("filter")) {
                Element fEl = (Element) o2;
                
                ConfigurationFile config = findConfigurationFileSetByName(fEl.getAttributeValue("file"));
                ErrorGenerator sset = findErrorGeneratorByName(fEl.getAttributeValue("error-generator"));
                
                if (sset == null || config == null ) continue;
                
                ConfigurationTransform filter = sset.getConfigurationTransform(config);
                
                filter.setPluginClass(fEl.getAttributeValue("filter-class"));
                
                for ( Object o : fEl.getChildren("param")) {
                    Element pEl = (Element) o;
                    
                    filter.setParameterValue(pEl.getAttributeValue("name"), pEl.getAttributeValue("value"));                    
                }
                
            }
    }
    
    
        public void saveToFile(String pfile) throws FileNotFoundException, IOException {
        
        setFile(pfile);
        
        Document doc = new Document();
        
        ProcessingInstruction sty = new ProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"plan.xsl\"");
        doc.addContent(sty);
        
        doc.setRootElement(new Element("plan"));
        
        doc.getRootElement().setAttribute("name", stringToXMLString(name));
        doc.getRootElement().setAttribute("base-directory", stringToXMLString(baseDirectory));
        
        Element runnerElement = new Element("runner");
        
        runnerElement.setAttribute("class-name", stringToXMLString(getPluginClass()));
        
        for (Parameter p : getParams()) {
            Element pElement = new Element("param");
            pElement.setAttribute("name", stringToXMLString(p.getName()));
            pElement.setAttribute("value", stringToXMLString(p.getValue()));
            runnerElement.addContent(pElement);
        }
        
        doc.getRootElement().addContent(runnerElement);
        
        Element jarsElement = new Element("jars");
        
        for (String jar : jars ) {
            Element jarEl = new Element("jar");
            jarEl.setAttribute("ref", stringToXMLString(jar));
            jarsElement.addContent(jarEl);
        }
        
        doc.getRootElement().addContent(jarsElement);             
        
        Element scenarioSetsElement = saveErrorGeneratorsToFile();
        
        doc.getRootElement().addContent(scenarioSetsElement);
        Element configFilesElement = saveConfigurationFiles();
        
        doc.getRootElement().addContent(configFilesElement);
        
        doc.getRootElement().addContent(saveConfigurationFilters());
        
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

        outputter.output(doc, new FileOutputStream(pfile));       
        
    }
    
    private Element saveConfigurationFilters() {
        Element e = new Element("filters");
        
        for (ErrorGenerator s : errorGenerators) {
            for (Map.Entry<ConfigurationFile, ConfigurationTransform> entry : s.getConfigurationTransforms().entrySet()) {
                Element filter = new Element("filter");
                
                filter.setAttribute("filter-class", stringToXMLString(entry.getValue().getPluginClass()));
                filter.setAttribute("file", stringToXMLString(entry.getKey().getName()));
                filter.setAttribute("error-generator", stringToXMLString(s.getName()));
                
                for (Parameter p : entry.getValue().getParams()) {
                    Element pEl = new Element("param");
                    pEl.setAttribute("name",p.getName());
                    pEl.setAttribute("value", stringToXMLString(p.getValue()));
                    filter.addContent(pEl);
                }
                
                e.addContent(filter);
            }
        }
        
        return e;
    }

    private Element saveConfigurationFiles() {

        Element configFilesElement = new Element("config-files");

        for (ConfigurationFile f : configurationFiles) {
            configFilesElement.addContent(f.toElement());
        }

        return configFilesElement;
    }

    private Element saveErrorGeneratorsToFile() {

        Element scenarioSetsElement = new Element("error-generators");

        for (ErrorGenerator s : errorGenerators) {
            scenarioSetsElement.addContent(s.toElement());
        }

        return scenarioSetsElement;
    }
    
    
        
    public void saveErrorGeneratorsToLibrary(String pfile) throws FileNotFoundException, IOException {
               
        Document doc = new Document();
        
        doc.setRootElement(new Element("library"));
                
        Element scenarioSetsElement = saveErrorGeneratorsToFile();
        
        doc.getRootElement().addContent(scenarioSetsElement);                
        
        XMLOutputter outputter = new XMLOutputter();

        outputter.output(doc, new FileOutputStream(pfile));       
        
    }

    public void loadErrorGeneratorsFromLibrary(String pfile) throws JDOMException, IOException {		
        
        SAXBuilder builder = new SAXBuilder();
		
        Document doc = builder.build(pfile);        
                
        Element ssetEl = doc.getRootElement().getChild("error-generators");        
        
        setErrorGenerators(loadErrorGenerators(ssetEl));        
        
    }    
 
    @Override
    public String getPluginInterface() {
        return "conferr.Runner";
    }

    public void propertyChange(PropertyChangeEvent arg0) {
        if (arg0.getPropertyName().equals("params")) {
            pcs.firePropertyChange("runnerParams", arg0.getOldValue(), arg0.getNewValue());
        } else if (arg0.getPropertyName().equals("pluginClass")) {
            pcs.firePropertyChange("runner", arg0.getOldValue(), arg0.getNewValue());
        }
    }

    @Override
    public ClassFinderBean getClassFinderBean() {
        return new ClassFinderBean(this);
    }
    
    public void clear() {
        setFile(null);
        setName("New fault injection plan");
        setBaseDirectory(".");
        setErrorGenerators(new Vector<ErrorGenerator>());
        setConfigurationFiles(new Vector<ConfigurationFile>());
        setJars(new Vector<String>());

        setPluginClass("");
        setParams(new Vector<Parameter>());
        
    }
    
}
