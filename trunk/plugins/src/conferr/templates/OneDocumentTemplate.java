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
package conferr.templates;

import conferr.AbstractFaultTemplate;
import conferr.FaultScenario;
import conferr.FaultScenarioEnumeration;
import conferr.FaultScenarioSet;
import conferr.Parameter;
import conferr.faultdesc.Fault;
import conferr.faultdesc.FaultSpace;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.jdom.Document;


public class OneDocumentTemplate extends AbstractFaultTemplate {

    public static final String configString = "config";
            
    @Override
    public Vector<Parameter> getDefaultParameters() {
        
        Vector<Parameter> parameters = new Vector<Parameter>();
                
        parameters.add(new Parameter(configString, "", Parameter.CONFIGURATION_FILE));
            
        return parameters;
        
    }

    public int getMaxChildren() {
        return 1;
    }

    public String getChildName(int pos) {
        return "Set";
    }

    public FaultScenarioEnumeration faults(Map<String, Document> configs, long seed, FaultScenarioSet scenario) {
                
         FaultScenarioSet child = scenario.getChildren().get(0);

         return child.getFaultTemplateInstance().faults(getReducedDocuments(configs, scenario), seed, child );
        
    }

    public FaultSpace getDescription(Map<String, Document> configs, FaultScenarioSet scenario) {
        
        FaultScenarioSet child = scenario.getChildren().get(0);
        return child.getFaultTemplateInstance().getDescription(getReducedDocuments(configs, scenario), scenario);
        
    }

    public FaultScenario getFaultScenario(Fault fault, Map<String, Document> configs, FaultScenarioSet scenario) {
        
        FaultScenarioSet child = scenario.getChildren().get(0);
        return child.getFaultTemplateInstance().getFaultScenario(fault, getReducedDocuments(configs, scenario), scenario);
    }

    private HashMap<String, Document> getReducedDocuments(Map<String, Document> configs, FaultScenarioSet scenario) {

        String name = scenario.getParameterValue(configString);

        HashMap<String, Document> configs2 = new HashMap<String, Document>();
        
        if (configs.keySet().contains(name)) {
            configs2.put(name, configs.get(name));            
        } 
        
        return configs2;
    }

    
    
    
}
