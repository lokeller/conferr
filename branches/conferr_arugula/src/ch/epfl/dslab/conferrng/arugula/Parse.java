/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public abstract class Parse extends Operator<SelectedNode, Configuration> {

    public Parse(FaultInjectionPlan plan, Element e) {
        super(plan, e);
    }

    @Override
    public Collection<SelectedNode> apply(Collection<Configuration> input) {
        List<SelectedNode> result = new Vector<SelectedNode>();
        for (Configuration cfile : input) {
            for (String file : cfile.getInputFileNameIterator()) {
                String readFile = cfile.getContentsForInputFileName(file);
                Document doc = parseConfiguration(readFile, makeAppropiateString(file));
                XPathUtils.addAttributes(doc.getRootElement());
                cfile.addDocumentForFile(readFile, doc);
                result.add(new SelectedNode("/", doc, cfile));
            }
        }
        return result;
    }

    private static String makeAppropiateString(String text) {
        return text.replace('/', '.').replaceAll("^\\.*", "");
    }
    public static void main(String[] args){
       System.err.println(makeAppropiateString("//silviu/andrica"));
    }
    protected abstract Document parseConfiguration(String file, String name);

    @Override
    public Class getOutputClass() {
        return SelectedNode.class;
    }

    @Override
    public Class getInputClass() {
        return Configuration.class;
    }



}
