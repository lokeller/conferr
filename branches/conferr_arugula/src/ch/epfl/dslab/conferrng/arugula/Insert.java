/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author slv
 */
public class Insert extends Transform {

    private static final String WHAT = "what";

    public Insert(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        addParameter(new Parameter(WHAT, "", Parameter.STRING));
    }

    protected static Element getElement(String string) {
        try {
            SAXBuilder builder = new SAXBuilder();
            return (Element) builder.build(new StringReader(string)).getRootElement().detach();
        } catch (JDOMException ex) {
            Logger.getLogger(Insert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Insert.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {
        Element e = getElement(getParameterValue(WHAT));
        if (e == null) {
            return input;
        }
        for (SelectedNode node : input) {
            Object o = node.getNode();
            if (o instanceof Element) {
                Element parent = (Element) (o);
                node.addDescription("Inserted "+e);
                parent.addContent(e);
            }
        }

        return input;

    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "Insert";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new Insert(plan, e);
            }
        };

    }
}
