/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng;

import ch.epfl.dslab.conferrng.engine.FaultInjectionEngine;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.InjectionResult;
import ch.epfl.dslab.conferrng.wizard.InjectionWizardController;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author slv
 */
public class ConfErr {

    public interface ConfErrInterface {

        void run(List<String> parameters);
    }

    public static class NoGUIConfErrInterface implements ConfErrInterface {

        @Override
        public void run(List<String> parameters) {
            FaultInjectionPlan faultInjectionPlan = new FaultInjectionPlan();
            try {
                String project = null;
                for (String s : parameters) {
                    if (s.contains(".plan")) {
                        project = s;
                        break;
                    }
                }
                if (project == null) {
                    throw new RuntimeException("You need to specify a plan to execute. A plan has the \".plan\" extension");
                }
                faultInjectionPlan.loadFromFile(project);
            } catch (Exception ex) {
                Logger.getLogger(ConfErr.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Failed to load the plan. Please check it for correctness.");
            }

            FaultInjectionEngine engine = new FaultInjectionEngine();
            engine.setPlan(faultInjectionPlan);
            faultInjectionPlan.computeConfigurationWithErrors();
            engine.start(faultInjectionPlan.getErrorGenerators());
            while (engine.isRunning()) {
                System.err.println("Waiting for the engine to finish ");
                System.err.println("ETA: " + engine.getEta());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConfErr.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.err.println("----DONE---");
            System.err.println("----THE RESILIENCE PROFILE---");
            List<InjectionResult> results = engine.getResults();
            Element element = new Element("ResilienceProfile");
            for (InjectionResult result : results) {
                element.addContent(result.toXML());
            }
            Document doc = new Document(element);
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            try {
                outputter.output(doc, System.err);
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static class GUIConfErrInterface implements ConfErrInterface {

        @Override
        public void run(List<String> parameters) {
            InjectionWizardController.main(parameters.toArray(new String[]{}));
        }
    }

    public static void main(String[] args) {
        ConfErrInterface ui = null;
        args = new String[]{"nogui--", "RSyncTest.plan"};
        List<String> params = new Vector<String>();
        for (String s : args) {
            System.err.println("Parameter " + s);
            String stripped = s.replaceAll("[^a-zA-Z0-9.]+", "");
            System.err.println("\tStripped = "+stripped);
            params.add(stripped);
        }
        if (params.contains("nogui")) {
            ui = new NoGUIConfErrInterface();
        } else {
            ui = new GUIConfErrInterface();
        }
        ui.run(params);
    }
}
