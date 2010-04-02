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
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;




public class RegexContentTemplate extends MultidocSimpleTemplate {

    static final private String documentParam = "document";
    static final private String targetParam = "target";        
            
    public static final String targetString = "target";    
    public static final String matchString = "match";
    public static final String substituteString = "substitute";
            
    @Override
    public Vector<Parameter> getDefaultParameters() {
        
        Vector<Parameter> parameters = new Vector<Parameter>();
                
        parameters.add(new Parameter(targetString, "", Parameter.XPATH_EXPRESSION));
        parameters.add(new Parameter(matchString, "", Parameter.REGEX_STRING));
        parameters.add(new Parameter(substituteString, "", Parameter.REGEX_STRING));
            
        return parameters;
        
    }

    @Override
    public FaultSpace getDescription(Map<String, Document> configs, FaultScenarioSet scenario) {
        
        try {

            FaultSpace documents = new FaultSpace(documentParam);

            Pattern p = Pattern.compile(scenario.getParameterValue(matchString));

            for (Map.Entry<String, Document> entry : configs.entrySet()) {
                XPath xpath = XPath.newInstance(scenario.getParameterValue(targetString));
     
               List l = xpath.selectNodes(entry.getValue());

               List matching = new Vector();

               for ( Object o : l) {

                   Matcher m = null;

                   if ( o instanceof Element) {
                       m = p.matcher(((Element) o).getText());
                   } else if ( o instanceof Attribute) {
                       m = p.matcher(((Attribute) o).getValue());
                   } else if (o instanceof Text) {
                       m = p.matcher(((Text) o).getText());
                   }

                   if ( m != null && m.find()) {
                       matching.add(o);
                   }

               }

                if (matching.size() > 0) {

                    FaultSpace targets = new FaultSpace(targetParam);
                    
                    targets.addSubspace(new ElementsSet(matching), null);
                    
                    documents.addSubspace(new ValueSet(new Value(entry.getKey())), targets);

                }
            }

            return documents;        
        } catch (JDOMException ex) {
            throw new RuntimeException(ex);
        } catch (PatternSyntaxException ex) {
            throw new RuntimeException(ex);         
        }
                
    }
    
    @Override
    public Map<String, Document> applyToConfig(Map<String, Document> docs, Fault f, FaultScenarioSet scenario) {
                
            String name = (String) f.getObjectByName(documentParam);            
            Document doc = new Document(docs.get(name).cloneContent());

            Object o = getCorrespondingContent(doc, f.getObjectByName(targetParam));

            if (o instanceof Element) {
                Element e = (Element) o;
                e.setText(modifyString(e.getText(), scenario));
                
            } else if (o instanceof Attribute) {
                Attribute attr = (Attribute) o;

                attr.setValue(modifyString(attr.getValue(), scenario));
            }  else if (o instanceof Text ) {
                Text t = (Text) o;
                
                t.setText(modifyString(t.getText(), scenario));
                
            } else {
                return null;
            }


            HashMap<String, Document> ret = new HashMap<String, Document>();

            ret.putAll(docs);
            ret.put(name, doc);
            
            return ret;            
        
    }
    
    protected String modifyString(String text, FaultScenarioSet scenario) {
        return text.replaceAll(scenario.getParameterValue(matchString), scenario.getParameterValue(substituteString));
    }

    public int getMaxChildren() {
        return 0;
    }

    public String getChildName(int pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
    
}
