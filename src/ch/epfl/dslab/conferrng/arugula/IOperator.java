/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import java.util.Collection;
import org.jdom.Element;

/**
 *
 * @author slv
 */
public interface IOperator<T, V> {

    public Collection<T> apply(Collection<V> input);

    public Factory getFactory();

    public static abstract class Factory<T, V> {

        public abstract String getName();

        public Operator<T, V> fromXML(Element e) {
            return fromXML(e, plan);
        }
        public Operator<T, V> fromXML(Element e, FaultInjectionPlan plan) {
            if (!e.getName().equals(getName())) {
                return null;
            }
            System.out.println("I can do " + e.getName());

            Operator newOp = getACopy(plan, e);
            for (Parameter param : newOp.getParameters()) {
                param.fromXML(e);
            }
            for (Parameter param : newOp.getParameters()) {
                if (!(param.isSet() || param.getName().equals("identifier"))) {
                    System.err.println("For "+getName()+", parameter "+param.getName()+" is not set");
                    return null;
                }
            }
            return newOp;
        }
        private FaultInjectionPlan plan;
        public void setFaultInjectionPlan(FaultInjectionPlan plan){
            this.plan=plan;
        }
        @Override
        public String toString() {
            return getName();
        }

        public Operator<T, V> getACopy(Element e){
      //      System.err.println("$$$$$$$$$$$$$$$$$$$$"+plan);
            return getACopy(plan, e);
        }
        public abstract Operator<T, V> getACopy(FaultInjectionPlan plan, Element e);

        public FaultInjectionPlan getPlan(){
            return plan;
        }
    }
}
