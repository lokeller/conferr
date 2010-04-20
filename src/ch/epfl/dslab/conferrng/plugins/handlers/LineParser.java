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
package ch.epfl.dslab.conferrng.plugins.handlers;

import ch.epfl.dslab.conferrng.arugula.Parse;
import ch.epfl.dslab.conferrng.arugula.Operator;
import ch.epfl.dslab.conferrng.engine.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Document;
import org.jdom.Element;

public class LineParser extends Parse {

    public static final String commentPatternString = "comment-pattern";
    public static final String sectionPatternString = "section-pattern";
    public static final String separatorPatternString = "separator-pattern";

    public LineParser(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        addParameter(new Parameter(commentPatternString, "#.*$", Parameter.REGEX_STRING));
        addParameter(new Parameter(sectionPatternString, "^\\[.*\\]$", Parameter.REGEX_STRING));
        addParameter(new Parameter(separatorPatternString, "\\s*=\\s*", Parameter.REGEX_STRING));

    }

    @Override
    protected Document parseConfiguration(String configurationFile, String name) {


        Element root = new Element(name);
        root.setAttribute("name", name);

        Element currentSection = root;

        Pattern commentPattern = null;

        if (getParameterValue(commentPatternString) != null) {
            commentPattern = Pattern.compile(getParameterValue(commentPatternString));
        }

        for (String line : configurationFile.split("\n")) {

            String strippedLine = line;

            Element comment = null;

            if (commentPattern != null) {

                Matcher cm = commentPattern.matcher(line);
                if (cm.find()) {
                    strippedLine = line.replaceFirst(getParameterValue(commentPatternString), "");
                    comment = new Element("comment");
                    comment.setText(cm.group());
                }
            }

            if (!getParameterValue(sectionPatternString).equals("") && strippedLine.matches(getParameterValue(sectionPatternString))) {

                currentSection = new Element("section");

                currentSection.setAttribute("name", strippedLine);

                root.addContent(currentSection);

            } else if (!strippedLine.matches("\\s*")) {

                Pattern p = Pattern.compile("^(.*?)(" + getParameterValue(separatorPatternString) + ")(.*)$");

                Matcher m = p.matcher(strippedLine);

                if (m.find()) {

                    Element node = new Element("directive");

                    node.setAttribute("name", m.group(1));
                    node.setAttribute("separator", m.group(2));
                    node.setAttribute("content", m.group(3));
                    //node.setText(m.group(3));
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
 //       return new SelectedNode("/", new Document(root), config);

    }

    


   

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "LineParser";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new LineParser(plan, e);
            }
        };
    }
}
