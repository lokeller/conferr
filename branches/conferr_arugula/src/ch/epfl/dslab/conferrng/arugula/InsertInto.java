/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.arugula.IOperator.Factory;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public class InsertInto extends Transform {

    public static final String WHERE = "where";

    public InsertInto(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        addParameter(new Parameter(WHERE, "", Parameter.XPATH_EXPRESSION));
    }

    /**
     * returns the places where the input nodes should be inserted.
     * Applies the XPathSelect operation on the "where" parameter with the
     * passed node as argument
     * @param node a node to insert
     * @return the places where to insert node
     */
    protected List<SelectedNode> getObjects(SelectedNode node) {
        String param = getParameterValue(WHERE);
        param = param.replaceAll("$target", XPathUtils.getXPathUnknown(node.getNode()));

        XPathSelect select = new XPathSelect(plan, null);
        select.setParameterValue(XPathSelect.TARGET, param);
        List<Operator> operators = new Vector(Arrays.asList(select));
        Collection execute = ArugulaRuntime.execute(operators, Arrays.asList(node));
        List<SelectedNode> result = new Vector<SelectedNode>();
        for (Object o : execute) {
            if (o instanceof SelectedNode) {
                result.add((SelectedNode) o);
            }
        }
        return result;
    }

    /**
     * Insert input into List of selected nodes denoted by "where" param
     * @param input
     * @return
     */
    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {
        List<SelectedNode> result = new Vector<SelectedNode>();
        for (SelectedNode node : input) {
            if (!(node.getNode() instanceof Element)) {
                continue;
            }
            List<SelectedNode> placesOfInsertion = getObjects(node);
            result.addAll(placesOfInsertion);
            for (SelectedNode parentNode : placesOfInsertion) {
                Object o = parentNode.getNode();
                if (o instanceof Element) {
                    Element parent = (Element) (o);
                    parent.addContent(Arrays.asList(XPathUtils.getACopy((Element) node.getNode())));
                }
            }
        }
        return result;

    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "InsertInto";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new InsertInto(plan, e);
            }
        };

    }
}
