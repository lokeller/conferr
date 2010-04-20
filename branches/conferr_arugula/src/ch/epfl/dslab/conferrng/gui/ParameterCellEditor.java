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
package ch.epfl.dslab.conferrng.gui;

import ch.epfl.dslab.conferrng.engine.*;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

/**
 * Cell editor to change plugin parameters, depending on the parameter type the
 * editor has different behaviours.
 */
public class ParameterCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private JTextField text;
    private JLabel label;
    private JCheckBox checkbox;
    private JButton button;
    private JPanel panel;
    private FaultInjectionPlan plan;
    private String currentType;
    private GridBagConstraints textConstr;

    public ParameterCellEditor(final FaultInjectionPlan plan) {

        this.plan = plan;

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        label = new JLabel();

        text = new JTextField();
        text.setBorder(new EmptyBorder(0, 0, 0, 0));

        textConstr = new GridBagConstraints();

        textConstr.fill = GridBagConstraints.BOTH;
        textConstr.gridx = 0;
        textConstr.gridy = 0;
        textConstr.weightx = 1.0;
        textConstr.weighty = 1.0;

        panel.add(text, textConstr);

        button = new JButton("...");

        button.addActionListener(this);

        GridBagConstraints btnConstr = new GridBagConstraints();

        btnConstr.gridx = 1;
        btnConstr.gridy = 0;
        btnConstr.fill = GridBagConstraints.VERTICAL;
        btnConstr.weighty = 1.0;

        panel.add(button, btnConstr);

        checkbox = new JCheckBox();

        final JPopupMenu menu = new JPopupMenu();
        JMenuItem mi = new JMenuItem("Edit...");
        mi.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                try {
                    Runtime.getRuntime().exec("gedit " + plan.getAbsolutePath(text.getText()));
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        });
        menu.add(mi);

        text.addMouseListener(new MouseAdapter() {

            private void maybeShowPopup(MouseEvent event) {

                if (event.isPopupTrigger()) {
                    if (currentType.equals(Parameter.FILE)) {
                        menu.show(event.getComponent(), event.getX(), event.getY());
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
                maybeShowPopup(arg0);
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
                maybeShowPopup(arg0);
            }
        });



    }

    @Override
    public Object getCellEditorValue() {

        if (currentType == null) {
            return null;
        }

        if (currentType.equals(Parameter.BOOLEAN)) {
            return checkbox.isSelected() + "";
        }
        if (currentType.equals(Parameter.CONFIGURATION_FILE)) {
            return label.getText();
        } else {
            return text.getText();
        }

    }
    private String classToLoad;

    @Override
    public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int arg3, int arg4) {
        currentType = (String) arg0.getValueAt(arg3, 1);


        if (currentType.equals(Parameter.BOOLEAN)) {
            checkbox.setSelected(Boolean.parseBoolean((String) arg1));
            return checkbox;
        } else if (currentType.equals(Parameter.CONFIGURATION_FILE)) {
            label.setText((String) arg1);
            panel.remove(text);
            panel.add(label, textConstr);
            return panel;
        } else if (currentType.equals(Parameter.XPATH_EXPRESSION)) {
            text.setText((String) arg1);
            panel.remove(label);
            panel.add(text, textConstr);
            return panel;

        } else if (currentType.equals(Parameter.FILE)
                || currentType.equals(Parameter.DIRECTORY)) {

            text.setText((String) arg1);

            panel.remove(label);
            panel.add(text, textConstr);
            return panel;
        } else if (currentType.equals(Parameter.CLASS_FILE)) {
            classToLoad = arg1.toString();
            text.setText((String) arg1);

            panel.remove(label);
            panel.add(text, textConstr);
            return panel;
        } else {
            text.setText((String) arg1);
            return text;
        }


    }

    @Override
    public void actionPerformed(ActionEvent arg0) {


        if (currentType.equals(Parameter.XPATH_EXPRESSION)) {
            String sel = Utils.getXPath(plan);

            if (sel != null) {
                text.setText(sel);
            }
        } else if (currentType.equals(Parameter.FILE)) {

            String sel = Utils.loadFile(panel, plan, "Choose file");

            if (sel != null) {
                text.setText(text.getText() + sel + " ");
            }

        } else if (currentType.equals(Parameter.DIRECTORY)) {

            String sel = Utils.loadDirectory(panel, plan, "Choose directory");

            if (sel != null) {
                text.setText(sel);
            }

        } else if (currentType.equals(Parameter.CLASS_FILE)) {
            String sel = Utils.loadClass(panel, plan, "Select a class", "Class selection", classToLoad);
            if (sel != null) {
                text.setText(sel);
            }
        }
    }


}
