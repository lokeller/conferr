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
import conferr.ErrorGenerator;
import conferr.FaultScenario;
import conferr.FaultScenarioEnumeration;
import conferr.FaultScenarioSet;
import conferr.Parameter;
import conferr.faultdesc.Fault;
import conferr.faultdesc.FaultSpace;
import java.util.Map;
import java.util.Vector;
import org.jdom.Document;


public class ErrorGeneratorTemplate extends AbstractFaultTemplate {
    public static final String generatorString = "error-generator";
    public static final String substitutionsString = "substitutions";

    public int getMaxChildren() {
        return 0;
    }

    public String getChildName(int pos) {
        throw new UnsupportedOperationException("No children supported");
    }

    private FaultScenarioSet getTemplateScenarioSet(FaultScenarioSet scenario) {
        
        FaultScenarioSet cset = (FaultScenarioSet) getErrorGenerator(scenario).getFaultScenarioSet();
        
        for (Variable v : getVariables(scenario.getParameterValue(substitutionsString))) {
            cset = cset.substituteVariable(v.name, v.value);
        }
        
        return cset;
        
    }
    
    private class Variable {
        
        private String name;
        private String value;

        public Variable(String name, String value) {
            this.name = name;
            this.value = value;
        }                      
        
    }
    
    private Variable[] getVariables(String param) {
        String[] v = param.split(";");
        
        Variable[] vv = new Variable[v.length];
        
        for ( int i = 0 ; i < v.length ; i++) {
            vv[i] = new Variable(param.substring(0, param.indexOf('=') ), 
                    param.substring(param.indexOf('=') + 1 ));
        }
        return vv;
        
    }    
    
    private ErrorGenerator getErrorGenerator(FaultScenarioSet scenario) {
        
        String generatorName = scenario.getParameterValue(generatorString);
        
        for (ErrorGenerator generator : scenario.getPlan().getErrorGenerators()) {
            if ( generator.getName().equals(generatorName)) return generator;
        }
        throw new RuntimeException("Unable to find error generator " + generatorName);
    }
    
    public FaultScenarioEnumeration faults(Map<String, Document> configs, long seed, FaultScenarioSet scenario) {        
        
        FaultScenarioSet substitutedScenarioSet = getTemplateScenarioSet(scenario);        
        
        return substitutedScenarioSet.getFaultTemplateInstance().faults(configs, seed, substitutedScenarioSet);
        
    }

    public FaultSpace getDescription(Map<String, Document> configs, FaultScenarioSet scenario) {             
                
        FaultScenarioSet substitutedScenarioSet = getTemplateScenarioSet(scenario);
        
        return substitutedScenarioSet.getFaultTemplateInstance().getDescription(configs, substitutedScenarioSet);
        
    }

    public FaultScenario getFaultScenario(Fault fault, Map<String, Document> configs, FaultScenarioSet scenario) {
        FaultScenarioSet substitutedScenarioSet = getTemplateScenarioSet(scenario);
        
        return substitutedScenarioSet.getFaultTemplateInstance().getFaultScenario(fault, configs, substitutedScenarioSet);
    }

    public Vector<Parameter> getDefaultParameters() {
        Vector<Parameter> parameters = new Vector<Parameter>();
                
        parameters.add(new Parameter(generatorString, "", Parameter.STRING));
        parameters.add(new Parameter(substitutionsString, "", Parameter.STRING));
        
        return parameters;
    }

    
    
}
