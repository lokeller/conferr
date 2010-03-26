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
package conferr.handlers;

import conferr.ConfigurationFile;
import conferr.Handler;
import conferr.ImpossibleConfigurationException;
import conferr.Parameter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;


public class TabularTextFile implements  Handler {
        
    private static final String hasStartingFixedLengthField = "hasStartingFixedLengthField";
    private static final String separatorString = "fieldSeparator";
    private static final String startingFixedLengthFieldSize = "startingFixedLengthFieldSize";
    public static final String transformString = "transform";
    private static final String serializationString = "serialization";

    public Document parseConfiguration(Reader input, ConfigurationFile provider) {
        try {

            BufferedReader br = new BufferedReader(input);

            Element root = new Element("root");
            Document doc = new Document(root);

            String line;

            String separator = "";
            String originalSeparator = provider.getParameterValue(separatorString);            
            
            for (int i = 0 ; i < originalSeparator.length(); i++) {
                separator += "\\x" + Integer.toHexString(originalSeparator.charAt(i));
            }
            
            while ((line = br.readLine()) != null) {

                Element lineEl = new Element("line");

                if (hasStartingFixedLengthField(provider)) {
                    Element f1 = new Element("field");
                    f1.addContent(line.substring(0, startingFixedLengthFieldSize(provider)));
                    lineEl.addContent(f1);
                    line = line.substring(startingFixedLengthFieldSize(provider));
                }

                
                String[] tokens = line.split(separator);
                
                for ( String token : tokens) {
                    Element fieldEl = new Element("field");
                    fieldEl.addContent(token);
                    lineEl.addContent(fieldEl);
                }

                root.addContent(lineEl);
            }

            if ( ! provider.getParameterValue(transformString).equals("") ) {
            
                File stylesheetFile = new File(provider.getPlan().getAbsolutePath(provider.getParameterValue(transformString)));

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Templates stylesheet = transformerFactory.newTemplates(new StreamSource(stylesheetFile));

                javax.xml.transform.Transformer processor = stylesheet.newTransformer();

                JDOMSource source = new JDOMSource(doc);

                JDOMResult result = new JDOMResult();

                processor.transform(source, result);

                return result.getDocument();            
            } else {
                return doc;
            }

        } catch (IOException ex) {
            throw new RuntimeException("Unable to process file: " + ex.getMessage(), ex);
        } catch (TransformerException ex) {            
            throw new RuntimeException("Unable to process file: " + ex.getMessage(), ex);
        }

        
    }
    
    public boolean hasStartingFixedLengthField(ConfigurationFile file) {
        try {
            return Boolean.parseBoolean(file.getParameterValue(hasStartingFixedLengthField));
        } catch ( IllegalArgumentException ex ) {
            return false;
        }
    }
    
    public int startingFixedLengthFieldSize(ConfigurationFile file) {
        try {
            return Integer.parseInt(file.getParameterValue(startingFixedLengthFieldSize));
        } catch ( IllegalArgumentException ex ) {
            return 0;
        }
    }

    public void serializeConfiguration(Document config, Writer output, ConfigurationFile provider) throws ImpossibleConfigurationException {
       try {
           
           if ( ! provider.getParameterValue(serializationString).equals("")) {
                File stylesheetFile = new File(provider.getPlan().getAbsolutePath(provider.getParameterValue(serializationString)));

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Templates stylesheet = transformerFactory.newTemplates(new StreamSource(stylesheetFile));

                javax.xml.transform.Transformer processor = stylesheet.newTransformer();

                JDOMSource source = new JDOMSource(config);

                JDOMResult result = new JDOMResult();

                processor.transform(source, result);
                
                config = result.getDocument();
           }
            
           for (Object o : config.getRootElement().getChildren("line")) {
               
               Element line = (Element) o;
               
               boolean first = true;
               boolean firstSep = true;
               
               for (Object o2 : line.getChildren("field")) {

                   Element field = (Element) o2;
                   
                   if ( first && hasStartingFixedLengthField(provider)) {
                       output.append(field.getText());                                              
                   } else if ( firstSep){
                       output.append(field.getText());
                       firstSep = false;
                   } else {
                       output.append(provider.getParameterValue(separatorString));
                       output.append(field.getText());
                   }
                   
                   first = false;
                   
               }
               output.append("\n");
               
           }
           
        } catch (IOException ex) {
            throw new RuntimeException("Unable to process file: " + ex.getMessage(), ex);
        } catch (TransformerException ex) {            
            throw new RuntimeException("Unable to process file: " + ex.getMessage(), ex);
        }
    }

    public Vector<Parameter> getDefaultParameters() {
        Vector<Parameter> parameters = new Vector<Parameter>();
        
        parameters.add(new Parameter(separatorString,"", Parameter.STRING));
        parameters.add(new Parameter(hasStartingFixedLengthField,"", Parameter.BOOLEAN));
        parameters.add(new Parameter(startingFixedLengthFieldSize,"", Parameter.INTEGER));
        parameters.add(new Parameter(transformString,"", Parameter.XSLT_FILE));
        parameters.add(new Parameter(serializationString,"", Parameter.XSLT_FILE));        
        
        return parameters;
    }

}
