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

import conferr.FaultScenarioSet;
import conferr.Parameter;
import conferr.faultdesc.ElementsSet;
import conferr.faultdesc.Fault;
import conferr.faultdesc.FaultSpace;
import conferr.faultdesc.Value;
import conferr.faultdesc.ValueSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;


public abstract class ChangeContentTemplate extends MultidocSimpleTemplate {
    public static final String targetParam = "target";

    
    public static final String targetString = "target";
    private static final String documentParam = "document";
            
    @Override
    public Vector<Parameter> getDefaultParameters() {
        
        Vector<Parameter> parameters = new Vector<Parameter>();
                
        parameters.add(new Parameter(targetString, "", Parameter.XPATH_EXPRESSION));       
        
        return parameters;
        
    }
    
    @Override
    protected Map<String, Document> applyToConfig(Map<String, Document> docs, Fault fault, FaultScenarioSet scenario) {

        String configName = (String) fault.getObjectByName(documentParam);
        Document config = docs.get(configName);
        long seed = new Random().nextLong();
        Object targetO = fault.getObjectByName(targetParam);
        
        Document copy = new Document((Element) config.getRootElement().clone());          

        Object target = getCorrespondingContent(copy, targetO);

        if (target instanceof Element) {
            Element e = (Element) target;

            e.setText(modifyString(e.getText(), seed, scenario));
        } else if (target instanceof Attribute) {
            Attribute a = (Attribute) target;

            a.setValue(modifyString(a.getValue(), seed, scenario));
        }  else if (target instanceof Text ) {
            Text t = (Text) target;

            t.setText(modifyString(t.getText(), seed, scenario));

        } else {
            return null;
        }

        HashMap<String, Document> docs2 = new HashMap<String, Document>(docs);        
        docs2.put(configName, copy);        
        return docs2;

    }

    public FaultSpace getDescription(Map<String, Document> configs, FaultScenarioSet scenario) {
        
        try {

            FaultSpace configsNames = new FaultSpace(documentParam);

            XPath p = XPath.newInstance(scenario.getParameterValue(targetString));

            for (Map.Entry<String, Document> entry : configs.entrySet()) {
                
                FaultSpace targets = new FaultSpace(targetParam);
                
                targets.addSubspace(new ElementsSet(p.selectNodes(entry.getValue())), null) ;
                            
                configsNames.addSubspace(new ValueSet(new Value(entry.getKey())), targets);
            }
            
            return configsNames;
            
        } catch (JDOMException ex) {
            throw new RuntimeException(ex);
        }
        
    }


    protected abstract String modifyString(String text, long seed, FaultScenarioSet scenario) ;

    public int getMaxChildren() {
        return 0;
    }

    public String getChildName(int pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
