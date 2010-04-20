/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.wizard;

import ch.epfl.dslab.conferrng.arugula.ErrorGenerator;
import ch.epfl.dslab.conferrng.arugula.Operator;
import java.awt.Component;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author lokeller
 */
public class ErrorGeneratorCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component ret = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof Operator ) {

            Vector<String> errors = ((Operator) value).getErrors();

            if (value instanceof ErrorGenerator) {            
                if ( errors.size() > 0) {
                    this.setIcon(new ImageIcon(ErrorGeneratorCellRenderer.class.getResource("resources/error-generator-error.png")));
                } else {
                    this.setIcon(new ImageIcon(ErrorGeneratorCellRenderer.class.getResource("resources/error-generator.png")));
                }

            } else {
                if ( errors.size() > 0 ) {
                    this.setIcon(new ImageIcon(ErrorGeneratorCellRenderer.class.getResource("resources/operator-error.png")));
                } else {
                    this.setIcon(new ImageIcon(ErrorGeneratorCellRenderer.class.getResource("resources/operator.png")));
                }


            }

            if ( errors.size() > 0) {

                String msg = "<html><ul>";
                for(String s : errors) {
                    msg += "<li>"+ s +"</li>";
                }
                msg += "</ul>";

                setToolTipText(msg);
            } else {
                setToolTipText(null);
            }

        }



        return ret;

    }



}
