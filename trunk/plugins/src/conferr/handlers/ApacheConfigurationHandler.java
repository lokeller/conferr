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
import conferr.Parameter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Document;
import org.jdom.Element;


public class ApacheConfigurationHandler implements Handler {
    
    public Document parseConfiguration(Reader fr, ConfigurationFile file) {
        try {

                BufferedReader br = new BufferedReader(fr);

                Element root = new Element("root");			

                String line;

                Stack<Element> sections = new Stack<Element>();
                sections.push(root);

                while ((line = br.readLine()) != null) {

                        line = line.replaceFirst("#.*", "");
                        line = line.trim();

                        /* section end */

                        if (line.matches("^</.*>$")) {
                                sections.pop();

                                /* section start */
                        } else 	if (line.matches("^<.*>$")) {
                                Element section = new Element("section");

                                String sectionName = line.substring(1, line.indexOf(' ') );
                                String sectionValue = line.substring(line.indexOf(' ') + 1, line.length() -1 );

                                section.setAttribute("name", sectionName);
                                section.setAttribute("value", sectionValue);

                                sections.peek().addContent(section);
                                sections.push(section);
                        } else {

                                Pattern p = Pattern.compile("^(.*?)(\\s+)(.*)$");					

                                Matcher m = p.matcher(line);

                                if ( m.find()) {

                                        Element node = new Element("directive");					

                                        node.setAttribute("name", m.group(1));
                                        node.setAttribute("separator", m.group(2));
                                        node.setText(m.group(3));

                                        sections.peek().addContent(node);

                                }
                        }
                }

                return new Document(root);

        } catch (Exception e) {
                throw new RuntimeException("Error parsing configuration file ", e);
        }
    }

    public void serializeConfiguration(Document config, Writer output, ConfigurationFile file) {
        
        try {

                serialize(config.getRootElement(), output);			

        }  catch (Exception e) {
                throw new RuntimeException("Error serializing configuration file ", e);
        }
        
    }

    private void serialize(Element n, Writer w) throws IOException {


            if (n.getName().equals("root")) {

                    for (Object n1 : n.getChildren()) {
                            serialize((Element) n1, w); 
                    }

            } else if ( n.getName().equals("section")) {

                    String sectionName = n.getAttributeValue("name");

                    String sectionValue = n.getAttributeValue("value");

                    w.append("<" + sectionName + " " + sectionValue + ">\n");

                    for (Object n1 : n.getChildren()) {
                            serialize((Element) n1, w); 
                    }

                    w.append("</" + sectionName + ">\n");

            } else if ( n.getName().equals("directive")) {

                    String directiveName = n.getAttributeValue("name");

                    String directiveValue = n.getText();

                    String separator = n.getAttributeValue("separator");
                    
                    w.append(directiveName + separator + directiveValue + "\n");

            }			

            w.flush();


    }

    public Vector<Parameter> getDefaultParameters() {
        return new Vector<Parameter>();
    }


    
    
}
