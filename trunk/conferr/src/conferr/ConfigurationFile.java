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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.jdom.Document;
import org.jdom.Element;


/**
 * 
 * This class stores all the information required to use a given configuration
 * file for a fault injection.
 * 
 */

public class ConfigurationFile extends DefaultPluginContainer {

    private FaultInjectionPlan plan;
    
    private String name = "New configuration file";
    private String input = "";    
    private String output = "";

    public ConfigurationFile(FaultInjectionPlan plan) {
        this.plan = plan;
    }

    
    public FaultInjectionPlan getPlan() {
        return plan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        pcs.firePropertyChange("name", old, name);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        String old = this.input;
        this.input = input;
        pcs.firePropertyChange("input", old, input);
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        String old = this.output;
        this.output = output;
        pcs.firePropertyChange("output", old, this.output);
    }
    
    @Override
    public String toString() {
        return "Configuration file: " + name;
    }
    
    public Document getDocument() throws FileNotFoundException, TransformerException {       

        Handler h = getHandlerInstance();

        if (h == null) {
            return null;
        }
        
        Document d = h.parseConfiguration(new FileReader(plan.getAbsolutePath(getInput())), this);                        

        return d;
        
    }
    
    public Handler getHandlerInstance() {            
            return (Handler) getPluginInstance();
    }
    
    
    public Element toElement() {
        
            Element configurationFileElement = new Element("config-file");

            configurationFileElement.setAttribute("name", plan.stringToXMLString(getName()));
            configurationFileElement.setAttribute("input", plan.stringToXMLString(getInput()));
            configurationFileElement.setAttribute("output", plan.stringToXMLString(getOutput()));

            Element handlerElement = new Element("handler");
            handlerElement.setAttribute("class-name", plan.stringToXMLString(getPluginClass()));

            for (Parameter p : getParams()) {
                Element pElement = new Element("param");
                pElement.setAttribute("name", plan.stringToXMLString(p.getName()));
                pElement.setAttribute("value", plan.stringToXMLString(p.getValue()));
                handlerElement.addContent(pElement);
            }

            configurationFileElement.addContent(handlerElement);
            return configurationFileElement;
    }
    
    public static ConfigurationFile fromElement(Element e, FaultInjectionPlan plan) {                   

            ConfigurationFile file = new ConfigurationFile(plan);

            file.setName(e.getAttributeValue("name"));
            file.setInput(e.getAttributeValue("input"));
            file.setOutput(e.getAttributeValue("output"));

            Vector<Parameter> params = new Vector<Parameter>();

            file.setPluginClass(e.getChild("handler").getAttributeValue("class-name"));
            
            Plugin plugin = file.getPluginInstance();
            
            if ( plugin != null) {
            
                for (Object o1 : e.getChild("handler").getChildren("param")) {
                    Element e1 = (Element) o1;
                    
                    String name = e1.getAttributeValue("name");
                    String value = e1.getAttributeValue("value");
                    String type = "unknown";
                    
                    for (Parameter pp : plugin.getDefaultParameters()) {
                        if (pp.getName().equals(name)) {
                            type = pp.getType();
                            break;
                        }
                    }                    
                    
                    params.add(new Parameter(name, value, type));
                    
                }
            }

            file.setParams(params);

            return file;
            
    }

    @Override
    public String getPluginInterface() {
        return "conferr.Handler";
    }

    @Override
    public ClassFinderBean getClassFinderBean() {
        return new ClassFinderBean(plan);
    }
    
}
