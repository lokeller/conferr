/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ArugulaToolbar.java
 *
 * Created on 11-mar-2010, 18.06.17
 */

package ch.epfl.dslab.conferrng.wizard;

import ch.epfl.dslab.conferrng.arugula.ErrorGenerator;
import ch.epfl.dslab.conferrng.arugula.IOperator.Factory;
import ch.epfl.dslab.conferrng.arugula.Parse;
import ch.epfl.dslab.conferrng.arugula.Serializer;
import ch.epfl.dslab.conferrng.arugula.Transform;
import ch.epfl.dslab.conferrng.engine.ClassFinderBean;
import ch.epfl.dslab.conferrng.engine.FaultInjectionPlan;
import ch.epfl.dslab.conferrng.engine.PluginFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author lokeller
 */
public class ArugulaToolbar extends javax.swing.JPanel implements PropertyChangeListener {

    /** Creates new form ArugulaToolbar */
    public ArugulaToolbar() {
        initComponents();

        operatorsTree.setCellRenderer(new TreeCellRenderer() {

            JLabel l = new JLabel();
            JButton btn = new JButton();

            Font std = l.getFont();
            Font thin = new Font(Font.SANS_SERIF,Font.PLAIN,std.getSize());

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

                if ( node.getParent() == tree.getModel().getRoot() ) {
                    l.setText(value+"");
                    l.setFont(std);
                    l.setHorizontalAlignment(JLabel.LEFT);
                    l.setBackground(new Color(0.85f,0.85f,0.85f));
                    l.setOpaque(true);
                    l.setBorder( new EmptyBorder(5,5,5,5));
                    l.setIcon(null);
                    return l;

                } else {

                    ImageIcon icon;

                    if (node.getParent() == errorGenerators) {
                        icon = new ImageIcon(ErrorGeneratorCellRenderer.class.getResource("resources/error-generator.png"));

                    } else {
                        icon = new ImageIcon(ErrorGeneratorCellRenderer.class.getResource("resources/operator.png"));
                    }

                     if (hasFocus) {
                        btn.setFont(thin);
                        btn.setText(value+"");
                        btn.setIcon(icon);
                        btn.setHorizontalAlignment(JLabel.LEFT);
                        return btn;
                    } else {
                        l.setText(value+"");
                        l.setHorizontalAlignment(JLabel.LEFT);
                        l.setFont(thin);
                        l.setBorder( new EmptyBorder(5,15,5,5));
                        l.setOpaque(false);
                        l.setIcon(icon);
                        return l;
                    }
                }

            }

            }
        );
        


        setupTreeTransferHandler();             
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        operatorsTree = new javax.swing.JTree();
        operatorsTree.setUI(new BasicTreeUI() {

            @Override
            protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
                return new NodeDimensionsHandler() {
                    @Override
                    public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle size) {
                        Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, size);
                        dimensions.x = 0;
                        dimensions.width = 200; //- getRowX(row, depth);
                        return dimensions;
                    }
                };
            }

            @Override
            protected void paintHorizontalLine(Graphics g, JComponent c,
                int y, int left, int right) {
                // do nothing.
            }

            @Override
            protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
                Insets insets, TreePath path) {
                // do nothing.
            }
        });
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);

        operatorsTree.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        operatorsTree.setModel(setupTreeModel());
        operatorsTree.setDragEnabled(true);
        operatorsTree.setRootVisible(false);
        operatorsTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                operatorsTreeMouseExited(evt);
            }
        });
        operatorsTree.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                operatorsTreeMouseMoved(evt);
            }
        });
        jScrollPane1.setViewportView(operatorsTree);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.disabledText"));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Palette");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jLabel1.setOpaque(true);
        add(jLabel1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void operatorsTreeMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_operatorsTreeMouseMoved

        operatorsTree.requestFocusInWindow();

        TreePath closestNode = operatorsTree.getClosestPathForLocation(evt.getX(), evt.getY());

        if (  closestNode != null) {
            operatorsTree.setSelectionPath(closestNode);
        }

    }//GEN-LAST:event_operatorsTreeMouseMoved

    private void operatorsTreeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_operatorsTreeMouseExited
        operatorsTree.setSelectionPath(null);
    }//GEN-LAST:event_operatorsTreeMouseExited

    private DefaultMutableTreeNode parser;
    private DefaultMutableTreeNode transform;
    private DefaultMutableTreeNode serializer;
    private DefaultMutableTreeNode errorGenerators;

    private FaultInjectionPlan plan;

    public FaultInjectionPlan getPlan() {
        return plan;
    }

    public void setPlan(FaultInjectionPlan plan) {

        if ( this.plan != null) {
            this.plan.removePropertyChangeListener(FaultInjectionPlan.PROP_JARS, this);
        }
        this.plan = plan;        

        initListOfAllComponents();
        
        if ( plan != null) {
            this.plan.addPropertyChangeListener(FaultInjectionPlan.PROP_JARS, this);
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        initListOfAllComponents();
    }


    private Map<DefaultMutableTreeNode, String> mapFromNodeToClass = new HashMap<DefaultMutableTreeNode, String>();

    private TreeModel setupTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Operators");
        errorGenerators = new DefaultMutableTreeNode("Error Generators");
        parser = new DefaultMutableTreeNode("Parser");
        transform = new DefaultMutableTreeNode("Transform");
        serializer = new DefaultMutableTreeNode("Serializer");
        root.add(errorGenerators);
        root.add(parser);
        root.add(transform);
        root.add(serializer);
        return new DefaultTreeModel(root);
    }
    

    private void setTree(Class whatType, DefaultMutableTreeNode node, ClassFinderBean finder) {
        node.removeAllChildren();
        for (String s : finder.getClassesNames(whatType.getName())) {
            try {
                Factory f = PluginFactory.newInstance(s, plan, null).getFactory();

                f.setFaultInjectionPlan(plan);

                f.getACopy(null);

                String whatToName = f.getName();

                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(whatToName);

                mapFromNodeToClass.put(newNode, s);
                node.add(newNode);
            } catch ( RuntimeException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initListOfAllComponents() {
        if ( plan == null) return;
        ClassFinderBean finder = new ClassFinderBean(plan);
        mapFromNodeToClass.clear();

        operatorsTree.setModel(setupTreeModel());

        setTree(Serializer.class, serializer, finder);
        setTree(Parse.class, parser, finder);
        setTree(Transform.class, transform, finder);
        setTree(ErrorGenerator.class, errorGenerators, finder);

        
        TreePath root = new TreePath(operatorsTree.getModel().getRoot());
        
        operatorsTree.expandPath(root.pathByAddingChild(serializer));
        operatorsTree.expandPath(root.pathByAddingChild(parser));
        operatorsTree.expandPath(root.pathByAddingChild(transform));
        operatorsTree.expandPath(root.pathByAddingChild(errorGenerators));
        
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree operatorsTree;
    // End of variables declaration//GEN-END:variables

    private void setupTreeTransferHandler() {
        
        TransferHandler tl = new TransferHandler() {

            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {

                Object selectedNode = operatorsTree.getSelectionPath().getLastPathComponent();

                if ( mapFromNodeToClass.containsKey((DefaultMutableTreeNode)selectedNode)) {                    
                    return new StringSelection(mapFromNodeToClass.get((DefaultMutableTreeNode) selectedNode));
                } else {
                    return null;
                }

            }           

        };

        operatorsTree.setTransferHandler(tl);

    }

}