/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package conferr.gui.graph;

/**
 *Marking interface
 * @author slv
 */
public abstract class GraphVertex {

    protected abstract boolean specificCompare(GraphVertex g);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GraphVertex)) {
            return false;
        }
        return specificCompare((GraphVertex) o);
    }

    protected abstract int getSpecificHashCode();

    @Override
    public int hashCode() {
        int hash = 26;

        hash *= getSpecificHashCode();
        hash *= toString().hashCode();
        return hash;
    }

    public abstract String getXPath();
}
