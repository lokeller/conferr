/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public abstract class Serializer extends Operator<Configuration, SelectedNode> {
    public Serializer(FaultInjectionPlan plan, Element e) {
        super(plan, e);
    }
    @Override
    public Collection<Configuration> apply(Collection<SelectedNode> input) {

        Set<Configuration> result = new HashSet<Configuration>();

        for (SelectedNode doc : input) {
            doc.addYourConfiguration(result);
        }
        for (Configuration config : result) {
            for (Document doc : config.getDocumentIterator()) {
                config.addModifiedVersion(doc, serializeConfiguration(doc));
            }
        }
        return result;
    }
    protected String serializeConfiguration(Document config){
        StringWriter writer = new StringWriter(1024);
        serialize(config.getRootElement(), writer);
        return writer.toString();
    }
    protected abstract void serialize(Element n, Writer w);

    @Override
    public Class getOutputClass() {
        return Configuration.class;
    }

    @Override
    public Class getInputClass() {
        return SelectedNode.class;
    }



}
