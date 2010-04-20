package ch.epfl.dslab.conferrng.engine;

import ch.epfl.dslab.conferrng.arugula.Operator;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;

/**
 *
 * This class is used to instantiate plugins
 *
 * 
 */
public class PluginFactory {

    /**
     *
     * Creates a new instance of a plugin of the specified class using the path
     * provided by the specified plan.
     *
     * @param className the name of the class of the plugin
     * @param plan  a fault plan    
     * @return an instance of the specified plugin
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     *
     */
    public static Plugin newInstance(String className, FaultInjectionPlan plan) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        ClassFinderBean cfb = new ClassFinderBean(plan);

        Class plugin = cfb.loadClass(className);

        Plugin pluginInstance = (Plugin) plugin.getConstructor(FaultInjectionPlan.class).newInstance(plan);

        return pluginInstance;

    }

    /**
     *
     * Initializes an instance of a plugin and its parameters accordingly to what
     * is specified in an XML fragment.
     *
     * @param el an XML fragment
     * @param plan a plan used to load the plugin instance
     * @return a plugin initialized as specified in the XML fragment
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static Plugin fromXML(Element el, FaultInjectionPlan plan) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, InstantiationException, IllegalArgumentException, IllegalAccessException, IllegalAccessException, InvocationTargetException {

        String className = el.getAttributeValue("class-name");

        Plugin pluginInstance = newInstance(className, plan);

        for (Object o : el.getChildren("param")) {
            Element p = (Element) o;

            pluginInstance.setParameterValue(p.getAttributeValue("name"), p.getAttributeValue("value"));

        }

        return pluginInstance;


    }


    
    private final static List<Operator.Factory> factories = new Vector<Operator.Factory>();
    private static Iterable<String> jars;

    public static void setNewJars(FaultInjectionPlan plan) {
        Iterable<String> newJars = plan.getJars();
        if (newJars == jars) {
            return;
        }
        jars=newJars;
        
        ClassFinderBean classFinder = new ClassFinderBean(plan);
        for (String s : classFinder.getClassesNames(Operator.class.getName())) {
            factories.add(newInstance(s, plan, null).getFactory());
        }

    }

    public static Operator getOperator(Element e, FaultInjectionPlan plan) {
        for (Operator.Factory factory : factories) {
            Operator op = factory.fromXML(e, plan);
            if (op != null) {
                return op;
            }
        }
        return null;
    }

    /**
     *
     * Creates a new instance of an operator of the specified class using the path
     * provided by the specified plan.
     *
     * @param className the name of the class of the plugin
     * @param plan  a fault plan
     * @return an instance of the specified plugin
     */
    public static Operator newInstance(String className, FaultInjectionPlan plan, Element elem) {

        ClassFinderBean cfb = new ClassFinderBean(plan);
        try {

            Class plugin = cfb.loadClass(className);

            Operator pluginInstance = (Operator) plugin.getConstructor(FaultInjectionPlan.class, Element.class).newInstance(plan, elem);

            return pluginInstance;
        } catch (Exception e) {
            Logger.getLogger(PluginFactory.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;

    }
}
