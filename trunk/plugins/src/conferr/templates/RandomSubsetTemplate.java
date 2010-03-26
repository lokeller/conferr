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
import conferr.FaultInjectionResult;
import conferr.FaultScenario;
import conferr.FaultScenarioEnumeration;
import conferr.Parameter;
import conferr.FaultScenarioSet;
import conferr.faultdesc.AbstractValueSet;
import conferr.faultdesc.ElementOfInterval;
import conferr.faultdesc.Fault;
import conferr.faultdesc.FaultSpace;
import conferr.faultdesc.Value;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import org.jdom.Document;


public class RandomSubsetTemplate extends AbstractFaultTemplate {

    
    public static final String sizeString = "size";
            
    @Override
    public Vector<Parameter> getDefaultParameters() {
        
        Vector<Parameter> parameters = new Vector<Parameter>();
                
        parameters.add(new Parameter(sizeString, "", Parameter.INTEGER));
            
        return parameters;
        
    }
        
    private int getSize( FaultScenarioSet scenario ) {        
        int size;
        try  {
            size = Integer.parseInt(scenario.getParameterValue(sizeString));
        } catch (NumberFormatException ex) {
            size = 0;
        }

        return size;
    }
    
    public int getMaxChildren() {
        return 1;
    }

    public String getChildName(int pos) {
        return "Set";
    }
        
    public FaultScenarioEnumeration faults(final Map<String,Document> config, final long seed, final FaultScenarioSet scenario) {         
        
        FaultScenarioEnumeration e = new FaultScenarioEnumeration() {

            
            FaultScenarioSet set = scenario.getChildren().get(0);            
            
            FaultSpace description = scenario.getChildren().get(0).getFaultTemplateInstance().getDescription(config, set);
            
            int count = 0;
            
            public boolean hasMoreElements() {
                return count < getSize(scenario);
            }

            public FaultScenario nextElement() {                
                if (count < getSize(scenario)) {
                    Random r = new Random(count);
                    
                    count++;
                    return set.getFaultTemplateInstance().getFaultScenario(randomlySelectFault(description, r), config, set);
                } else {
                    return null;
                }
                                
            }            

            public void notifyInjectionResult(FaultInjectionResult result) {                
            }

            public void close() {
                
            }
        };
        
        return e;
    }
    
    public Fault randomlySelectFault( FaultSpace space, Random rnd) {

        long coeffs = 0;

        for ( Map.Entry<AbstractValueSet, FaultSpace> entry : space.getSubspaces().entrySet() ) {                                        
            if (entry.getValue() != null) {
                coeffs += entry.getKey().size() * entry.getValue().numberOfFaults();
            } else {
                coeffs += entry.getKey().size();
            }
            
        }                

        rnd.nextDouble();
        
        long val = (long) (rnd.nextDouble() * coeffs);
        
        
        for ( Map.Entry<AbstractValueSet, FaultSpace> entry : space.getSubspaces().entrySet() ) {                                        
            
            if (entry.getValue() != null) {
                val  -= entry.getKey().size() * entry.getValue().numberOfFaults();
            } else {
                val -= entry.getKey().size();
            }
            
            if (val <= 0) {

                AbstractValueSet set = entry.getKey();
                long el = (long) (set.size() * rnd.nextDouble());                            
                Value val1 = set.get(el);
                
                Fault f;
                
                if ( entry.getValue() == null) {
                    f = new Fault(new HashMap<FaultSpace, Value>());
                } else {                            
                    f = randomlySelectFault(entry.getValue(), rnd);                                                        
                }

                f.getValues().put(space, val1);
                
                return f;

            }
        }

        throw new RuntimeException("Size of description changed while picking a random value");

    }
    

    public FaultSpace getDescription(Map<String, Document> configs, FaultScenarioSet scenario) {
        
        FaultSpace count = new FaultSpace("count_" + scenario.getId());
        
        int max = getSize(scenario);
        
        count.addSubspace(new ElementOfInterval(0, max - 1), null);
        
        return count;
        
    }

    public FaultScenario getFaultScenario(Fault fault, Map<String, Document> configs, FaultScenarioSet scenario) {        
        
        Enumeration<FaultScenario> e = faults(configs, 0, scenario);
        
        int num = (Integer) fault.getObjectByName("count_" + scenario.getId()) - 1;
        
        FaultScenarioSet set = scenario.getChildren().get(0);
        FaultSpace description = set.getFaultTemplateInstance().getDescription(configs, set);
        Random r = new Random(num);
        
        return set.getFaultTemplateInstance().getFaultScenario(randomlySelectFault(description, r), configs, set);
         
    }

    
    
}
