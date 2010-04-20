/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LoadProject.java
 *
 * Created on 22-mar-2010, 14.01.02
 */

package ch.epfl.dslab.conferrng.wizard;

import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.FileResolver;
import ch.epfl.dslab.conferrng.gui.Utils;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelNavResult;

/**
 *
 * @author lokeller
 */
public class LoadProject extends WizardPage {


    private FaultInjectionPlan faultInjectionPlan;
    public static final String PROP_FAULTINJECTIONPLAN = "faultInjectionPlan";

    public void setFaultInjectionPlan(FaultInjectionPlan faultInjectionPlan) {
        this.faultInjectionPlan = faultInjectionPlan;
    }

    public static String getDescription() {
        return "Load injection plan";
    }


    /** Creates new form LoadProject */
    public LoadProject() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        buttonGroup1 = new javax.swing.ButtonGroup();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Create new fault injection plan");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Load existing injection plan");

        jButton1.setText("...");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jButton1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
                            .addComponent(jRadioButton2))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButton1)
                        .addGap(175, 175, 175))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(215, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        jTextField1.setText(Utils.loadFile(null, new FileResolver(), "Select file to load", "plan", "Fault Injection Project (*.plan)", false));

    }//GEN-LAST:event_jButton1ActionPerformed




    
    @Override
    public WizardPanelNavResult allowNext(java.lang.String stepName, java.util.Map settings, Wizard wizard) {

        if ( jRadioButton2.isSelected()) {
            loadFile();
        }

        return WizardPanelNavResult.PROCEED;

    }

    private void loadFile() {
       try {

            String sel = jTextField1.getText();

            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(sel);

            // create a resolver that will be used to check the JAR files
            FileResolver resolver = new FileResolver(faultInjectionPlan.loadBaseDir(doc),
                                                        new File(sel).getParent());


            // load jar names from the file and check that they are available
            Vector<String> jars = faultInjectionPlan.loadJars(doc);
            Vector<String> jarsOut = new Vector<String>();

            for (String jar : jars) {
                try {

                    // try to open the jar, if success add it to the list of jars that we want to load in the project
                    ZipFile zip = new ZipFile(resolver.getAbsolutePath(jar));
                    System.out.println(resolver.getAbsolutePath(jar));
                    jarsOut.add(jar);

                } catch (Exception ex) {
                    // there was an error loading the jars, prompt the user for a new file

                    int ret = JOptionPane.showConfirmDialog(this,
                            "Unable to load " + jar + ". Do you want to provide a new location for the file?",
                            "JAR not found",
                            JOptionPane.YES_NO_OPTION);

                    if ( ret == JOptionPane.YES_OPTION) {

                        String file = Utils.loadFile(this, faultInjectionPlan, "Alternative location for " + jar, "jar", "JAR file", false);

                        // if the user didn't cancel add the inserted jar in the list of jars we want to load in the project
                        if (file != null) {
                            jarsOut.add(resolver.getRelativePath(file));
                        }

                    }
                }
            }

            // finally load the project with the updated list of jars
            faultInjectionPlan.loadFromFile(sel, jarsOut);

        } catch (JDOMException ex) {
            Logger.getLogger(LoadProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadProject.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    protected String validateContents(Component component, Object event) {

        if ( ! jRadioButton1.isSelected() &&
                ! (new File(jTextField1.getText()).exists() )) {

                return "Invalid file";
        }

        return null;
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}