/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.gui.arugula;

import ch.epfl.dslab.conferrng.arugula.Operator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author slv
 */
public class ConfErrTreeCell extends DefaultTreeCellEditor {

    public ConfErrTreeCell(JTree tree) {
        super(tree, new DefaultTreeCellRenderer());
    }

    @Override
    public JComponent getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof Operator) {
            return new ConfErrEditor((Operator) node.getUserObject());
        }
        return new JLabel(node.getUserObject()+ " cannot be edited");
    }
}
