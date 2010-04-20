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
public class XPathSelect extends Transform {
    public static final String TARGET = "target";

    public XPathSelect(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        addParameter(new Parameter("target", "*", Parameter.XPATH_EXPRESSION));
    }

    /**:
     * Applies the XPath expression defined by the "target" parameter to all the
     * documents that can be reached from the nodes passed as input
     * @param input The nodes whose documents will be explored
     * @return the set of selected nodes that correspndong to the XPath expression
     */
    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {
        Set<Configuration> configurations = new HashSet<Configuration>();
        for (SelectedNode node : input) {
            node.addYourConfiguration(configurations);
        }
        List<SelectedNode> result = new Vector<SelectedNode>();
        for (Configuration config : configurations) {
            for (Document doc : config.getDocumentIterator()) {
                result.addAll(getNodes(doc, config));
            }
        }
        return result;
    }

    private List<SelectedNode> getNodes(Document doc, Configuration config) {
        List<SelectedNode> listOfNodes = new Vector<SelectedNode>();
        try {
            XPath p = XPath.newInstance(getParameterValue(TARGET));


            for (Object o : p.selectNodes(doc)) {
                listOfNodes.add(new SelectedNode(XPathUtils.getXPathUnknown(o), doc, config));
            }



        } catch (JDOMException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return listOfNodes;
    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
               return "XPathSelect";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new XPathSelect(plan, e);
            }
        };
    }
}
