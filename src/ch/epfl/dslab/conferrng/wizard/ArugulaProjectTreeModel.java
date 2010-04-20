/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.wizard;

import ch.epfl.dslab.conferrng.arugula.ErrorGenerator;
import ch.epfl.dslab.conferrng.arugula.Operator;
import ch.epfl.dslab.conferrng.arugula.OperatorChangedListener;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author lokeller
 */
public class ArugulaProjectTreeModel implements TreeModel, PropertyChangeListener, OperatorChangedListener {

    private FaultInjectionPlan plan;

    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Project", true);

    private HashSet<TreeModelListener> listeners = new HashSet<TreeModelListener> ();

    public ArugulaProjectTreeModel(FaultInjectionPlan plan) {
        setPlan(plan);
    }

    public FaultInjectionPlan getPlan() {
        return plan;
    }

    public void setPlan(FaultInjectionPlan plan) {
        if ( this.plan != null ) {

            for ( ErrorGenerator e : this.plan.getErrorGenerators() ) {
                e.removeOperatorChangeListener(this);
            }

            this.plan.removePropertyChangeListener(FaultInjectionPlan.PROP_ERRORGENERATORS, this);
        }
        this.plan = plan;

        if ( this.plan != null) {

            for ( ErrorGenerator e : this.plan.getErrorGenerators() ) {
                e.addOperatorChangeListener(this);
            }

            this.plan.addPropertyChangeListener(FaultInjectionPlan.PROP_ERRORGENERATORS, this);
        }
    }

    @Override
    public Object getRoot() {
        return root;
    }

    private List parentToList(Object parent) {
        if ( parent == root) {
            if ( plan == null) return new Vector();
            return plan.getErrorGenerators();
        } else if ( parent instanceof Operator) {
            Operator o = (Operator) parent;
            return o.getChildren();
        } else {
            return null;
        }
    }

    @Override
    public Object getChild(Object parent, int index) {

        return parentToList(parent).get(index);

    }

    @Override
    public int getChildCount(Object parent) {
        return parentToList(parent).size();

    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return parentToList(parent).indexOf(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        for ( ErrorGenerator e : (Vector<ErrorGenerator>) evt.getOldValue()) {
            e.removeOperatorChangeListener(this);
        }
        
        for ( ErrorGenerator e : (Vector<ErrorGenerator>) evt.getNewValue()) {
            e.addOperatorChangeListener(this);
        }

        for ( TreeModelListener listener : listeners) {
            listener.treeStructureChanged(new TreeModelEvent(this, new Object[] { root}));
        }


    }

    @Override
    public void operatorChanged(Operator o) {
        for ( TreeModelListener listener : listeners) {
            listener.treeStructureChanged(new TreeModelEvent(this, new Object[] { root}));
        }
    }


}
