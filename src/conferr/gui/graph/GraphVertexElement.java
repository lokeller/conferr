/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package conferr.gui.graph;

import ch.epfl.dslab.conferrng.arugula.XPathUtils;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;

/**
 *GraphVertex based on an Element
 * @author slv
 */
public class GraphVertexElement extends GraphVertex {

    private Element element;
    private String stringValue;

    private static String getFullString(Element e) {
        String result = "";
        for (Object o : e.getAttributes()) {
            Attribute a = (Attribute) o;
            if (a.getName().equals("name")) {
                continue;
            }
            if(a.getName().startsWith("_")){
                continue;
            }
            result += " " + a.getValue();
        }
        for (Object o : e.getContent()) {
            Content c = (Content) o;
            if (!(c instanceof Text)) {
                continue;
            }
            result += " " + c.getValue();
        }
        return result;
    }

    private static String getStringOf(Element e) {

        String nameValue = e.getAttributeValue("name");
        String result = /*e.getName() + " " +*/ (nameValue != null ? nameValue : "");
        return result + getFullString(e);
    }

    public GraphVertexElement(Element e) {
        this.element = e;
        stringValue = getStringOf(e);
    }

    @Override
    protected int getSpecificHashCode() {

        return 83 * (this.element != null ? this.element.hashCode() : 0);
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    protected boolean specificCompare(GraphVertex o) {
        if (!(o instanceof GraphVertexElement)) {
            return false;
        }
        GraphVertexElement g = (GraphVertexElement) o;
        return element.equals(g.element);
    }

    @Override
    public String getXPath() {
        return XPathUtils.getXPathNotSpecific(element);
    }
}
