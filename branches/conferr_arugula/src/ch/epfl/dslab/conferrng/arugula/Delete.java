/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import java.util.Collection;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public class Delete extends Transform {

    public Delete(FaultInjectionPlan plan, Element e) {
        super(plan, e);
    }

    private static void removeObjectFromDoc(Object o) {
        if (o instanceof Content) {
            Element e = (Element) o;
            e.detach();
            return;
        }
        if (o instanceof Attribute) {
            Attribute a = (Attribute) o;
            a.detach();
            return;
        }

    }

    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {

        Parent p = new Parent(plan, null);
        Collection<SelectedNode> result = p.apply(input);
        for(SelectedNode node : input){
            result.add(node);
        }
        for (SelectedNode node : input) {
            node.addDescription("deleted "+node.toString());
            Object elem = node.getNode();
            removeObjectFromDoc(elem);
        }
        return result;
    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "Delete";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new Delete(plan, e);
            }
        };
    }
}
