/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import java.util.Collection;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public class Move extends Transform {

    private static final String WHERE = "where";

    public Move(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        addParameter(new Parameter(WHERE, "", Parameter.XPATH_EXPRESSION));
    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "Move";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new Move(plan, e);
            }
        };
    }

    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {
        if(getParameterValue(WHERE)==null || getParameterValue(WHERE).equals(""))
            return input;
        Transform t = new InsertInto(plan, null);
        t.setParameterValue(InsertInto.WHERE, getParameterValue(WHERE));
        Collection<SelectedNode> result = t.apply(input);
        t = new Delete(plan, null);
        result = t.apply(input);
        return result;
    }
}
