/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author slv
 */
public abstract class Transform extends Operator<SelectedNode, SelectedNode> {

    private static final String CONDITION = "enabled";
    private static final String SHOULDITSUCCEED = "benign";

    public Transform(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        addParameter(new Parameter(CONDITION, "*", Parameter.XPATH_EXPRESSION));
        addParameter(new Parameter(SHOULDITSUCCEED, "true", Parameter.BOOLEAN));
    }

    private boolean conditionSatisfied(Collection<SelectedNode> input) {
        try {
            Set<Configuration> configs = new HashSet<Configuration>();
            for (SelectedNode node : input) {
                node.addYourConfiguration(configs);
            }
            XPath xpath = XPath.newInstance(getParameterValue(CONDITION));
            for (Configuration config : configs) {
                for (Document doc : config.getDocumentIterator()) {
                    if (xpath.selectSingleNode(doc) != null) {
                        return true;
                    }
                }
            }
        } catch (JDOMException ex) {
            Logger.getLogger(Transform.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Collection<SelectedNode> apply(Collection<SelectedNode> input) {
        if (conditionSatisfied(input)) {
            Set<SelectedNode> result = new HashSet<SelectedNode>();
            Iterable<SelectedNode> afterApplying = applyAfterCheck(input);
            for (SelectedNode node : afterApplying) {
                if (node != null && node.getNode() != null) {
                    node.setExpectedBehavior(Boolean.valueOf(getParameterValue(SHOULDITSUCCEED)));
                    result.add(node);
                }
            }
            if (result.isEmpty()) {
                //throw new RuntimeException("RESULT IS EMPTY in " + getClass().getName());
            }
            return result;
        }

        return input;
    }

    public abstract Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input);

    @Override
    public Class getOutputClass() {
        return SelectedNode.class;
    }

    @Override
    public Class getInputClass() {
        return SelectedNode.class;
    }



}
