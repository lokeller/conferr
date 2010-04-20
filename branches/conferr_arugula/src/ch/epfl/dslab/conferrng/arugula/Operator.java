/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.AbstractPlugin;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import ch.epfl.dslab.conferrng.engine.PluginFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public abstract class Operator<T, V> extends AbstractPlugin implements IOperator<T, V>, PropertyChangeListener, Iterable<Operator>, OperatorChangedListener {

    private List<Operator> children;
    private final String name;
    private Operator parent;

    public Operator() {
        super(null);
        name="";
        children=new Vector<Operator>();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt){
        super.fireParamChanged(evt);
    }

    private HashSet<OperatorChangedListener> listeners = new HashSet<OperatorChangedListener>();

    public void addOperatorChangeListener(OperatorChangedListener listener) {
        listeners.add(listener);
    }

    public void removeOperatorChangeListener(OperatorChangedListener listener) {
        listeners.remove(listener);
    }

    protected void fireOperatorChanged(Operator o) {
        for(OperatorChangedListener listener : listeners) {
            listener.operatorChanged(o);
        }
    }

    public Operator getParent() {
        return parent;
    }

    private void setParent(Operator parent) {
        this.parent = parent;
    }

    public List<Operator> getChildren() {
        return children;
    }

    public void setChildren(List<Operator> children) {
        List<Operator> oldChildren = this.children;

        if ( this.children  != null ) {
            for ( Operator child : this.children ) {
                child.removeOperatorChangeListener(this);
                child.setParent(null);
            }
        }

        this.children = children;


        if ( this.children  != null ) {
            for ( Operator child : this.children ) {
                child.addOperatorChangeListener(this);
                child.setParent(this);
            }
        }

        fireOperatorChanged(this);
        pcs.firePropertyChange("children", oldChildren, children);
    }

    public void addChild(Operator op){
        Vector<Operator> newChildren = new Vector<Operator>(children);
        newChildren.add(op);
        setChildren(newChildren);
    }

    public void addChild(Operator op, int pos){
        Vector<Operator> newChildren = new Vector<Operator>(children);
        newChildren.insertElementAt(op, pos);
        setChildren(newChildren);
    }

    public void removeChild(Operator op){
        Vector<Operator> newChildren = new Vector<Operator>(children);
        newChildren.remove(op);
        setChildren(newChildren);
    }

    
    public Operator(FaultInjectionPlan plan, Element e) {
        super(plan);
        //System.out.println("Building " + getClass().getName());
        setChildren(getChildrenFromXML(plan, e));
        //System.out.println("Done " + getClass().getName() + "\n");
        name = getFactory().getName();
    }

    @Override
    public String toString(){
        return name;
    }

    public static List<Operator> getChildrenFromXML(FaultInjectionPlan plan, Element e) {
        List<Operator> children = new Vector<Operator>();

        if (e == null) {
            return children;
        }

        for (Object o : e.getChildren()) {
            if (o instanceof Element) {
                Element elem = (Element) o;
                Operator op = PluginFactory.getOperator(elem, plan);
                System.err.println("===ADDING " + op);
                if (op != null) {
                    children.add(op);
                }
            }
        }
        return children;
    }

    protected Collection<T> applyToChildren(Collection<V> input) {
        return ArugulaRuntime.execute(children, input);
    }

    @Override
    public Element toXML() {
        Element e = new Element(getFactory().getName());
        for (Parameter p : getParameters()) {
            e.setAttribute(p.getName(), p.getValue());
        }
        for (Operator child : children) {
            e.addContent(child.toXML());
        }
        return e;
    }


    @Override
    public Iterator<Operator> iterator(){
        return children.iterator();
    }

    @Override
    public void operatorChanged(Operator o) {
        fireOperatorChanged(o);
    }

    public Vector<String> getErrors() {

        Vector<String> errors = new Vector<String>();

        boolean childWithErrors = false;

        for (Operator o : children ) {

            Vector<String> childErrors = o.getErrors();
            if ( childErrors.size() > 0) {
                childWithErrors = true;
            }

        }

        if ( getParent() != null ) {
            int myPos = getParent().getChildren().indexOf(this);
            
            if ( myPos > 0 ) {
                 Operator prev = (Operator) getParent().getChildren().get(myPos - 1);

                 if ( ! this.getInputClass().isAssignableFrom(prev.getOutputClass()) ) {
                     errors.add("The input of this operator is incompatible with the output of the previous operator");
                 }

            }
            
        }

        if ( childWithErrors) {
            errors.add("Some child operators contain errors");
        }

        return errors;
    }

    public abstract Class getOutputClass();

    public abstract Class getInputClass();

    @Override
    public Factory getFactory(){
        Factory f = getProtectedFactory();
        f.setFaultInjectionPlan(plan);
        return f;
    }

    protected abstract Factory getProtectedFactory();
}
