/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.arugula.IOperator.Factory;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import ch.epfl.dslab.conferrng.plugins.handlers.ConfigurationCollector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 *
 * @author slv
 */
public class SiblingsStaticAnalysis extends Transform {

    public static final String LLVMMODULE = "llvmModule";
    public static final String TARGET = "target";
    private Map<String, String> mapFromOriginalSectionNameToUsedName;

    public SiblingsStaticAnalysis(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        addParameter(new Parameter(LLVMMODULE, "Test.bc", Parameter.FILE));
        mapFromOriginalSectionNameToUsedName = new HashMap<String, String>();
        addParameter(new Parameter(TARGET, "//directive | //section", Parameter.XPATH_EXPRESSION));
    }

    @Override
    public Collection<SelectedNode> applyAfterCheck(Collection<SelectedNode> _input) {
        try {
            if (getParameterValue(LLVMMODULE).equals("")) {
                return _input;
            }
            //Set<String> directives = new HashSet<String>();
            //Set<String> sections = new HashSet<String>();
            Set<String> set = new HashSet<String>();
            Map<String, List<Attribute>> mapFromNameToElement = new HashMap<String, List<Attribute>>();
            XPath xDirectives = XPath.newInstance(getParameterValue(TARGET));
            //XPath xSections = XPath.newInstance("//section");
            Set<Configuration> input = new HashSet<Configuration>();
            for (SelectedNode node : _input) {
                node.addYourConfiguration(input);
                System.err.println("---------------"+node);
            }
            for (Configuration config : input) {
                System.err.println("|||||||||||||||"+config);
                for (Document doc : config.getDocumentIterator()) {
                    for (Object o : xDirectives.selectNodes(doc)) {
                        System.err.println("*********"+o+"   "+o.getClass());
                        if(o instanceof Element){
                            System.err.println(o+" is an XML Element. Please choose anb attribute as the taregt.");
                            continue;
                        }
                        Attribute e = (Attribute) o;
                        String name = e.getValue();
                        String namePrunned = name.replaceAll("[^a-zA-Z0-9]*", "");
                        mapFromOriginalSectionNameToUsedName.put(name, namePrunned);
                        if (!mapFromNameToElement.containsKey(namePrunned)) {
                            mapFromNameToElement.put(namePrunned, new Vector<Attribute>());
                        }
                        mapFromNameToElement.get(namePrunned).add(e);
                        set.add(namePrunned);
                        System.err.println(">> " + namePrunned);
                    }
                    /* for (Object o : xSections.selectNodes(doc)) {
                    Element e = (Element) o;

                    String name = e.getAttributeValue("name")
                    mapFromOriginalSectionNameToUsedName.put(e.getAttributeValue("name"), name);
                    if (!mapFromNameToElement.containsKey(name)) {
                    mapFromNameToElement.put(name, new Vector<Element>());
                    }
                    mapFromNameToElement.get(name).add(e);
                    sections.add(name);
                    System.err.println(">> " + name);
                    }*/
                }
            }
            //directives.addAll(sections);
            Map<String, List<String>> subtituteDirectivesFor = getSiblingsFor(set);
            return getConfigurations(subtituteDirectivesFor, mapFromNameToElement, input);
            /*Set<SelectedNode> result = new HashSet<SelectedNode>();
            for (Configuration conf : resultConfigs) {
            for (Document doc : conf.getDocumentIterator()) {
            SelectedNode s=new SelectedNode(XPathUtils.getXPathUnknown(doc.getRootElement()), doc, conf);
            s.addDescription(doc.getRootElement().getAttributeValue("name"));
            result.add(s);
            }
            }
            return result;*/

        } catch (JDOMException ex) {
            Logger.getLogger(SiblingsStaticAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }

        return _input;

    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return SiblingsStaticAnalysis.class.getSimpleName();
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new SiblingsStaticAnalysis(plan, e);
            }
        };
    }

    public Map<String, List<String>> getSiblingsFor(Collection<String> what) {
        makeInputFile(what);
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        fillMapAfterRunning(result);
        return result;
    }

    private void fillMapAfterRunning(Map<String, List<String>> map) {
        File dir = new File(exeDir);
        try {
            final Process p = Runtime.getRuntime().exec(new String[]{"./siblings", "input", getParameterValue(LLVMMODULE)}, new String[]{}, dir);

            p.waitFor();
            System.err.println("Exist status: " + p.exitValue());
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new FileInputStream(exeDir + "/output"));
            Element e = doc.getRootElement();
            for (Object o : e.getContent()) {
                if (o instanceof Element) {
                    Element original = (Element) o;
                    String originalName = original.getName();
                    System.out.println(originalName + " can be substituted with");
                    map.put(originalName, new Vector<String>());
                    map.get(originalName).add(originalName);
                    //map.get(originalName).add(originalName);
                    for (Object ochild : original.getContent()) {
                        if (ochild instanceof Element) {
                            Element child = (Element) ochild;
                            System.out.println("\t" + child.getName());
                            map.get(originalName).add(child.getName());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SiblingsStaticAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private final String exeDir = ".";

    private void makeInputFile(Collection<String> what) {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(exeDir + "/input"));
            for (String s : what) {
                pw.println(s);
            }
            pw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SiblingsStaticAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Set<SelectedNode> getNodeForConfiguration(Configuration conf, String description) {
        Set<SelectedNode> result = new HashSet<SelectedNode>();
        for (Document doc : conf.getDocumentIterator()) {
            SelectedNode s = new SelectedNode(XPathUtils.getXPathUnknown(doc.getRootElement()), doc, conf);
            if (description != null) {
                s.addDescription(description);
            }
            result.add(s);
        }
        return result;
    }

    private Set<SelectedNode> getConfigurations(Map<String, List<String>> map, Map<String, List<Attribute>> mapNameToElment, Collection<Configuration> input) {
        if (map.isEmpty()) {
            Set<SelectedNode> result = new HashSet<SelectedNode>();
            for (Configuration c : input) {
                result.addAll(getNodeForConfiguration(c, null));
            }
            return result;
        }
        String whatToSubstitute = map.keySet().iterator().next();
        List<String> siblings = map.get(whatToSubstitute);
        map.remove(whatToSubstitute);
        Set<SelectedNode> results = new HashSet<SelectedNode>();
        Set<SelectedNode> temp = getConfigurations(map, mapNameToElment, input);
        ConfigurationCollector collector = new ConfigurationCollector(plan, null);
        Collection<Configuration> tempConfig = collector.apply(temp);
        for (Attribute e : mapNameToElment.get(whatToSubstitute)) {
            System.err.println("Element "+e);
            for (String s : siblings) {
                System.err.println("Sibling "+s);
                for (Configuration conf : tempConfig) {
                    Configuration newConf = conf.copy();
                    Element newE = findElementInConfiguration(e.getParent(), newConf);
                    if(newE==null){
                        continue;
                    }

                    String newName = getNewName(e.getValue(), s);
                    
                    System.err.println("^^^^Replacing " + e.getValue() + " with " + getNewName(e.getValue(), s));
                    newE.setAttribute(e.getName(), newName);
                    results.addAll(getNodeForConfiguration(newConf, conf.getDescription()+" "+e.getValue() + " -> " + newName));
                }
            }
        }
//        for (Configuration conf : tempConfig) {
//            Configuration newConf = conf.copy();
//            results.addAll(getNodeForConfiguration(newConf, null));
//        }
        return results;


    }

    public static void main(String[] args) {
        System.out.println("[A]".replaceAll("[^a-zA-Z0-9]", ""));
        new SiblingsStaticAnalysis(null, null).getSiblingsFor(Arrays.asList("A", "G", "N"));
    }

    private String getNewName(String original, String sibling) {
        String used = mapFromOriginalSectionNameToUsedName.get(original);
        if (used == null) {
            return sibling;
        }
        return original.replace(used, sibling);
    }

    private Element findElementInConfiguration(Element whatToFind, Configuration conf) {
        for (Document doc : conf.getDocumentIterator()) {
            if (SelectedNode.areTheseDocumentsTheSame(doc, whatToFind.getDocument())) {
                String xpath = XPathUtils.getXPath(whatToFind);
                try {
                    XPath path = XPath.newInstance(xpath);
                    Element newE = (Element) path.selectSingleNode(doc);
                    return newE;

                } catch (JDOMException ex) {
                    Logger.getLogger(SiblingsStaticAnalysis.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        return null;
    }
}
