/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.plugins.handlers;

import ch.epfl.dslab.conferrng.arugula.Operator;
import ch.epfl.dslab.conferrng.arugula.Serializer;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import java.io.Writer;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public class LineSerializer extends Serializer {

    public static final String commentPatternString = "comment-pattern";
    public static final String sectionPatternString = "section-pattern";
    public static final String separatorPatternString = "separator-pattern";

    public LineSerializer(FaultInjectionPlan plan, Element e) {
        super(plan, e);

        addParameter(new Parameter(commentPatternString, "#.*$", Parameter.REGEX_STRING));
        addParameter(new Parameter(sectionPatternString, "^\\[.*\\]$", Parameter.REGEX_STRING));
        addParameter(new Parameter(separatorPatternString, "\\s*=\\s*", Parameter.REGEX_STRING));
    }

    @Override
     protected void serialize(Element n, Writer w) {

        try {
            if (n.getName().equals("directive")) {
                w.write(n.getAttributeValue("name"));
                if (n.getAttribute("separator") != null) {
                    w.write(n.getAttributeValue("separator"));
                }
                w.write(n.getAttributeValue("content"));
                w.write("\n");
            } else if (n.getName().equals("section")) {
                w.write(n.getAttributeValue("name"));
                w.write("\n");
            }

            for (Object o : n.getChildren()) {
                serialize((Element) o, w);
            }

            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "LineSerializer";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new LineSerializer(plan, e);
            }
        };
    }
}
