/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.gui.arugula;

import ch.epfl.dslab.conferrng.arugula.Operator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author slv
 */
public class ArugulaTreeCellEditor extends DefaultTreeCellEditor {

    public ArugulaTreeCellEditor(JTree tree) {
        super(tree, new DefaultTreeCellRenderer());
    }

    @Override
    public JComponent getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {

        if (value instanceof Operator) {
            return new ConfErrEditor((Operator) value);
        }
        return new JLabel(value +"");
    }


}
