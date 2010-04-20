/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.plugins.handlers;

import ch.epfl.dslab.conferrng.arugula.Configuration;
import ch.epfl.dslab.conferrng.arugula.Operator;
import ch.epfl.dslab.conferrng.arugula.SelectedNode;
import ch.epfl.dslab.conferrng.arugula.Serializer;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public class ConfigurationCollector extends Serializer{

    public ConfigurationCollector(FaultInjectionPlan plan, Element e){
        super(plan, e);
    }
    @Override
    public Collection<Configuration> apply(Collection<SelectedNode> input) {

        Set<Configuration> result = new HashSet<Configuration>();

        for (SelectedNode doc : input) {
            doc.addYourConfiguration(result);
        }
        return result;
    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
               return "ConfigurationCollector";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new ConfigurationCollector(plan, e);
            }
        };
    }

    @Override
    protected void serialize(Element n, Writer w) {
    }

}
