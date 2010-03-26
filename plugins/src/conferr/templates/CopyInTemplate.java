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
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;


public class CopyInTemplate extends MultidocSimpleTemplate {
     
    static final private String targetDocumentParam = "targetDocument";
    static final private String destinationDocumentParam = "destinationDocument";
    static final private String targetParam = "target";    
    static final private String destinationParam = "destination";    
            
    
    public static final String moveString = "move";
    public static final String afterString = "after";
    public static final String targetString = "target";
    public static final String destinationString = "destination";
    public static final String replaceString = "replace";
    public static final String attributesAsTextString = "attributes as text";
            
    @Override
    public Vector<Parameter> getDefaultParameters() {
        
        Vector<Parameter> parameters = new Vector<Parameter>();
                
        parameters.add(new Parameter(targetString, "", Parameter.XPATH_EXPRESSION));
        parameters.add(new Parameter(destinationString, "", Parameter.XPATH_EXPRESSION));
        parameters.add(new Parameter(replaceString, "", Parameter.BOOLEAN));
        parameters.add(new Parameter(attributesAsTextString, "", Parameter.BOOLEAN));
        parameters.add(new Parameter(moveString, "", Parameter.BOOLEAN));
        parameters.add(new Parameter(afterString, "", Parameter.BOOLEAN));
        
        return parameters;
        
    }
    
    public boolean getReplace(FaultScenarioSet scenario) {
        try {
            return Boolean.parseBoolean(scenario.getParameterValue(replaceString));
        } catch ( IllegalArgumentException ex ) {
            return false;
        }
    }
    
    public boolean getMove(FaultScenarioSet scenario) {
        try {
            return Boolean.parseBoolean(scenario.getParameterValue(moveString));
        } catch ( IllegalArgumentException ex ) {
            return false;
        }
    } 
    
    public boolean getAfter(FaultScenarioSet scenario) {
        try {
            return Boolean.parseBoolean(scenario.getParameterValue(afterString));
        } catch ( IllegalArgumentException ex ) {
            return false;
        }
    }    
    
    public boolean getAttributesAsText(FaultScenarioSet scenario) {
        try {
            return Boolean.parseBoolean(scenario.getParameterValue(attributesAsTextString));
        } catch ( IllegalArgumentException ex ) {
            return false;
        }
    }
    
   @Override
   public FaultSpace getDescription(Map<String, Document> docs, FaultScenarioSet scenario) {
        
        try {
            FaultSpace targetDocuments = new FaultSpace(targetDocumentParam);          

            for (Map.Entry<String, Document> entry : docs.entrySet()) {
                
                XPath xpath = XPath.newInstance(scenario.getParameterValue(targetString));

                List l = xpath.selectNodes(entry.getValue());
                if (l.size() > 0) {

                    FaultSpace targets = new FaultSpace(targetParam);                                       

                    for ( Object target : l) {

                        FaultSpace destinationDocuments = new FaultSpace(destinationDocumentParam);
                        
                        for ( Map.Entry<String, Document> entry2 : docs.entrySet()) { 
                            
                            XPath destXpath = XPath.newInstance(scenario.getParameterValue(destinationString));
                            
                            destXpath.setVariable("target", target);
                            
                            List dests = destXpath.selectNodes(entry2.getValue());

                            if ( getMove(scenario))
                                dests.remove(target);
                            
                            if (dests.size() > 0) {                                                            
                                
                                FaultSpace destinations = new FaultSpace(destinationParam);
                                
                                destinations.addSubspace(new ElementsSet(dests), null);
                                
                                destinationDocuments.addSubspace(new ValueSet(new Value(entry2.getKey())), destinations);
                                                                                                
                            }                                                        
                            
                            
                        }
                        
                        if (destinationDocuments.numberOfFaults() > 0)
                            targets.addSubspace(new ElementsSet(target), destinationDocuments);
                                                
                    }
                    
                    if ( targets.numberOfFaults() > 0 )
                        targetDocuments.addSubspace(new ValueSet(new Value(entry.getKey())), targets);
                    
                    
                }
            }
            
            return targetDocuments;        
        } catch (JDOMException ex) {
            throw new RuntimeException(ex);
        }
        
    }
    
   @Override
    protected Map<String, Document> applyToConfig(Map<String, Document> docs, Fault f, FaultScenarioSet scenario) {

            String name = (String) f.getObjectByName(targetDocumentParam);
            Document doc = new Document(docs.get(name).cloneContent());

            Object target = getCorrespondingContent(doc, f.getObjectByName(targetParam));         
            
            String nameDest = (String) f.getObjectByName(destinationDocumentParam);
            
            /* use same copy of the input document if target and dest documents 
             * are the same */
            Document docDest;
            if (nameDest.equals(name)) {
                docDest = doc;
            } else {
                docDest = new Document(docs.get(nameDest).cloneContent());
            }            

            Object dest = getCorrespondingContent(docDest, f.getObjectByName(destinationParam));            
                                                                    
            if (getAttributesAsText(scenario) && target instanceof Attribute) {
                
                if (getMove(scenario))
                    ((Attribute) target).detach();
                
                target = new Text(((Attribute)(target)).getValue());
            } 
            
            if (getReplace(scenario) && getAfter(scenario)) 
                throw new RuntimeException("Replace and after cannot be true at the same time");
            
            if ( target instanceof Content){

                Content tc = (Content) target;

                if ( getMove(scenario)) {
                    tc.detach();
                }
                    

                Content c = (Content) ((Content) target).clone();

                if (getAfter(scenario) && dest instanceof Content) {
                    Content c1 = (Content) dest;
                    
                    int pos = c1.getParent().indexOf(c1);
                    c1.getParent().getContent().add(pos + 1, c); 
                } else if ( dest instanceof Element) {
                    Element e = (Element) dest;
                                    
                    if (getReplace(scenario)) {
                        e.setContent(c);    
                    } else {
                        e.addContent(c);    
                    }

                } else if (dest instanceof Attribute ) {
                    if (!getReplace(scenario) || getAfter(scenario))
                        throw new RuntimeException("Unable to attach object of type " + tc.getClass() + " to an attribute");
                       
                    Attribute a = (Attribute) dest;                   

                    if (c instanceof Text) {                                         
                        a.setValue(((Text) c).getText());
                    } else {
                        throw new RuntimeException("Unable to convert " + c.getClass() + " to a value suitable for an attribute");
                    }

                } else {
                    throw new RuntimeException("Unsupported operation (" + target.getClass() + " -> " + dest.getClass());
                }
                
            } else if ( target instanceof Attribute) {

                Attribute ta = (Attribute) target;

                if (getMove(scenario))
                    ta.detach();

                Attribute a = (Attribute) ((Attribute) target).clone();
                
                if (getAfter(scenario) && dest instanceof Attribute) {
                    Attribute a1 = (Attribute) dest;
                    
                    int pos = a1.getParent().getAttributes().indexOf(a1);
                    a1.getParent().getAttributes().add(pos + 1, a); 
                } else if ( !getAfter(scenario) && dest instanceof Element ) {
                  
                    Element e = (Element) dest;                    
                    e.getAttributes().add(a);
                    
                } else {
                    throw new RuntimeException("Unsupported operation (" + target.getClass() + " -> " + dest.getClass());
                }

            } else {                                     
                throw new RuntimeException("Unsupported target object (type: " + target.getClass() + ")");
            }           
                
            HashMap<String, Document> ret = new HashMap<String, Document>();

            ret.putAll(docs);
            ret.put(nameDest, docDest);

            return ret;
                        
    }

    public int getMaxChildren() {
        return 0;
    }

    public String getChildName(int pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
}
