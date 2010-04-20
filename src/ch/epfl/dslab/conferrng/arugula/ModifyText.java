/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.engine.ClassFinderBean;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;

/**
 *
 * @author slv
 */
public class ModifyText extends Transform {

    private static final String FUNC = "func";

    private String prevVersion = "";
    public ModifyText(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        plan.addPropertyChangeListener(FaultInjectionPlan.PROP_JARS, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                initStringModification();
            }
        });
        Parameter p = new Parameter(FUNC, "ch.epfl.dslab.conferrng.arugula.StringModification", Parameter.CLASS_FILE);
        addParameter(p);
        stringModificationList = new Vector<StringModification>();
    }
    private List<StringModification> stringModificationList;
    private StringModification stringModification;

    private void initStringModification() {
        if (getParameterValue(FUNC).trim().equals("")) {
            return;
        }
        if(prevVersion.equals(getParameterValue(FUNC))){
            return;
        }
        prevVersion = getParameterValue(FUNC);

        stringModificationList.clear();
        try {
            Class stringModificationClass = Class.forName(getParameterValue(FUNC));
            if (Modifier.isAbstract(stringModificationClass.getModifiers())) {
                ClassFinderBean finder = new ClassFinderBean(plan);
                List<String> classes = finder.getClassesNamesIncludingAbstract(stringModificationClass.getName());
                for (String s : classes) {
                    Class cls = finder.loadClass(s);
                    if (!Modifier.isAbstract(cls.getModifiers())) {
                        System.out.println("@@@@@@@@@@@@ Class " + s);
                        stringModificationList.add((StringModification) Class.forName(s).newInstance());
                    }
                }
            }else{
                stringModificationList.add((StringModification) stringModificationClass.newInstance());
            }
        } catch (Exception ex) {
            Logger.getLogger(ModifyText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String elementScenario(Object target, long seed) {
        Element e = (Element) target;


        String mod = stringModification.getNewText(e.getText());

        if (mod == null || mod.equals("")) {

            for (Object o : e.getAttributes()) {
                Attribute a = (Attribute) o;
                if (a.getName().startsWith("_")) {
                    continue;
                }
                mod = processAnXMLNode(a, seed);
                if (!(mod == null || mod.equals(""))) {
                    return mod;
                }
            }
            for (Object o2 : e.getContent()) {
                Content c = (Content) o2;
                mod = processAnXMLNode(c, seed);
                if (!(mod == null || mod.equals(""))) {
                    return mod;
                }
            }
            return null;
        }

        e.setText(mod);
        return mod;
    }

    private String attributeScenario(Object target, long seed) {
        Attribute a = (Attribute) target;
        String mod;
        mod = stringModification.getNewText(a.getValue());
        if (mod == null) {
            return null;
        }
        a.setValue(mod);
        return mod;
    }

    private String textScenario(Object target) {
        Text t = (Text) target;
        String mod;
        mod = stringModification.getNewText(t.getText());
        if (mod == null) {
            return null;
        }
        t.setText(mod);
        return mod;
    }

    private String processANode(SelectedNode node, long seed) {
        Object XMLNode = node.getNode();
        return processAnXMLNode(XMLNode, seed);
    }

    private String processAnXMLNode(Object target, long seed) {
        String mod;
        if (target instanceof Element) {
            mod = elementScenario(target, seed);
        } else if (target instanceof Attribute) {
            mod = attributeScenario(target, seed);
        } else if (target instanceof Text) {
            mod = textScenario(target);
        } else {
            return null;
        }
        return mod;
    }

    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> input) {
        List<SelectedNode> result = new Vector<SelectedNode>();
        initStringModification();
        System.out.println("Input = " + input.size());
        for (SelectedNode node : input) {
            for (StringModification s : stringModificationList) {
                System.err.println("%%%%%%Modification "+s);
                SelectedNode newNode = node.splitYourself();
                stringModification = s;
                try {
                    System.out.println("===Modify text: " + processANode(newNode, 0));
                } catch (Throwable t) {

                    System.err.println("Encountered error, but moving on");
                    t.printStackTrace();
                }
                newNode.addDescription(stringModification.getDescription());
                result.add(newNode);
            }

        }
        return result;
    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "ModifyText";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new ModifyText(plan, e);
            }
        };
    }
}
