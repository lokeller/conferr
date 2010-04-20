/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package conferr.gui.graph;

import ch.epfl.dslab.conferrng.arugula.XPathUtils;
import org.jdom.Attribute;

/**
 *
 * @author slv
 */
public class GraphVertexAttribute extends GraphVertex {

    private String stringValue;
    private Attribute attribute;

    public GraphVertexAttribute(Attribute a) {
        stringValue = a.getValue();
        attribute = a;
    }

     @Override
    protected int getSpecificHashCode() {

        return 23 * (this.attribute != null ? this.attribute.hashCode() : 0);
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    protected boolean specificCompare(GraphVertex g) {
        if (!(g instanceof GraphVertexAttribute)) {
            return false;
        }
        GraphVertexAttribute a = (GraphVertexAttribute) g;
        return attribute.equals(a.attribute);
    }

    @Override
    public String getXPath() {
        return XPathUtils.getXPath(attribute);
    }
}
