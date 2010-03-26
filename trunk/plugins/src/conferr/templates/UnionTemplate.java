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
import conferr.FaultTemplate;
import conferr.FaultScenario;
import conferr.FaultScenarioEnumeration;
import conferr.Parameter;
import conferr.FaultScenarioSet;
import conferr.faultdesc.Fault;
import conferr.faultdesc.FaultSpace;
import conferr.faultdesc.Value;
import conferr.faultdesc.ValueSet;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import org.jdom.Document;


public class UnionTemplate extends AbstractFaultTemplate {

    public Vector<Parameter> getDefaultParameters() {
        return new Vector<Parameter>();
    }

    public int getMaxChildren() {
        return -1;
    }

    public String getChildName(int pos) {
        return "" + pos;
    }
    
    
    public FaultScenarioEnumeration faults(final Map<String,Document> config, final long seed, FaultScenarioSet scenario) {   
       return new UnionEnum(config, seed, scenario);
    }

    private class UnionEnum implements FaultScenarioEnumeration {
        
        private Random random;
        private Map<String,Document> doc;
        
        private Vector<Enumeration<FaultScenario>> enums = new Vector<Enumeration<FaultScenario>>();
        
        public UnionEnum(Map<String,Document> doc, long seed, FaultScenarioSet scenario) {
            this.random = new Random(seed);
            this.doc = doc;
            
            for (FaultScenarioSet s : scenario.getChildren()) {
                FaultTemplate a = s.getFaultTemplateInstance();
            
                if (a == null) continue;
                Enumeration<FaultScenario> e = a.faults(doc, random.nextLong(), s);                
                if (e.hasMoreElements()) enums.add(e);
            }
        }                

        public boolean hasMoreElements() {
            return enums.size() > 0;
        }

        public FaultScenario nextElement() {

            Enumeration<FaultScenario> e = enums.get((int) 0);
            FaultScenario n = e.nextElement();
            
            if (!e.hasMoreElements()) enums.remove(e);

            return n;
        }

        public void notifyInjectionResult(FaultInjectionResult result) {           
        }

        public void close() {            
        }

    }

    public FaultSpace getDescription(Map<String, Document> configs, FaultScenarioSet scenario) {
        FaultSpace union = new FaultSpace("child_" + scenario.getId());
        
        for (int i = 0 ; i < scenario.getChildren().size() ; i ++ ) {
            
            union.addSubspace(
                    new ValueSet(new Value(i + "", i)), 
                    scenario.getChildren().get(i).getFaultTemplateInstance().getDescription(configs, scenario.getChildren().get(i)));
            
        }
        
        return union;
    }

    public FaultScenario getFaultScenario(Fault fault, Map<String, Document> configs, FaultScenarioSet scenario) {
        
        int id = (Integer) fault.getObjectByName("child_" + scenario.getId());
        FaultScenarioSet subScenario = scenario.getChildren().get(id);
        return subScenario.getFaultTemplateInstance().getFaultScenario(fault, configs, subScenario);
        
    }
    

}
