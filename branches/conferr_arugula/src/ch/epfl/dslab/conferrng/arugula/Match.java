/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public class Match extends Transform{
    private static final String EXPRESSION = "expression";

    public Match(FaultInjectionPlan plan, Element e){
        super(plan, e);
        addParameter(new Parameter(EXPRESSION,"", Parameter.REGEX_STRING));
    }
    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {
        List<SelectedNode> result = new Vector<SelectedNode>();

        String expression = getParameterValue(EXPRESSION);

        for(SelectedNode n : input){
            if(Pattern.matches(expression, n.getNode().toString()))
                result.add(n);
        }
        return result;
    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "Match";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new Match(plan, e);
            }
        };
    }

}
