/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.gui.XPathSelectDialog;
import java.util.List;
import java.util.Vector;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Text;

/**
 *
 * @author slv
 */
public class XPathUtils {

    public static String getXPathUnknown(Object o) {
        if (o instanceof Attribute) {
            return getXPath((Attribute) o);
        }
        if (o instanceof Text) {
            return getXPath((Text) o);
        }
        if (o instanceof Element) {
            return getXPath((Element) o);
        }
        return "";
    }

    public static String getXPath(Attribute attribute) {
        return XPathUtils.getXPath(attribute.getParent()) + "/attribute::" + attribute.getName();
    }

    public static String getXPath(Text text) {
        return XPathUtils.getXPath(text.getParentElement()) + "/text()";
    }
    private static String ATTRIBUTE = "_attribute_";

    private static String addAttribute(Element e) {
        String attr = e.getAttributeValue(ATTRIBUTE);
        if (attr == null || attr.equals("")) {
            attr = getStringElement(e).hashCode() + "";
            e.setAttribute(ATTRIBUTE, attr);
        }
        return attr;
    }

    public static void addAttributes(Element e) {
        addAttribute(e);
        for(Object o : e.getContent()){
            if(o instanceof Element){
                addAttributes((Element) o);
            }
        }
    }

    public static String getStringElement(Element e){
        String res = "";
        res+=e.getName();
        for(Object o : e.getAttributes()){
            res+=((Attribute)o).toString();
        }
        for(Object o : e.getContent()){
            if(o instanceof Element){
                res+=getStringElement((Element) o);
            }else{
                res+=o.toString();
            }
        }
        return res;
    }
    public static String getXPath(Element e) {
        return "//" + e.getName() + "[@" + ATTRIBUTE + "=" + addAttribute(e) + "]";
    }

    public static Element getACopy(Element e) {
        Element copy = (Element) e.clone();
        copy.removeAttribute(ATTRIBUTE);
        return copy;
    }

    public static String getXPathNotSpecific(Element e) {
        List<String> path = new Vector<String>();
        String result = e.getName();
        path.add(result);
        Element parent = e.getParentElement();
        while (!e.isRootElement() && parent != null && e.getAttributeValue(XPathSelectDialog.ROOT) == null) {
            int index = 1;
            for (Object o : parent.getChildren(e.getName())) {
                if (o.equals(e)) {
                    break;
                }
                index++;
            }
            path.add(0, parent.getName());
            String p = path.get(1);
            path.remove(1);
            path.add(1, p + "[" + index + "]");
            e = parent;
            parent = e.getParentElement();
        }

        result = "";
        for (int i = path.size() - 1; i >= 0; i--) {
            result = "/" + path.get(i) + result;
        }
        return "/" + result;
    }
}
