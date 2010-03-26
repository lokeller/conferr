/*

Copyright (c) 2008, Dependable Systems Lab, EPFL
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, 
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, 
      this list of conditions and the following disclaimer in the documentation 
      and/or other materials provided with the distribution.
    * Neither the name of the Dependable Systems Lab, EPFL nor the names of its 
      contributors may be used to endorse or promote products derived from this 
      software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
package conferr.gui;

import conferr.*;
import conferr.faultdesc.FaultSpace;
import conferr.gui.JDisablingPanel;
import conferr.gui.FaultScenarioSetTreeModel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableColumn;
import javax.xml.transform.TransformerException;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author  lokeller
 */
public class MainFrame extends javax.swing.JFrame {
    
    /** Creates new form MainFrame */
    public MainFrame() {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            System.out.println("Unable to load native look and feel");
        }
        
        loadRecentFilesList();
        initComponents();

        System.setErr(new PrintStream(new TextAreaOutputStream(jTextArea1, System.err)));
        System.setOut(new PrintStream(new TextAreaOutputStream(jTextArea1, System.out)));
    }

    private Vector<String> recentFiles = new Vector<String>();
    private HashMap<JMenuItem, String> recentFilesMenuItems = new HashMap<JMenuItem, String>();    
    
    private void addRecentFile(String file) {
        
        if (recentFiles.contains(file)) return;
        
        ActionListener open = new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                try {
                    javax.swing.JMenuItem menu = (JMenuItem) arg0.getSource();

                    String file = recentFilesMenuItems.get(menu);

                    plan.loadFromFile(file);
                    
                } catch (JDOMException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        };
        
        if (recentFiles.size() > 5) {            
            for (Map.Entry entry : recentFilesMenuItems.entrySet()) {
                if (entry.getValue().equals(recentFiles.get(5))) 
                    recentFilesMenuItems.remove(entry.getKey());
            }
            recentFiles.remove(5);
        }
        
        recentFiles.add(0, file);
        
        JMenuItem item = new JMenuItem(file);
        item.addActionListener(open);
        
        recentFilesMenuItems.put(item, file);
    }
    
    private void saveRecentFilesList() {
        Preferences myPreferences = Preferences.userNodeForPackage(this.getClass());                        
        for (int i = 0 ; i < 5 && i < recentFiles.size(); i++) {
                                   
            myPreferences.put("file" + i, recentFiles.get(i));                
                        
        }
    }
    
    private void loadRecentFilesList() {
        Preferences myPreferences = Preferences.userNodeForPackage(this.getClass());                
        for (int i = 5 ; i > -1 ; i--) {
            
            String file = myPreferences.get("file" + i, "");                
            
            if (!file.equals("")) addRecentFile(file);
            
        }
    }
    
    private void updateRecentFilesMenu() {
                                
        for (JMenuItem m : recentFilesMenuItems.keySet()) {
            jMenu3.remove(m);
        }                
        
        jMenu3.remove(jSeparator2);
        
        int idx = jMenu3.getItemCount() - 1;
        
        for (JMenuItem m : recentFilesMenuItems.keySet()) {
            jMenu3.add(m, idx);
        }
        
        if (recentFiles.size() > 0) jMenu3.add(jSeparator2, jMenu3.getItemCount() - 1);          
        
    }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        plan = new conferr.FaultInjectionPlan();
        faultInjectionEngine1 = new conferr.FaultInjectionEngine();
        scenarioBean1 = new conferr.gui.ScenarioBean();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel20 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        programName = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        baseDirectory = new javax.swing.JTextField();
        btnBaseDirectory = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        filesList = new javax.swing.JList();
        jPanel15 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new JDisablingPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        configHandlerParams = new javax.swing.JTable();
        jPanel8 = new JDisablingPanel();
        viewOriginalConfigFile = new javax.swing.JButton();
        previewParsedFile = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel19 = new JDisablingPanel();
        btnHandler = new javax.swing.JButton();
        btnOutput = new javax.swing.JButton();
        btnInput = new javax.swing.JButton();
        configName = new javax.swing.JTextField();
        configInput = new javax.swing.JTextField();
        configOutput = new javax.swing.JTextField();
        configHandler = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        scenarioSetList = new javax.swing.JComboBox();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel6 = new JDisablingPanel();
        jLabel7 = new javax.swing.JLabel();
        scenarioSetName = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel10 = new JDisablingPanel();
        jPanel11 = new JDisablingPanel();
        jButton8 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        scenariosTree = new javax.swing.JTree();
        jPanel5 = new JDisablingPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        actionParamTable = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        scenarioAction = new javax.swing.JTextField();
        scenarioName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnAction = new javax.swing.JButton();
        jPanel25 = new JDisablingPanel();
        jButton3 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        runner = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        runnerParams = new javax.swing.JTable();
        btnRunner = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        jPanel17 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        outputDir = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        allScenarioSets = new javax.swing.JRadioButton();
        selectedScenarioSet = new javax.swing.JRadioButton();
        executeScenarioSetList = new javax.swing.JComboBox();
        jButton5 = new javax.swing.JButton();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jarList = new javax.swing.JList();
        jPanel24 = new javax.swing.JPanel();
        jButton19 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        menuOpen1 = new javax.swing.JMenuItem();
        menuSave = new javax.swing.JMenuItem();
        menuSaveAs1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        menuExit = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        faultInjectionEngine1.setPlan(plan);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ConfErr - Configuration file error injection tool");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jSplitPane2.setDividerLocation(400);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel20.setLayout(new java.awt.GridBagLayout());

        jPanel9.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Program name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(jLabel1, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plan, org.jdesktop.beansbinding.ELProperty.create("${name}"), programName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(programName, gridBagConstraints);

        jLabel10.setText("Base directory:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(jLabel10, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plan, org.jdesktop.beansbinding.ELProperty.create("${baseDirectory}"), baseDirectory, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(baseDirectory, gridBagConstraints);

        btnBaseDirectory.setText("...");
        btnBaseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaseDirectoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(btnBaseDirectory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel20.add(jPanel9, gridBagConstraints);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jPanel14.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(240, 100));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${configurationFiles}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plan, eLProperty, filesList);
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        bindingGroup.addBinding(jListBinding);

        jScrollPane1.setViewportView(filesList);

        jPanel14.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton1);

        jButton2.setText("Remove");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton2);

        jPanel14.add(jPanel15, java.awt.BorderLayout.SOUTH);

        jSplitPane3.setLeftComponent(jPanel14);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filesList, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jPanel2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        configHandlerParams.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        configHandlerParams.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                configHandlerParamsPropertyChange(evt);
            }
        });
        {
            eLProperty = org.jdesktop.beansbinding.ELProperty.create("${selectedElement.params}");
            org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filesList, eLProperty, configHandlerParams);
            bindingGroup.addBinding(jTableBinding);

            BeanProperty nameP = BeanProperty.create("name");
            BeanProperty typeP = BeanProperty.create("type");
            BeanProperty valueP = BeanProperty.create("value");

            jTableBinding.addColumnBinding(nameP).setColumnName("Name").setEditable(false);
            jTableBinding.addColumnBinding(typeP).setColumnName("Type").setEditable(false);
            jTableBinding.addColumnBinding(valueP).setColumnName("Value");

            jTableBinding.bind();
        }
        jScrollPane2.setViewportView(configHandlerParams);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        viewOriginalConfigFile.setText("View original file...");
        viewOriginalConfigFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewOriginalConfigFileActionPerformed(evt);
            }
        });
        jPanel8.add(viewOriginalConfigFile);

        previewParsedFile.setText("Preview parsed file...");
        previewParsedFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewParsedFileActionPerformed(evt);
            }
        });
        jPanel8.add(previewParsedFile);

        jButton15.setText("View serialized file...");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton15);

        jButton12.setText("Save parsed file...");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton12);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel2.add(jPanel8, gridBagConstraints);

        jPanel19.setLayout(new java.awt.GridBagLayout());

        btnHandler.setText("...");
        btnHandler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHandlerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(btnHandler, gridBagConstraints);

        btnOutput.setText("...");
        btnOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOutputActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(btnOutput, gridBagConstraints);

        btnInput.setText("...");
        btnInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInputActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(btnInput, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filesList, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.name}"), configName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(configName, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filesList, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.input}"), configInput, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(configInput, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filesList, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.output}"), configOutput, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(configOutput, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filesList, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.pluginClass}"), configHandler, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(configHandler, gridBagConstraints);

        jLabel4.setText("Handler:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(jLabel4, gridBagConstraints);

        jLabel3.setText("Output:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(jLabel3, gridBagConstraints);

        jLabel2.setText("Input:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(jLabel2, gridBagConstraints);

        jLabel6.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel19.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(jPanel19, gridBagConstraints);

        jSplitPane3.setRightComponent(jPanel2);

        jPanel1.add(jSplitPane3, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Configuration files", jPanel1);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel21.setLayout(new java.awt.BorderLayout());

        jPanel12.setLayout(new java.awt.GridBagLayout());

        jLabel5.setText("Error generator:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel5, gridBagConstraints);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${errorGenerators}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plan, eLProperty, scenarioSetList);
        bindingGroup.addBinding(jComboBoxBinding);

        scenarioSetList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scenarioSetListActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(scenarioSetList, gridBagConstraints);

        jButton6.setText("Add");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jButton6, gridBagConstraints);

        jButton7.setText("Remove");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jButton7, gridBagConstraints);

        jPanel21.add(jPanel12, java.awt.BorderLayout.NORTH);

        jPanel6.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5), javax.swing.BorderFactory.createEtchedBorder()));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, scenarioSetList, org.jdesktop.beansbinding.ELProperty.create("${selectedItem != null}"), jPanel6, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, scenarioSetList, org.jdesktop.beansbinding.ELProperty.create("${selectedItem.name}"), scenarioSetName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(scenarioSetName, gridBagConstraints);

        jSplitPane1.setDividerLocation(300);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, scenarioSetList, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jPanel10, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel10.setLayout(new java.awt.BorderLayout());

        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel11.setLayout(new java.awt.GridLayout(2, 2, 5, 5));

        jButton8.setText("Add");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton8);

        jButton13.setText("Duplicate");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton13);

        jButton14.setText("Insert");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton14);

        jButton9.setText("Remove");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton9);

        jPanel10.add(jPanel11, java.awt.BorderLayout.SOUTH);

        scenariosTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                scenariosTreeValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(scenariosTree);

        jPanel10.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel10);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, scenarioBean1, org.jdesktop.beansbinding.ELProperty.create("${scenario != null}"), jPanel5, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        actionParamTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Value"
            }
        ));
        actionParamTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                actionParamTablePropertyChange(evt);
            }
        });
        actionParamTable.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                actionParamTableVetoableChange(evt);
            }
        });
        {
            eLProperty = org.jdesktop.beansbinding.ELProperty.create("${scenario.params}");
            org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, scenarioBean1, eLProperty, actionParamTable);
            bindingGroup.addBinding(jTableBinding);

            BeanProperty nameP = BeanProperty.create("name");
            BeanProperty typeP = BeanProperty.create("type");
            BeanProperty valueP = BeanProperty.create("value");

            jTableBinding.addColumnBinding(nameP).setColumnName("Name").setEditable(false);
            jTableBinding.addColumnBinding(typeP).setColumnName("Type").setEditable(false);
            jTableBinding.addColumnBinding(valueP).setColumnName("Value");

            jTableBinding.bind();

        }
        jScrollPane6.setViewportView(actionParamTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(jScrollPane6, gridBagConstraints);

        jLabel9.setText("Template:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(jLabel9, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, scenarioBean1, org.jdesktop.beansbinding.ELProperty.create("${scenario.pluginClass}"), scenarioAction, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(scenarioAction, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, scenarioBean1, org.jdesktop.beansbinding.ELProperty.create("${scenario.name}"), scenarioName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(scenarioName, gridBagConstraints);

        jLabel8.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(jLabel8, gridBagConstraints);

        btnAction.setText("...");
        btnAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(btnAction, gridBagConstraints);

        jButton3.setText("View  fault scenarios...");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel25.add(jButton3);

        jButton20.setText("View fault domain description...");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });
        jPanel25.add(jButton20);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        jPanel5.add(jPanel25, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel5);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jSplitPane1, gridBagConstraints);

        jButton16.setText("Edit transformations...");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton16, new java.awt.GridBagConstraints());

        jPanel21.add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel21, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Error generators", jPanel4);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel12.setText("Runner:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jLabel12, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plan, org.jdesktop.beansbinding.ELProperty.create("${runner}"), runner, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(runner, gridBagConstraints);

        runnerParams.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        runnerParams.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                runnerParamsPropertyChange(evt);
            }
        });
        {
            eLProperty = org.jdesktop.beansbinding.ELProperty.create("${runnerParams}");
            org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plan, eLProperty, runnerParams);
            bindingGroup.addBinding(jTableBinding);

            BeanProperty nameP = BeanProperty.create("name");
            BeanProperty typeP = BeanProperty.create("type");
            BeanProperty valueP = BeanProperty.create("value");

            jTableBinding.addColumnBinding(nameP).setColumnName("Name").setEditable(false);
            jTableBinding.addColumnBinding(typeP).setColumnName("Type").setEditable(false);
            jTableBinding.addColumnBinding(valueP).setColumnName("Value");

            jTableBinding.bind();
        }
        jScrollPane7.setViewportView(runnerParams);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jScrollPane7, gridBagConstraints);

        btnRunner.setText("...");
        btnRunner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunnerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(btnRunner, gridBagConstraints);

        jTabbedPane1.addTab("Runner", jPanel7);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton4.setText("Start");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, outputDir, org.jdesktop.beansbinding.ELProperty.create("${not empty text}"), jButton4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton4);

        jButton10.setText("Stop");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, faultInjectionEngine1, org.jdesktop.beansbinding.ELProperty.create("${running}"), jButton10, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton10);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel3.add(jPanel16, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, faultInjectionEngine1, org.jdesktop.beansbinding.ELProperty.create("${status}"), statusLabel, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(statusLabel, gridBagConstraints);

        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultsTableMouseClicked(evt);
            }
        });
        {
            eLProperty = org.jdesktop.beansbinding.ELProperty.create("${results}");
            JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, faultInjectionEngine1, eLProperty, resultsTable);
            bindingGroup.addBinding(jTableBinding);

            BeanProperty nameP = BeanProperty.create("description");
            BeanProperty valueP = BeanProperty.create("result");

            jTableBinding.addColumnBinding(nameP).setColumnName("Description").setEditable(false);
            jTableBinding.addColumnBinding(valueP).setColumnName("Results").setEditable(false);;

            jTableBinding.bind();

            resultsTable.getColumnModel().getColumn(0).setCellRenderer(new ResultTableRenderer(faultInjectionEngine1));
            resultsTable.getColumnModel().getColumn(1).setCellRenderer(new ResultTableRenderer(faultInjectionEngine1));

        }
        jScrollPane4.setViewportView(resultsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jScrollPane4, gridBagConstraints);

        jPanel17.setLayout(new java.awt.GridBagLayout());

        jLabel13.setText("Progress:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel17.add(jLabel13, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, faultInjectionEngine1, org.jdesktop.beansbinding.ELProperty.create("${percentage}"), jProgressBar1, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel17.add(jProgressBar1, gridBagConstraints);

        jLabel16.setText("ETA (s):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel17.add(jLabel16, gridBagConstraints);

        jLabel15.setMinimumSize(new java.awt.Dimension(20, 20));
        jLabel15.setPreferredSize(new java.awt.Dimension(30, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, faultInjectionEngine1, org.jdesktop.beansbinding.ELProperty.create("${eta}"), jLabel15, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel17.add(jLabel15, gridBagConstraints);

        jLabel11.setText("Output directory:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel17.add(jLabel11, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, faultInjectionEngine1, org.jdesktop.beansbinding.ELProperty.create("${outputDir}"), outputDir, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        outputDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputDirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel17.add(outputDir, gridBagConstraints);

        jButton11.setText("...");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel17.add(jButton11, gridBagConstraints);

        jLabel14.setText("Error generator:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel17.add(jLabel14, gridBagConstraints);

        jPanel13.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(allScenarioSets);
        allScenarioSets.setSelected(true);
        allScenarioSets.setText("All error generators");
        allScenarioSets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allScenarioSetsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(allScenarioSets, gridBagConstraints);

        buttonGroup1.add(selectedScenarioSet);
        selectedScenarioSet.setText("Selected error generators:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(selectedScenarioSet, gridBagConstraints);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${errorGenerators}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plan, eLProperty, executeScenarioSetList);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, selectedScenarioSet, org.jdesktop.beansbinding.ELProperty.create("${selected}"), executeScenarioSetList, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(executeScenarioSetList, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel17.add(jPanel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jPanel17, gridBagConstraints);

        jButton5.setText("Save resilience profile...");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jButton5, gridBagConstraints);

        jTabbedPane1.addTab("Execution", jPanel3);

        jPanel23.setLayout(new java.awt.GridBagLayout());

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${jars}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plan, eLProperty, jarList);
        bindingGroup.addBinding(jListBinding);

        jScrollPane8.setViewportView(jarList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel23.add(jScrollPane8, gridBagConstraints);

        jButton19.setText("Add JAR ...");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });
        jPanel24.add(jButton19);

        jButton21.setText("Add directory...");
        jButton21.setEnabled(false);
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });
        jPanel24.add(jButton21);

        jButton18.setText("Remove");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });
        jPanel24.add(jButton18);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel23.add(jPanel24, gridBagConstraints);

        jLabel17.setText("Plugins JARs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel23.add(jLabel17, gridBagConstraints);

        jTabbedPane1.addTab("Plugins", jPanel23);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel20.add(jTabbedPane1, gridBagConstraints);

        jSplitPane2.setLeftComponent(jPanel20);

        jPanel18.setLayout(new java.awt.BorderLayout());

        jButton17.setText("Clean");
        jButton17.setFocusable(false);
        jButton17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton17.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });
        jPanel22.add(jButton17);

        jPanel18.add(jPanel22, java.awt.BorderLayout.WEST);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jScrollPane3.setViewportView(jTextArea1);

        jPanel18.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jSplitPane2.setRightComponent(jPanel18);

        getContentPane().add(jSplitPane2, java.awt.BorderLayout.CENTER);

        jMenu3.setText("File");

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("New");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        menuOpen1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuOpen1.setText("Open...");
        menuOpen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenActionPerformed(evt);
            }
        });
        jMenu3.add(menuOpen1);

        menuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuSave.setText("Save");
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        jMenu3.add(menuSave);

        menuSaveAs1.setText("Save as...");
        menuSaveAs1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveAsActionPerformed(evt);
            }
        });
        jMenu3.add(menuSaveAs1);
        jMenu3.add(jSeparator1);
        jMenu3.add(jSeparator2);

        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        jMenu3.add(menuExit);

        updateRecentFilesMenu();

        jMenuBar2.add(jMenu3);

        jMenu4.setText("Tools");

        jMenuItem1.setText("Export error generators to library...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem1);

        jMenuItem2.setText("Import error generators from library...");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem2);
        jMenu4.add(jSeparator3);

        jMenuItem3.setText("Open base directory");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed1(evt);
            }
        });
        jMenu4.add(jMenuItem3);

        jMenuItem4.setText("XPath Editor");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);

        jMenuBar2.add(jMenu4);

        setJMenuBar(jMenuBar2);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaseDirectoryActionPerformed
        
        String selectedValue = Utils.loadDirectory(this, plan, "Select the base directory");
        
        if ( selectedValue != null) {
            plan.setBaseDirectory(selectedValue);
        }        
        
}//GEN-LAST:event_btnBaseDirectoryActionPerformed

    private void menuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveAsActionPerformed
        try {

            String sel = Utils.saveFile(this, plan, "Save fault injection plan",".plan" , "Fault injection plans (*.plan)", false);                                
            

            if (sel != null) {
                plan.saveToFile(plan.getAbsolutePath(sel));

                addRecentFile(plan.getAbsolutePath(sel));
                updateRecentFilesMenu();
            }
       
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_menuSaveAsActionPerformed

    private void menuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenActionPerformed
        
        try {
            
            
            String sel = Utils.loadFile(this, plan,"Load fault injection plan" ,".plan" , "Fault injection plan (*.plan)", false);
                                
            if (sel != null) {                
                sel = plan.getAbsolutePath(sel);
                plan.loadFromFile(sel);            
                addRecentFile(sel);
                updateRecentFilesMenu();
            }
            
        } catch (JDOMException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_menuOpenActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        XPathEditorDialog dialog = new XPathEditorDialog(this, true, plan, null);
        
        dialog.setVisible(true);
        
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void btnRunnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunnerActionPerformed
        
        String selectedValue = Utils.loadClass(this, plan, "Choose runner", "Runner", "conferr.Runner");
        
        if (selectedValue != null) 
            plan.setRunner(selectedValue);
    }//GEN-LAST:event_btnRunnerActionPerformed

    private void previewParsedFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewParsedFileActionPerformed
        ConfigurationFile file = plan.getConfigurationFiles().get(filesList.getSelectedIndex());
        
        if (file.getPluginClass() == null || file.getPluginClass().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select an handler");
            return;
        }
        
        try {
                        
            Document doc = file.getDocument();                      
            
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            
            StringWriter wout = new StringWriter();
            
            out.output(doc, wout);
            
            JTextArea area = new JTextArea(wout.getBuffer().toString());
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN,12));
            JScrollPane pane = new JScrollPane(area);
            pane.setPreferredSize(new Dimension(1500,1000));
            
            JOptionPane.showMessageDialog(this, pane, "Parsed file", JOptionPane.PLAIN_MESSAGE);
            
            wout.close();
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);            
            JOptionPane.showMessageDialog(this, "Unable to parse file: " + ex.getMessage());
        }
    }//GEN-LAST:event_previewParsedFileActionPerformed

    private void viewOriginalConfigFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewOriginalConfigFileActionPerformed
        FileReader reader = null;
        try {
            StringBuffer sb = new StringBuffer();
            ConfigurationFile file = plan.getConfigurationFiles().get(filesList.getSelectedIndex());
            reader = new FileReader(plan.getAbsolutePath(file.getInput()));
            
            int ch;
            
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            
            JTextArea area = new JTextArea(sb.toString());
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN,12));
            JScrollPane pane = new JScrollPane(area);
            pane.setPreferredSize(new Dimension(700,600));
            
            JOptionPane.showMessageDialog(this, pane, "File source", JOptionPane.PLAIN_MESSAGE);
            
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_viewOriginalConfigFileActionPerformed

    private void btnHandlerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHandlerActionPerformed
        
        String selectedValue = Utils.loadClass(this, plan, "Choose handler", "Handler", "conferr.Handler");               
               
        if (selectedValue != null)
            plan.getConfigurationFiles().get(filesList.getSelectedIndex()).setPluginClass(selectedValue);
    }//GEN-LAST:event_btnHandlerActionPerformed

    private void btnOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOutputActionPerformed
        
        String sel = Utils.saveFile(this, plan, "Select output file");
        
        if (sel != null) {
            plan.getConfigurationFiles().get(filesList.getSelectedIndex()).setOutput(sel);
        }
    }//GEN-LAST:event_btnOutputActionPerformed

    private void btnInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInputActionPerformed
        
        String selectedValue = Utils.loadFile(this, plan, "Select input file");
                
        if (selectedValue != null) {
            plan.getConfigurationFiles().get(filesList.getSelectedIndex()).setInput(selectedValue);
        }
    }//GEN-LAST:event_btnInputActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (filesList.getSelectedIndex() > -1)
            plan.removeConfigurationFile(plan.getConfigurationFiles().get(filesList.getSelectedIndex()));
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ConfigurationFile file = new ConfigurationFile(plan);
        file.setName("New file");
        plan.addConfigurationFile(file);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (scenarioSetList.getSelectedIndex() > -1) {
            
            plan.removeErrorGenerator(plan.getErrorGenerators().get(scenarioSetList.getSelectedIndex()));
            
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        ErrorGenerator ss = new ErrorGenerator(plan);
        ss.setName("New scenario set");
        
        plan.addErrorGenerator(ss);
        
        scenarioSetList.setSelectedItem(ss);
        
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        
        FaultScenarioSet scenarioSet = Utils.promptForVariables(scenarioBean1.getScenario());
            
        if (scenarioSet == null) return;
        
        TestScenarioDialog dialog = new TestScenarioDialog(this, true, plan, scenarioSet, (ErrorGenerator) scenarioSetList.getSelectedItem());
        
        dialog.setVisible(true);
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActionActionPerformed
        
        String selectedValue = Utils.loadClass(this, plan, "Choose template", "Template", "conferr.FaultTemplate");
        
        if (selectedValue != null) 
            scenarioBean1.getScenario().setPluginClass(selectedValue);
    }//GEN-LAST:event_btnActionActionPerformed

    private void actionParamTableVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_actionParamTableVetoableChange
        // TODO add your handling code here:
    }//GEN-LAST:event_actionParamTableVetoableChange

    private void actionParamTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_actionParamTablePropertyChange
        if (evt.getPropertyName().equals("model")) {
            TableColumn col = actionParamTable.getColumnModel().getColumn(2);  
            col.setCellEditor(new ParameterCellEditor(plan,this, (ErrorGenerator) scenarioSetList.getSelectedItem()));
        }
    }//GEN-LAST:event_actionParamTablePropertyChange

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

         if (scenariosTree.getSelectionPath() != null) {
        
            FaultScenarioSetTreeModel.FaultClassTreeNode node = (FaultScenarioSetTreeModel.FaultClassTreeNode) scenariosTree.getSelectionPath().getLastPathComponent();
                        
            if (node.getParent() != null) {
                FaultTemplate a2 = node.getParent().getValue().getFaultTemplateInstance();

                /* only remove from scenarios that have unbounded number of children */
                if (a2.getMaxChildren() == -1 ) {

                    Vector<FaultScenarioSet> s = new Vector<FaultScenarioSet>(node.getParent().getValue().getChildren());                    
                    s.remove(node.getValue());
                    node.getParent().getValue().setChildren(s);

                }            
            }
        
        }
        
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        
        if (scenariosTree.getSelectionPath() != null) {
        
            FaultScenarioSetTreeModel.FaultClassTreeNode node = (FaultScenarioSetTreeModel.FaultClassTreeNode) scenariosTree.getSelectionPath().getLastPathComponent();
            
            FaultTemplate a = node.getValue().getFaultTemplateInstance();
            if (a != null && a.getMaxChildren() == -1) {
                Vector<FaultScenarioSet> s = new Vector<FaultScenarioSet>(node.getValue().getChildren());                
                s.add(new FaultScenarioSet(plan, node.getValue()));     
                node.getValue().setChildren(s);
            } else if ( node.getParent() != null ){
                FaultTemplate a2 = node.getParent().getValue().getFaultTemplateInstance();
                
                if ( a2 != null && a2.getMaxChildren() == -1 ) {
                    Vector<FaultScenarioSet> s = new Vector<FaultScenarioSet>(node.getParent().getValue().getChildren());
                    s.add(new FaultScenarioSet(plan, node.getParent().getValue()));
                    node.getParent().getValue().setChildren(s);
                }
            }
        
        }
        
    }//GEN-LAST:event_jButton8ActionPerformed

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        
        if(JOptionPane.showConfirmDialog(this, "Do you really want to exit? (unsaved changes will be lost)", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        
            this.dispose();
                       
        }
        
    }//GEN-LAST:event_menuExitActionPerformed

    private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveActionPerformed
        if (plan.getFile() != null) {
            try {
                plan.saveToFile(plan.getFile());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            menuSaveAsActionPerformed(evt);
        }
    }//GEN-LAST:event_menuSaveActionPerformed

    private void outputDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputDirActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_outputDirActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        if (allScenarioSets.isSelected()) {
            faultInjectionEngine1.start(plan.getErrorGenerators());    
        } else {
            
            Vector<ErrorGenerator> set = new Vector<ErrorGenerator>(); 
            
            set.add((ErrorGenerator) executeScenarioSetList.getSelectedItem());
            faultInjectionEngine1.start(set);
        }
        
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        faultInjectionEngine1.stop();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        String sel = Utils.saveFile(this, plan, "Save injection results", ".xml", "XML file (*.xml)");
            
        if (sel != null) {
            faultInjectionEngine1.writeToFile(plan.getAbsolutePath(sel));
        }

    }//GEN-LAST:event_jButton5ActionPerformed

    private void resultsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultsTableMouseClicked
        
        Point p = evt.getPoint();
        int row = resultsTable.rowAtPoint(p);
            
        FaultInjectionResult result = faultInjectionEngine1.getResults().get(row);
        
        
        JEditorPane htmlDisplay = new JEditorPane();
        htmlDisplay.setEditable(false);
        htmlDisplay.setContentType("text/html");
        htmlDisplay.setText(result.getResult().getCompleteLogHtml());
        
        JScrollPane pane = new JScrollPane(htmlDisplay);
        
        pane.setPreferredSize(new Dimension(700,600));
        
        
        JOptionPane.showMessageDialog(this, pane, "Test output", JOptionPane.PLAIN_MESSAGE);            
        
    }//GEN-LAST:event_resultsTableMouseClicked

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        
        String selectedValue = Utils.loadDirectory(this, plan, "Select output directory");
        
        if (selectedValue != null) {
            outputDir.setText(selectedValue);
        }

    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        ConfigurationFile file = plan.getConfigurationFiles().get(filesList.getSelectedIndex());
        
        if (file.getPluginClass() == null || file.getPluginClass().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select an handler");
            return;
        }
        
        try {
                        
            Document doc = file.getDocument();                      
            
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
             
            String sel = Utils.saveFile(this, plan, "Save parsed file", ".xml", "XML file (*.xml)");
            
            if (sel != null) {
                FileWriter w = new FileWriter(plan.getAbsolutePath(sel));

                out.output(doc, w);

                w.close();
            }
            
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);            
            JOptionPane.showMessageDialog(this, "Unable to parse file: " + ex.getMessage());
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        System.out.println("Closed");
        saveRecentFilesList();
        System.exit(0);
    }//GEN-LAST:event_formWindowClosed

    private void scenariosTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_scenariosTreeValueChanged

        if (scenariosTree.getSelectionPath() != null) {
            scenarioBean1.setScenario(((FaultScenarioSetTreeModel.FaultClassTreeNode) scenariosTree.getSelectionPath().getLastPathComponent()).getValue());
        } else {
            scenarioBean1.setScenario(null);
        }
    }//GEN-LAST:event_scenariosTreeValueChanged

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed

        if (scenariosTree.getSelectionPath() != null) {
        
            FaultScenarioSetTreeModel.FaultClassTreeNode node = (FaultScenarioSetTreeModel.FaultClassTreeNode) scenariosTree.getSelectionPath().getLastPathComponent();
                        

            if (node.getParent() != null) {
                FaultTemplate a2 = node.getParent().getValue().getFaultTemplateInstance();

                if ( a2 != null && a2.getMaxChildren() == -1 ) {
                        Vector<FaultScenarioSet> s = new Vector<FaultScenarioSet>(node.getParent().getValue().getChildren());                    
                        s.add(node.getValue().copy());
                        node.getParent().getValue().setChildren(s);
                }            
            }
        }
        
        
    }//GEN-LAST:event_jButton13ActionPerformed

    private void scenarioSetListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scenarioSetListActionPerformed
        if ( scenarioSetList.getSelectedItem() != null) {
            
            ErrorGenerator selectedScenario = (ErrorGenerator) scenarioSetList.getSelectedItem();
            
            scenariosTree.setModel(new FaultScenarioSetTreeModel(selectedScenario.getFaultScenarioSet()));
        } else {
            scenariosTree.setModel(null);
        }
    }//GEN-LAST:event_scenarioSetListActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        
        try {
            
            String sel = Utils.saveFile(this, plan, "Save scenarios to library", ".faultlib", "Scenario set library (*.faultlib)");

            if (sel != null) {
                plan.saveErrorGeneratorsToLibrary(plan.getAbsolutePath(sel));
            }
            
       
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
         try {
            
            String sel = Utils.loadFile(this, plan, "Load error generators from library", ".faultlib", "Scenario set library (*.faultlib)");
            
            if (sel != null) {                
                plan.loadErrorGeneratorsFromLibrary(plan.getAbsolutePath(sel));                                            
            }
            
        } catch (JDOMException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
       if (scenariosTree.getSelectionPath() != null) {
        
            FaultScenarioSetTreeModel.FaultClassTreeNode node = (FaultScenarioSetTreeModel.FaultClassTreeNode) scenariosTree.getSelectionPath().getLastPathComponent();

            FaultScenarioSet s = new FaultScenarioSet(plan, null);
            s.setName("New scenario");
            s.setPluginClass("conferr.templates.RandomSubsetTemplate");

            Vector<FaultScenarioSet> cv = new Vector<FaultScenarioSet>();
            cv.add(node.getValue());
            s.setChildren(cv);
            
            if (node.getParent() != null) {
                                
                Vector<FaultScenarioSet> v = new Vector<FaultScenarioSet>(node.getParent().getValue().getChildren());
                v.set(v.indexOf(node.getValue()), s) ;
                
                node.getParent().getValue().setChildren(v);
                
            } else {
                ((ErrorGenerator) scenarioSetList.getSelectedItem()).setFaultScenarioSet(s);
                scenariosTree.setModel(new FaultScenarioSetTreeModel(s));
            }
        } 
        
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
       ConfigurationFile file = plan.getConfigurationFiles().get(filesList.getSelectedIndex());
        
        if (file.getPluginClass() == null || file.getPluginClass().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select an handler");
            return;
        }
        
        try {
         
         
            Document doc = file.getDocument();                      
         
            StringWriter wout = new StringWriter();
            
            try {
                file.getHandlerInstance().serializeConfiguration(doc, wout, file);
            
                JTextArea area = new JTextArea(wout.getBuffer().toString());
                area.setFont(new Font(Font.MONOSPACED, Font.PLAIN,12));
                JScrollPane pane = new JScrollPane(area);
                pane.setPreferredSize(new Dimension(700,600));

                JOptionPane.showMessageDialog(this, pane, "Serialize file", JOptionPane.PLAIN_MESSAGE);
            } catch (Exception ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);            
                JOptionPane.showMessageDialog(this, "Unable to serialize file: " + ex.getMessage());                
            }
            
            wout.close();
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);            
            JOptionPane.showMessageDialog(this, "Unable to parse file: " + ex.getMessage());
        }
    }//GEN-LAST:event_jButton15ActionPerformed

    private void allScenarioSetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allScenarioSetsActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_allScenarioSetsActionPerformed

    private void jMenuItem3ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed1
        try {
            Runtime.getRuntime().exec(new String[] {"nautilus", plan.getAbsolutePath(plan.getBaseDirectory())});
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed1

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        
        ConfigurationFiltersFrame frame = new ConfigurationFiltersFrame(plan, (ErrorGenerator) scenarioSetList.getSelectedItem());
        
        frame.setVisible(true);
        
    }//GEN-LAST:event_jButton16ActionPerformed

    private void configHandlerParamsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_configHandlerParamsPropertyChange
        if (evt.getPropertyName().equals("model")) {
            TableColumn col = configHandlerParams.getColumnModel().getColumn(2);
            col.setCellEditor(new ParameterCellEditor(plan,this, null));
        }
    }//GEN-LAST:event_configHandlerParamsPropertyChange

    private void runnerParamsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_runnerParamsPropertyChange
      if (evt.getPropertyName().equals("model")) {
            TableColumn col = runnerParams.getColumnModel().getColumn(2);
            col.setCellEditor(new ParameterCellEditor(plan,this, null));
        }
    }//GEN-LAST:event_runnerParamsPropertyChange

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        jTextArea1.setText("");
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed

        String sel = Utils.loadFile(this, plan, "Add plugin JAR", ".jar", "JAR archive (*.jar)");
        
        if (sel != null) {                
            plan.addJar(sel);                                            
        }

    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        if (jarList.getSelectedValue() != null) {
            plan.removeJar((String)jarList.getSelectedValue());
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        try {

            FaultScenarioSet scenarioSet = Utils.promptForVariables(scenarioBean1.getScenario());
            
            if (scenarioSet == null) return;
            
            ErrorGenerator e = (ErrorGenerator) scenarioSetList.getSelectedItem();

            HashMap<String, Document> configs = e.getTransformedConfigurationFiles();

            FaultSpace space = scenarioSet.getFaultTemplateInstance().getDescription(configs, scenarioSet);

            JTextArea area = new JTextArea(space.toString());
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            JScrollPane pane = new JScrollPane(area);
            pane.setPreferredSize(new Dimension(700, 600));

            JOptionPane.showMessageDialog(this, pane, "Serialize file", JOptionPane.PLAIN_MESSAGE);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ImpossibleConfigurationException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        String sel = Utils.loadDirectory(this, plan, "Add plugin directory");
        
        if (sel != null) {                
            plan.addJar(sel);                                            
        }

    }//GEN-LAST:event_jButton21ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        if ( JOptionPane.showConfirmDialog(this, "All unsaved changes will be lost, do you want to continue?", "Create new plan", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            plan.clear();    
        }        
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable actionParamTable;
    private javax.swing.JRadioButton allScenarioSets;
    private javax.swing.JTextField baseDirectory;
    private javax.swing.JButton btnAction;
    private javax.swing.JButton btnBaseDirectory;
    private javax.swing.JButton btnHandler;
    private javax.swing.JButton btnInput;
    private javax.swing.JButton btnOutput;
    private javax.swing.JButton btnRunner;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField configHandler;
    private javax.swing.JTable configHandlerParams;
    private javax.swing.JTextField configInput;
    private javax.swing.JTextField configName;
    private javax.swing.JTextField configOutput;
    private javax.swing.JComboBox executeScenarioSetList;
    private conferr.FaultInjectionEngine faultInjectionEngine1;
    private javax.swing.JList filesList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JList jarList;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuOpen1;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JMenuItem menuSaveAs1;
    private javax.swing.JTextField outputDir;
    private conferr.FaultInjectionPlan plan;
    private javax.swing.JButton previewParsedFile;
    private javax.swing.JTextField programName;
    private javax.swing.JTable resultsTable;
    private javax.swing.JTextField runner;
    private javax.swing.JTable runnerParams;
    private javax.swing.JTextField scenarioAction;
    private conferr.gui.ScenarioBean scenarioBean1;
    private javax.swing.JTextField scenarioName;
    private javax.swing.JComboBox scenarioSetList;
    private javax.swing.JTextField scenarioSetName;
    private javax.swing.JTree scenariosTree;
    private javax.swing.JRadioButton selectedScenarioSet;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton viewOriginalConfigFile;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    
}
