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
import conferr.FaultTemplate;
import conferr.FaultScenario;
import conferr.FaultScenarioSet;
import conferr.faultdesc.Fault;
import conferr.faultdesc.FaultSpace;
import conferr.faultdesc.Value;
import conferr.faultdesc.ValueSet;
import java.util.Map;
import org.jdom.Document;


public class IdentityTemplate extends AbstractFaultTemplate {


    private FaultScenario getFault(final Map<String,Document> config) {                 
        
         FaultScenario identity = new FaultScenario() {

            public Map<String, Document> getDocument() {
                return config;
            }

            public FaultTemplate getTemplate() {
                return IdentityTemplate.this;
            }

            public String getDescription() {
                return "No fault";
            }
        };
        
        return identity;
    }

    @Override
    public FaultSpace getDescription(Map<String, Document> configs, FaultScenarioSet scenario) {
        FaultSpace s = new FaultSpace("identity");
        s.addSubspace(new ValueSet(new Value("identity")), null);
        return s;
    }

    
    public FaultScenario getFaultScenario(Fault fault, Map<String, Document> configs, FaultScenarioSet scenario) {
        return getFault(configs);
    }

    public int getMaxChildren() {
        return 0;
    }

    public String getChildName(int pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
