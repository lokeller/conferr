/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.gui.graph;

import ch.epfl.dslab.conferrng.arugula.XPathUtils;
import org.jdom.Text;

/**
 *
 * @author slv
 */
public class GraphVertexText extends GraphVertex{
    private Text text;
    public GraphVertexText(Text t){
        this.text = t;
    }
    @Override
    public String toString(){
        return text.getTextTrim();
    }

    @Override
    protected boolean specificCompare(GraphVertex g) {
        if(!(g instanceof GraphVertexText))
            return false;
        return text.equals(((GraphVertexText)g).text);
    }

    @Override
    protected int getSpecificHashCode() {

        return 97 * (this.text != null ? this.text.hashCode() : 0);
    }

    @Override
    public String getXPath() {
        return XPathUtils.getXPath(text);
    }
    
}
