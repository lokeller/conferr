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
import java.util.HashMap;
import javax.xml.transform.TransformerException;
import org.jdom.Document;
import org.jdom.Element;

/**
 * 
 * This class represent an error generator module, it stores all the fault
 * scenario set and the transforms required to inject the fault in the SUT
 * configuration files.
 * 
 */
public class ErrorGenerator extends ObservableBean {

    private String name = "New error generator";
    
    private FaultScenarioSet scenario;

    private HashMap<ConfigurationFile, ConfigurationTransform> transforms = new HashMap<ConfigurationFile, ConfigurationTransform>();
           
    private FaultInjectionPlan plan;

    public FaultInjectionPlan getPlan() {
        return plan;
    }
    
    public ErrorGenerator() {
        
    }
    
    public ErrorGenerator(FaultInjectionPlan plan) {
        this.plan = plan;        
        this.scenario = new FaultScenarioSet(plan, null);
        scenario.setPluginClass("conferr.templates.RandomSubsetTemplate");        

    }
         
    public String getName() {
        return name;
    }

    public HashMap<String, Document> getTransformedConfigurationFiles() throws FileNotFoundException, TransformerException, ImpossibleConfigurationException {
        
        HashMap<String, Document> configs = new HashMap<String, Document>();
        
        for (ConfigurationFile file : plan.getConfigurationFiles()) {
                
            ConfigurationTransform c = getConfigurationTransform(file);
                
            Transform f = c.getTransformInstance();

            if (f != null) {
                configs.put(file.getName(), f.filter(file.getDocument(), c ));
            } else {
                configs.put(file.getName(), file.getDocument());
            }

            if (configs.get(file.getName()) == null) {
                new RuntimeException("Unable to parse " + file.getName());
            }
        }
        
        return configs;
        
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        pcs.firePropertyChange("name", old, this.name);
    }

    public FaultScenarioSet getFaultScenarioSet() {
        return scenario;
    }

    public void setFaultScenarioSet(FaultScenarioSet scenario) {
        FaultScenarioSet old = this.scenario;
        this.scenario = scenario;
        pcs.firePropertyChange("faultScenarioSet", old, scenario);
    }


    @Override
    public String toString() {
        return "Error generator: " + name;
    }
    
    public ConfigurationTransform getConfigurationTransform(ConfigurationFile file) {
        
        if (!transforms.containsKey(file)) {
            transforms.put(file, new ConfigurationTransform(plan));
        }
        return transforms.get(file);
    }

    public HashMap<ConfigurationFile, ConfigurationTransform> getConfigurationTransforms() {
        return transforms;
    }

    public void setConfigurationTransforms(HashMap<ConfigurationFile, ConfigurationTransform> filters) {
        this.transforms = filters;
    }
    
    public Element toElement() {
            Element generatorEl = new Element("error-generator");

            generatorEl.setAttribute("name", plan.stringToXMLString(getName()));

            generatorEl.addContent(getFaultScenarioSet().toElement());
            return generatorEl;
    }
    
}
