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

import conferr.faultdesc.Fault;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Document;

/**
 * Abstract template that provides standard variable substitution in parameters.
 */

public abstract class AbstractFaultTemplate implements FaultTemplate {

    public FaultScenarioEnumeration faults(final Map<String, Document> configs, long seed, final FaultScenarioSet scenario) {
       return new FaultScenarioEnumeration() {

            Enumeration<Fault> e = getDescription(configs, scenario).faults();
            
            public void notifyInjectionResult(FaultInjectionResult result) {}

            public void close() {}

            public boolean hasMoreElements() {
                return e.hasMoreElements();
            }

            public FaultScenario nextElement() {
                if (e.hasMoreElements())
                    return getFaultScenario(e.nextElement(), configs, scenario);
                else
                    return null;
            }
           
        };
    }

    public HashSet<String> getRequiredVariables(FaultScenarioSet scenario) {                    
        
        HashSet<String> requiredVars = new HashSet<String>();
        
        for (Parameter p : scenario.getParams()) {
            
            Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
            
            Matcher m = pattern.matcher(p.getValue());
            
            while (m.find()) {
                requiredVars.add(m.group(1));
            }
            
        }
        
        for (FaultScenarioSet set : scenario.getChildren()) {
            requiredVars.addAll(set.getFaultTemplateInstance().getRequiredVariables(set));
        }
        
        return requiredVars;    
        
    }

    public Vector<Parameter> getDefaultParameters() {
        return new Vector<Parameter>();
    }

    
    
}
