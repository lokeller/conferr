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

import conferr.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Document;
import org.jdom.Element;


public class LineBasedHandler implements Handler {

    public static final String commentPatternString = "comment-pattern";
    public static final String sectionPatternString = "section-pattern";
    public static final String separatorPatternString = "separator-pattern";    

    @Override
    public Vector<Parameter> getDefaultParameters() {
        
        Vector<Parameter> parameters = new Vector<Parameter>();
        
        parameters.add(new Parameter(commentPatternString, "", Parameter.REGEX_STRING));
        parameters.add(new Parameter(sectionPatternString, "", Parameter.REGEX_STRING));
        parameters.add(new Parameter(separatorPatternString, "", Parameter.REGEX_STRING));
        
        return parameters;
        
    }
    
    @Override
    public Document parseConfiguration(Reader fr, ConfigurationFile file) {

        try {

            BufferedReader br = new BufferedReader(fr);

            Element root = new Element("root");

            Element currentSection = root;

            String line;

            Pattern commentPattern = null;

            if (file.getParameterValue(commentPatternString) != null) {
                commentPattern = Pattern.compile(file.getParameterValue(commentPatternString));
            }

            while ((line = br.readLine()) != null) {

                String strippedLine = line;

                Element comment = null;

                if (commentPattern != null) {

                    Matcher cm = commentPattern.matcher(line);
                    if (cm.find()) {
                        strippedLine = line.replaceFirst(file.getParameterValue(commentPatternString), "");
                        comment = new Element("comment");
                        comment.setText(cm.group());
                    }
                }

                if (!file.getParameterValue(sectionPatternString).equals("") && strippedLine.matches(file.getParameterValue(sectionPatternString))) {

                    currentSection = new Element("section");

                    currentSection.setAttribute("name", strippedLine);

                    root.addContent(currentSection);

                } else if (!strippedLine.matches("\\s*")) {

                    Pattern p = Pattern.compile("^(.*?)(" + file.getParameterValue(separatorPatternString) + ")(.*)$");

                    Matcher m = p.matcher(strippedLine);

                    if (m.find()) {

                        Element node = new Element("directive");
                        
                        node.setAttribute("name", m.group(1));
                        node.setAttribute("separator", m.group(2));
                        node.setText(m.group(3));
                        currentSection.addContent(node);

                    } else {
                        
                        Element node = new Element("directive");

                        node.setAttribute("name", strippedLine);

                        currentSection.addContent(node);

                    }
                }

                if (comment != null) {
                    currentSection.addContent(comment);                
                }

            }

            return new Document(root);

        } catch (Exception e) {
            throw new RuntimeException("Error parsing configuration file ", e);
        }

    }

    @Override
    public void serializeConfiguration(Document config, Writer output, ConfigurationFile file) {

        try {

            serialize(config.getRootElement(), output);

        } catch (Exception e) {
            throw new RuntimeException("Error serializing configuration file ", e);
        }

    }

    private void serialize(Element n, Writer w) throws IOException {


        if (n.getName().equals("directive")) {
            w.write(n.getAttributeValue("name"));
            if (n.getAttribute("separator") != null)
                w.write(n.getAttributeValue("separator"));
            w.write(n.getText());
            w.write("\n");
        } else if (n.getName().equals("section")) {
            w.write(n.getAttributeValue("name"));            
            w.write("\n");
        }
        
        for (Object o : n.getChildren()) {
            serialize((Element) o, w);
        }

        w.flush();


    }

}
