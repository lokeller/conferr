/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.wizard;

import ch.epfl.dslab.conferrng.engine.ClassFinderBean;
import ch.epfl.dslab.conferrng.engine.FaultInjectionEngine;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.WizardBranchController;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.Wizard;

/**
 *
 * @author lokeller
 */
public class InjectionWizardController extends WizardBranchController {

    private FaultInjectionEngine faultInjectionEngine;
    public static final String PROP_FAULTINJECTIONENGINE = "faultInjectionEngine";

    /**
     * Get the value of faultInjectionEngine
     *
     * @return the value of faultInjectionEngine
     */
    public FaultInjectionEngine getFaultInjectionEngine() {
        return faultInjectionEngine;
    }

    /**
     * Set the value of faultInjectionEngine
     *
     * @param faultInjectionEngine new value of faultInjectionEngine
     */
    public void setFaultInjectionEngine(FaultInjectionEngine faultInjectionEngine) {
        FaultInjectionEngine oldFaultInjectionEngine = this.faultInjectionEngine;
        this.faultInjectionEngine = faultInjectionEngine;
        propertyChangeSupport.firePropertyChange(PROP_FAULTINJECTIONENGINE, oldFaultInjectionEngine, faultInjectionEngine);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    private static LoadProject loadProject = new LoadProject();
    private static ErrorGenerators errorGeneratorConfig = new ErrorGenerators();
    private static Runner runner = new Runner();
    private static ExperimentsOverview overview = new ExperimentsOverview();
    private static ReportPath reportPath = new ReportPath();
    private static SaveProject saveProject = new SaveProject();
    private static ExperimentExecution experimentExecution = new ExperimentExecution();

    public InjectionWizardController() {
        super(new WizardPage[]{
                    loadProject,
                    errorGeneratorConfig,
                    overview,
                    runner,
                    saveProject,
                    experimentExecution
                });


        initPlan();
        initInjectionEngine();
        System.err.println("+++++++++++++++++++++" + faultInjectionPlan);
        loadProject.setFaultInjectionPlan(faultInjectionPlan);
        errorGeneratorConfig.setFaultInjectionPlan(faultInjectionPlan);
        runner.setInjectionParent(this);
        reportPath.setInjectionPArent(this);
        saveProject.setFaultInjectionPlan(faultInjectionPlan);
        overview.setFaultInjectionPlan(faultInjectionPlan);
        experimentExecution.setFaultInjectionEngine(faultInjectionEngine);
        experimentExecution.setFaultInjectionPlan(faultInjectionPlan);
    }

    private void initPlan() {
        FaultInjectionPlan plan = new FaultInjectionPlan();
        plan.addJar(userJar);
        System.out.println("USER = " + userJar);
        setFaultInjectionPlan(plan);

    }
    private static final String userJar = new File(".").getAbsolutePath() +File.separator+"dist/ConfErr.jar"; //System.getProperty("user.home") +File.separator+ ".conferr/plugins.jar";

    private void initInjectionEngine() {
        faultInjectionEngine = new FaultInjectionEngine();
    }

    @Override
    protected Wizard getWizardForStep(String step, Map settings) {
        return WizardPage.createWizard(new WizardPage[]{new Finished()});
    }

    public static void main(String[] args) {
        WizardBranchController wzc = new InjectionWizardController();
        Wizard wz = wzc.createWizard();
        WizardDisplayer.showWizard(wz, new Rectangle(400, 400, 1200, 600));
    }
    protected FaultInjectionPlan faultInjectionPlan;
    public static final String PROP_FAULTINJECTIONPLAN = "faultInjectionPlan";

    /**
     * Get the value of faultInjectionPlan
     *
     * @return the value of faultInjectionPlan
     */
    public FaultInjectionPlan getFaultInjectionPlan() {
        return faultInjectionPlan;
    }

    /**
     * Set the value of faultInjectionPlan
     *
     * @param faultInjectionPlan new value of faultInjectionPlan
     */
    public void setFaultInjectionPlan(final FaultInjectionPlan faultInjectionPlan) {
        FaultInjectionPlan oldFaultInjectionPlan = this.faultInjectionPlan;
        this.faultInjectionPlan = faultInjectionPlan;
        classFinder = new ClassFinderBean(faultInjectionPlan);
        propertyChangeSupport.firePropertyChange(PROP_FAULTINJECTIONPLAN, oldFaultInjectionPlan, faultInjectionPlan);
    }
    private String targetNodes;
    public static final String PROP_TARGETNODES = "targetNodes";

    /**
     * Get the value of selectedNodes
     *
     * @return the value of selectedNodes
     */
    public String getTargetNodes() {
        return targetNodes;
    }

    /**
     * Set the value of selectedNodes
     *
     * @param selectedNodes new value of selectedNodes
     */
    public void setTargetNodes(String targetNodes) {
        String oldSelectedNodes = this.targetNodes;
        this.targetNodes = targetNodes;
        propertyChangeSupport.firePropertyChange(PROP_TARGETNODES, oldSelectedNodes, targetNodes);
    }
    private String sourceNodes;
    public static final String PROP_SOURCENODES = "sourceNodes";

    /**
     * Get the value of sourceNodes
     *
     * @return the value of sourceNodes
     */
    public String getSourceNodes() {
        return sourceNodes;
    }

    /**
     * Set the value of sourceNodes
     *
     * @param sourceNodes new value of sourceNodes
     */
    public void setSourceNodes(String sourceNodes) {
        String oldSourceNodes = this.sourceNodes;
        this.sourceNodes = sourceNodes;
        propertyChangeSupport.firePropertyChange(PROP_SOURCENODES, oldSourceNodes, sourceNodes);
    }
    private ClassFinderBean classFinder;

    public List<String> getClassesForInterface(Class<?> cls) {
        return classFinder.getClassesNames(cls.getName());
    }

    public List<String> getClassesForInterface(String cls) {
        System.err.println("Get classes for: " + cls);
        return classFinder.getClassesNames(cls);
    }
}
