/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public class ForEach extends Transform {

    public ForEach(FaultInjectionPlan plan, Element e) {
        super(plan, e);
    }

    

    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {
        List<SelectedNode> result = new Vector<SelectedNode>();
        for (SelectedNode s : input) {
            SelectedNode newNode = s.splitYourself();
            result.addAll(applyToChildren(new Vector(Arrays.asList(newNode))));
        }
        return result;
    }

    

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "ForEach";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new ForEach(plan, e);
            }
        };
    }
}
