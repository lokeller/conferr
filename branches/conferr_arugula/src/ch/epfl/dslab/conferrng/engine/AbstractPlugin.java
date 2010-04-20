/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.engine;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import org.jdom.Element;

/**
 * Helper class that can be used to implement a plugin
 * 
 */

public abstract class AbstractPlugin extends ObservableBean implements Plugin  {
    private static final String IDENTIFIER = "identifier";
    
    private Vector<Parameter> parameters = new Vector<Parameter>();
   
    /** the fault plan associated with this plugin */
    protected FaultInjectionPlan plan;

    /**
     *
     * Creates a new plugin instance
     *
     * @param plan the associated plan
     */
    public AbstractPlugin(FaultInjectionPlan plan) {
        this.plan = plan;

      //  addParameter(new Parameter(IDENTIFIER, "Plugin" + this.hashCode(), Parameter.STRING));
        
    }

    /**
     *
     * Add a parameter to the list of parameters, the method doesn't check if the name
     * is duplicate.
     *
     * @param p a new parameter to be added
     */
    protected void addParameter(Parameter p) {
        parameters.add(p);
        p.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                fireParamChanged(pce);
            }
        });
    }

    /**
     * Returns the plan associated to this plugin
     *
     * @return the associated plan
     */
    public FaultInjectionPlan getPlan() {
        return plan;
    }

    @Override
    public Vector<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public String getParameterValue(String name) {

        for (Parameter p : parameters) {
            if (p.getName().equals(name)) return p.getValue();
        }

        return null;

    }

    @Override
    public void setParameterValue(String name, String value) {

        for (Parameter p : parameters) {
            if (p.getName().equals(name)) {
                p.setValue(value);
            }
        }
    }

    protected void fireParamChanged(PropertyChangeEvent pce){
        pcs.firePropertyChange(pce);
        
    }

    @Override
    public Element toXML() {
        Element element = new Element("plugin");
        element.setAttribute("class-name", this.getClass().getCanonicalName());

        for ( Parameter p : parameters) {
            Element pe = new Element("param");
            pe.setAttribute("name", p.getName());
            pe.setAttribute("value", p.getValue());
            element.addContent(pe);
        }
        
        return element;

    }

    @Override
    public String getIdentifier() {
        return getParameterValue(IDENTIFIER);
    }

    @Override
    public void setIdentifier(String identifier) {
        String oldValue = getIdentifier();
        setParameterValue( IDENTIFIER, identifier);
        pcs.firePropertyChange( IDENTIFIER, oldValue, identifier);
    }


    @Override
    public String toString() {
        return this.getIdentifier() + "(" + this.getClass().getSimpleName() + ")";
    }

}
