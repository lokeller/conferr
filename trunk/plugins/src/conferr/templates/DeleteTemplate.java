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

import conferr.*;
import conferr.faultdesc.ElementsSet;
import conferr.faultdesc.Fault;
import conferr.faultdesc.FaultSpace;
import conferr.faultdesc.Value;
import conferr.faultdesc.ValueSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

public class DeleteTemplate extends MultidocSimpleTemplate {
    
    
    private static final String targetString = "target";
    
    private static final String documentParam = "document";
    private static final String targetParam = "target";    
    
    
    @Override
    public Vector<Parameter> getDefaultParameters() {
        
        Vector<Parameter> parameters = new Vector<Parameter>();
                
        parameters.add(new Parameter(targetString, "", Parameter.XPATH_EXPRESSION));
        
        return parameters;
        
    }
    
    @Override
    public FaultSpace getDescription(Map<String, Document> docs, FaultScenarioSet scenario) {
        
        try {
            FaultSpace documents = new FaultSpace(documentParam);                     

            for (Map.Entry<String, Document> entry : docs.entrySet()) {
                XPath xpath = XPath.newInstance(scenario.getParameterValue(targetString));

                List l = xpath.selectNodes(entry.getValue());
                if (l.size() > 0) {

                    FaultSpace targets = new FaultSpace(targetParam);
                    
                    targets.addSubspace(new ElementsSet(l), null);
                    
                    documents.addSubspace(new ValueSet(new Value(entry.getKey())), targets);

                }
            }

            return documents;        
        } catch (JDOMException ex) {
            throw new RuntimeException(ex);
        }
        
    }
    
    @Override
    protected Map<String, Document> applyToConfig(Map<String, Document> docs, Fault a, FaultScenarioSet scenario) {
   
        String name = (String) a.getObjectByName(documentParam);
        Document doc = new Document(docs.get(name).cloneContent());

        Object o = getCorrespondingContent(doc, a.getObjectByName(targetParam));
        
        if (o instanceof Content) {   
            ((Content) o).detach();
        } else if (o instanceof Attribute) {
            ((Attribute) o).detach();
        } else {
            throw new RuntimeException("Unsupported destination type " + o.getClass() );
        } 

        HashMap<String, Document> ret = new HashMap<String, Document>();

        ret.putAll(docs);
        ret.put(name, doc);


        return ret;

    }

    public int getMaxChildren() {
        return 0;
    }

    public String getChildName(int pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
