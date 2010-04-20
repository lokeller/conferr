/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import java.util.Collection;
import java.util.Vector;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public class Parent extends Transform{

    public Parent(FaultInjectionPlan plan, Element e){
        super(plan, e);
    }
    public static Element getParent(Object o) {
        Element parent = null;
        if (o instanceof Content) {
            parent = ((Content) o).getParentElement();
        } else {
            if (o instanceof Attribute) {
                parent = ((Attribute) o).getParent();
            }
        }
        return parent;
    }

    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {
        Collection<SelectedNode> result = new Vector<SelectedNode>();
        for(SelectedNode node : input){
            result.add(new SelectedNode(getParent(node.getNode()), node));
        }
        return result;
    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "Parent";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new Parent(plan, e);
            }
        };
    }

}
