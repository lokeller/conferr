/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package conferr.templates;


import conferr.AbstractFaultTemplate;
import conferr.FaultInjectionResult;
import conferr.FaultScenario;
import conferr.FaultTemplate;
import conferr.faultdesc.AbstractValueSet;
import conferr.FaultScenarioEnumeration;
import conferr.Parameter;
import conferr.faultdesc.Value;
import conferr.FaultScenarioSet;
import conferr.faultdesc.ElementOfInterval;
import conferr.faultdesc.Fault;
import conferr.faultdesc.FaultSpace;
import java.util.Map;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import java.util.HashMap;
import org.jdom.Document;

/**
 *
 * @author lokeller
 */
public class RandomMixTemplate extends AbstractFaultTemplate {


    public static final String sizeString = "size";

    @Override
    public Vector<Parameter> getDefaultParameters() {

        Vector<Parameter> parameters = new Vector<Parameter>();

        parameters.add(new Parameter(sizeString, "", Parameter.INTEGER));

        return parameters;

    }

    private int getSize( FaultScenarioSet scenario ) {
        int size;
        try  {
            size = Integer.parseInt(scenario.getParameterValue(sizeString));
        } catch (NumberFormatException ex) {
            size = 0;
        }

        return size;
    }

    public int getMaxChildren() {
        return -1;
    }

    public String getChildName(int pos) {
        return pos +"";
    }

    public FaultSpace getDescription(Map<String, Document> configs, FaultScenarioSet scenario) {

        FaultSpace count = new FaultSpace("count_" + scenario.getId());

        int max = getSize(scenario);

        count.addSubspace(new ElementOfInterval(0, max - 1), null);

        return count;

    }

    public FaultScenario getFaultScenario(Fault fault, final Map<String, Document>  configs, final FaultScenarioSet scenario) {
        

        Enumeration<FaultScenario> e = faults(configs, 0, scenario);

        int num = (Integer) fault.getObjectByName("count_" + scenario.getId()) - 1;


        final Vector<String> descriptions = new Vector<String>();

        Map<String, Document> docs = configs;

        Random r = new Random(num);

        for ( FaultScenarioSet set : scenario.getChildren()) {

            FaultSpace description = set.getFaultTemplateInstance().getDescription(docs, set);            

            FaultScenario ret = set.getFaultTemplateInstance().getFaultScenario(randomlySelectFault(description, r), docs, set);

            descriptions.add(ret.getDescription());
            docs = ret.getDocument();

        }

        final Map<String, Document> finalDocs = docs;

        return new FaultScenario() {

            public Map<String, Document> getDocument() {
                return finalDocs;
            }

            public FaultTemplate getTemplate() {
                return RandomMixTemplate.this;
            }

            public String getDescription() {
                return descriptions.toString();
            }
        };

    }


    public Fault randomlySelectFault( FaultSpace space, Random rnd) {

        long coeffs = 0;

        for ( Map.Entry<AbstractValueSet, FaultSpace> entry : space.getSubspaces().entrySet() ) {
            if (entry.getValue() != null) {
                coeffs += entry.getKey().size() * entry.getValue().numberOfFaults();
            } else {
                coeffs += entry.getKey().size();
            }

        }

        rnd.nextDouble();

        long val = (long) (rnd.nextDouble() * coeffs);


        for ( Map.Entry<AbstractValueSet, FaultSpace> entry : space.getSubspaces().entrySet() ) {

            if (entry.getValue() != null) {
                val  -= entry.getKey().size() * entry.getValue().numberOfFaults();
            } else {
                val -= entry.getKey().size();
            }

            if (val <= 0) {

                AbstractValueSet set = entry.getKey();
                long el = (long) (set.size() * rnd.nextDouble());
                Value val1 = set.get(el);

                Fault f;

                if ( entry.getValue() == null) {
                    f = new Fault(new HashMap<FaultSpace, Value>());
                } else {
                    f = randomlySelectFault(entry.getValue(), rnd);
                }

                f.getValues().put(space, val1);

                return f;

            }
        }

        throw new RuntimeException("No faults available in one of the children or size of description changed while picking a random value");

    }



}
