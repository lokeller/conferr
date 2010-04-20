/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public class Variations extends Transform {

    public Variations(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        addParameter(new Parameter("times", "1", Parameter.INTEGER));
    }

    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {
        System.err.println("~~~~EXECUTING: "+Integer.valueOf(getParameterValue("times")));
        List<SelectedNode> result = new Vector<SelectedNode>();
        for (int i = 0; i < Integer.valueOf(getParameterValue("times")); i++) {
            Collection<SelectedNode> newState = copyState(input);
            result.addAll(applyToChildren(newState));
        }
        return result;
    }

    private Collection<SelectedNode> copyState(Collection<SelectedNode> input) {

        List<SelectedNode> newState = new Vector<SelectedNode>();
        Set<Configuration> setOfConfigs = new HashSet<Configuration>();
        for (SelectedNode node : input) {
            node.addYourConfiguration(setOfConfigs);
        }
        for (Configuration c : setOfConfigs) {
            Configuration conf = c.copy();
            for (SelectedNode node : input) {
                newState.add(node.findYourself(conf));
            }
        }
        return newState;
    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "Variations";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new Variations(plan, e);
            }
        };
    }
}
