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

package conferr.transforms;

import conferr.ConfigurationTransform;
import conferr.Transform;
import conferr.ImpossibleConfigurationException;
import conferr.Parameter;
import java.io.File;
import java.util.Vector;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.jdom.Document;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

/**
 *
 * @author lokeller
 */
public class XSLTTransform implements  Transform {
    
    
    private final static String filterString = "Filter script";
    private final static String unfilterString = "Unfilter script";

    public Vector<Parameter> getDefaultParameters() {
        Vector<Parameter> parameters = new Vector<Parameter>();
        
        parameters.add(new Parameter(filterString, "", Parameter.XSLT_FILE));
        parameters.add(new Parameter(unfilterString, "", Parameter.XSLT_FILE));
        
        return parameters;
    }  
    
    public Document filter(Document doc, ConfigurationTransform provider) throws ImpossibleConfigurationException{
        try {
            if (provider.getParameterValue(filterString).equals("")) {
                return doc;
            }
            return process(doc, provider.getParameterValue(filterString), provider);
        } catch (TransformerException ex) {
            throw new ImpossibleConfigurationException(ex.getMessageAndLocation());
        }
    }
    
    private Document process(Document doc, String script, ConfigurationTransform provider ) throws TransformerException {

        File stylesheetFile = new File(provider.getPlan().getAbsolutePath(script));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Templates stylesheet = transformerFactory.newTemplates(new StreamSource(stylesheetFile));

        javax.xml.transform.Transformer processor = stylesheet.newTransformer();

        JDOMSource source = new JDOMSource(doc);
        JDOMResult result = new JDOMResult();

        processor.transform(source, result);

        return result.getDocument();        
            
    }
 
    public Document unfilter(Document doc, ConfigurationTransform provider) throws ImpossibleConfigurationException {
        try {
            if (provider.getParameterValue(unfilterString).equals("")) {
                return doc;
            }
            return process(doc, provider.getParameterValue(unfilterString), provider);
        } catch (TransformerException ex) {
            throw new ImpossibleConfigurationException(ex.getMessageAndLocation());
        }
    }
    
  
    
}
