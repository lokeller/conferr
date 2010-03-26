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
import conferr.FaultScenarioSet;
import conferr.Parameter;
import conferr.faultdesc.ElementsSet;
import conferr.faultdesc.Fault;
import conferr.faultdesc.FaultSpace;
import conferr.faultdesc.Value;
import conferr.faultdesc.ValueSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;


public class ForEachNode extends AbstractFaultTemplate {

    @Override
    public int getMaxChildren() {
        return 1;
    }

    @Override
    public String getChildName(int pos) {
        return "Template";
    }

    private String getXPath(Document d , Object o ) {
        if ( o instanceof Element ) {
            return getXPath(d, ((Element) o).getParent()) + "/*[position()=" + (((Element) o).getParent().indexOf((Element) o) + 1) + "]";
        } else if (o instanceof Attribute) {
            return getXPath(d, ((Attribute) o).getParent()) + "/@*[position()=" + (((Attribute) o).getParent().getAttributes().indexOf((Attribute) o) + 1) + "]";
        } else if ( o instanceof Text ) {
            return getXPath(d, ((Text) o).getParent()) + "/text()[position()=" + (((Text) o).getParent().indexOf((Text) o) + 1) + "]";
        } else if ( o instanceof Document) {
            return "";
        } else {
            throw new RuntimeException("Unsupported type " + o);
        }
    } 
    
    private FaultScenarioSet getChildScenario(FaultScenarioSet mine, String value) {
        
        return mine.getChildren().get(0).substituteVariable(mine.getParameterValue("targetVariable"), value);
        
    }
    
    public FaultSpace getDescription(Map<String, Document> configs, FaultScenarioSet scenario) {

        try {
            XPath xpath = XPath.newInstance(scenario.getParameterValue("nodeset"));

            FaultSpace document = new FaultSpace("document" + scenario.getId());

            for (Map.Entry<String, Document> entry : configs.entrySet()) {
                    
                    FaultSpace space = new FaultSpace("node" + scenario.getId());

                    List l = xpath.selectNodes(entry.getValue());

                    if (l.size() > 0 ) {
                    
                        for (Object o : l) {                            
                            
                            FaultScenarioSet child = getChildScenario(scenario, getXPath(entry.getValue(), o));
                            space.addSubspace(new ElementsSet(o), child.getFaultTemplateInstance().getDescription(configs, child));
                        }
                        
                        document.addSubspace(new ValueSet(new Value(entry.getKey())), space);

                    }

            }
            
            return document;
            
        } catch (JDOMException ex) {
            throw new RuntimeException(ex);
        }
        
    }

    public FaultScenario getFaultScenario(Fault fault, Map<String, Document> configs, FaultScenarioSet scenario) {
        
        Document d = configs.get(fault.getObjectByName("document" + scenario.getId()));        
        Object o = fault.getObjectByName("node" + scenario.getId());
        
        FaultScenarioSet child = getChildScenario(scenario, getXPath(d, o));
        
        return child.getFaultTemplateInstance().getFaultScenario(fault, configs, child);
        
    }

    public Vector<Parameter> getDefaultParameters() {
        Vector<Parameter> parameters = new Vector<Parameter>();
        
        parameters.add(new Parameter("targetVariable", "", Parameter.STRING));
        parameters.add(new Parameter("nodeset", "", Parameter.XPATH_EXPRESSION));
        
        return parameters;
    }

    @Override
    public HashSet<String> getRequiredVariables(FaultScenarioSet scenario) {
    
        HashSet<String> vars = super.getRequiredVariables(scenario);
        vars.remove(scenario.getParameterValue("targetVariable"));        
        
        return vars;
    }
    
    
}
