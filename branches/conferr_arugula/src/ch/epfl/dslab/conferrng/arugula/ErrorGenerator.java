/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import ch.epfl.dslab.conferrng.arugula.IOperator.Factory;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.Parameter;
import ch.epfl.dslab.conferrng.engine.PluginFactory;
import ch.epfl.dslab.conferrng.plugins.handlers.ConfigurationCollector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author slv
 */
public class ErrorGenerator extends Operator<ConfigurationWithError, Object>  {

    private static final String INPUT = "configuration";
    private static final String SEED = "seed";

    public void initRandom() {
        try {
            int seed = Integer.valueOf(getParameterValue(SEED));
            RandomNumberGenerator.staticSetInitialSeed(seed);
        } catch (Exception ex) {
        }
    }

    public ErrorGenerator(FaultInjectionPlan plan, Element e) {
        super(plan, e);
        addParameter(new Parameter(INPUT, "", Parameter.FILE));
        addParameter(new Parameter(SEED, "", Parameter.INTEGER));

        // System.err.println(e.getAttributeValue("configuration"));
        initRandom();


    }

    public static void main(String[] args) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();

        Document doc = builder.build("test.xml");

        Element root = doc.getRootElement();
        FaultInjectionPlan plan = new FaultInjectionPlan();
        plan.addJar(new File(".").getAbsolutePath() +File.separator+"dist/ConfErr.jar");
        PluginFactory.setNewJars(plan);
        ErrorGenerator op = (ErrorGenerator) PluginFactory.getOperator(root, plan);
        for (ConfigurationWithError error : op.apply(null)) {
            error.writeConfiguration();
        }
    }

    private void initConfigurationFiles() {
        String separator = "\\s+|,+";
        String input = getParameterValue(INPUT);
        String[] inputs = input.split(separator);

        initialFiles = new HashMap<String, String>();
        for (String s : inputs) {
            System.err.println("Input file: " + s);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(s)));
                String total = "";
                String line;
                while ((line = br.readLine()) != null) {
                    total += line + "\n";
                }
                initialFiles.put(s, total);


            } catch (Exception ex) {
                System.err.println("Could not read file");//Logger.getLogger(ErrorGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private Map<String, String> initialFiles;

    @Override
    public Collection<ConfigurationWithError> apply(Collection<Object> __) {
        initConfigurationFiles();
        if (initialFiles.isEmpty()) {
            return null;
        }
        Configuration configurationFiles = new Configuration(initialFiles);

        ConfigurationCollector collector = new ConfigurationCollector(plan, null);
        int tries = 3;
        try {
            while (tries-- > 0) {
                Collection c = applyToChildren(new Vector(Arrays.asList(configurationFiles)));
                List<ConfigurationWithError> configurations = new Vector<ConfigurationWithError>();
                boolean ok = true;
                for (Object o : c) {
                    if (!(o instanceof Configuration)) {
                        System.err.println("======NOT A CONFIGURATION: Adding a temp terminator");
                        addChild(collector);
                        ok = false;
                        configurationFiles = new Configuration(initialFiles);
                        break;
                    }
                    Configuration config = (Configuration) o;
                    configurations.add(new ConfigurationWithError(config));
                }
                if (ok) {
                    return configurations;
                }
            }
        } finally {
            removeChild(collector);
        }
        return null;
    }

    @Override
    public Factory getProtectedFactory() {
        return new Factory() {

            @Override
            public String getName() {
                return "error-generator";
            }

            @Override
            public Operator getACopy(FaultInjectionPlan plan, Element e) {
                return new ErrorGenerator(plan, e);
            }
        };
    }

    @Override
    public Vector<String> getErrors() {
        Vector<String> ret = super.getErrors();

        if ( getChildren().size() == 0 || !( getChildren().get(0) instanceof Parse) ) {
            ret.add("The first operator has to be a parser");
        }

        if ( getChildren().size() == 0 || !( getChildren().get(getChildren().size() - 1) instanceof Serializer) ) {
            ret.add("The last operator has to be a serializer");
        }

        return ret;


    }

    @Override
    public Class getOutputClass() {
        return ConfigurationWithError.class;
    }

    @Override
    public Class getInputClass() {
        return Object.class;
    }





}
