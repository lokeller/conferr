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
import java.util.Map;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;


public abstract class MultidocSimpleTemplate extends AbstractFaultTemplate {


    protected Object getCorrespondingContent(Document copy , Object o) {
        
        if ( o instanceof Content) {

            Content c = (Content) o;
            Vector<Integer> pos = new Vector<Integer>();

            Content curr = c;
            while ( true ){
                pos.insertElementAt(curr.getParent().indexOf(curr), 0);
                if ( curr.getParent() instanceof Content) {
                    curr = (Content) curr.getParent();
                } else {
                    break;
                }

            }

            Content ret = copy.getContent(pos.remove(0));
            while (pos.size() > 0) {
                ret = ((Parent) ret).getContent(pos.remove(0));
            }
            
            return  ret;
            
        } else if (o instanceof Attribute ) {
            
            Attribute a = (Attribute) o;
            
            Element e = (Element) getCorrespondingContent(copy, a.getParent());
            
            return e.getAttribute(a.getName());                        
             
        } else {
            throw new RuntimeException("Unsupported object type" + o.getClass()) ;
        }         
        
    }
    
    protected String getDocumentName(Map<String, Document> docs, Object o) {
        
        if ( o instanceof Content) {
            Content c = (Content) o;
            if (c.getDocument() == null) return null;

            for (Map.Entry<String, Document> entry : docs.entrySet()) {
                if (c.getDocument().equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
            
            throw new RuntimeException("Document name not found");
            
        } else if (o instanceof Attribute ) {
            return getDocumentName(docs, ((Attribute) o).getParent());
        } else {
            throw new RuntimeException("Unsupported type " + o.getClass());
        }
        
    }
    
    class FaultScenarioImpl implements FaultScenario {

        private Map<String,Document> docs;
        private FaultScenarioSet scenario;
        private Fault a;

        public FaultScenarioImpl(Fault a, Map<String,Document> docs, FaultScenarioSet scenario) {
                this.a = a;
                this.docs = docs;
                this.scenario = scenario;                            
        }            

        public Map<String,Document> getDocument() {                                
            return applyToConfig(docs, a, scenario);
        }

        public FaultTemplate getTemplate() {
            return MultidocSimpleTemplate.this;
        }                       

        public String getDescription() {
            return MultidocSimpleTemplate.this.getClass().getName();
        }
    }
    
    protected abstract Map<String,Document> applyToConfig(Map<String,Document> docs, Fault fault, FaultScenarioSet scenario);

    @Override
    public FaultScenario getFaultScenario(Fault fault, Map<String, Document> configs, FaultScenarioSet scenario) {
        
        return new FaultScenarioImpl(fault, configs, scenario);
        
    }
    
    
    
    
}
