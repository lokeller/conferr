/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * InjectionSurface.java
 *
 * Created on 19-gen-2010, 14.30.50
 */
package ch.epfl.dslab.conferrng.gui;

import ch.epfl.dslab.conferrng.arugula.Configuration;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.tree.JGraphCompactTreeLayout;

import ch.epfl.dslab.conferrng.gui.graph.GraphFactory;
import ch.epfl.dslab.conferrng.gui.graph.GraphVertex;
import java.awt.Dimension;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.SwingConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.Edge;
import org.jgrapht.ext.JGraphModelAdapter;

/**
 *
 * @author lokeller
 */
public class XPathSelectDialog extends JDialog {

    public String getTheXPathExpression() {
        return XPathExpression.getText();
    }

    private JGraph getTheGraph() {
        JGraph jGraph = new JGraph();
        jGraph.setEditable(false);
        final List<Object> currentSelection = new Vector<Object>();
        jGraph.addGraphSelectionListener(new GraphSelectionListener() {

            @Override
            public void valueChanged(GraphSelectionEvent arg0) {
                System.out.println("Graph selection changed");
                for (Object o : arg0.getCells()) {
                    System.err.println("Class: " + o.getClass());
                    if (o instanceof Edge) {
                        continue;
                    }
                    if (arg0.isAddedCell(o)) {
                        currentSelection.add(o);
                    } else {
                        currentSelection.remove(o);
                    }

                }
                updateTheXPathExpression();
            }

            private void updateTheXPathExpression() {
                String formula = "";
                int size = currentSelection.size();
                for (int i = 0; i < size; i++) {
                    formula += ((GraphVertex) ((DefaultGraphCell) currentSelection.get(i)).getUserObject()).getXPath();
                    if (i != (size - 1)) {
                        formula += " | ";

                    }
                }

                XPathExpression.setText(formula);
            }
        });
        return jGraph;
    }

    private void updateTheGraph(Document file) {
        System.out.println("Changed the graph");
        configurationGraph.removeAll();
        configurationGraph.setModel(new JGraphModelAdapter(GraphFactory.createGraph(file)));
        JGraphFacade facade = new JGraphFacade(configurationGraph);
        JGraphCompactTreeLayout layout = new JGraphCompactTreeLayout();
        layout.setOrientation(SwingConstants.NORTH);
        layout.run(facade);
        Map nested = facade.createNestedMap(true, true);
        configurationGraph.getGraphLayoutCache().edit(nested);
    }

    public XPathSelectDialog() {
        initComponents();
    }
    public static final String ROOT = "_root_";

    /** Creates new form InjectionSurface */
    public XPathSelectDialog(Configuration config) {
        super.setSize(new Dimension(500, 500));
        this.setTitle("Select XPath expression");
        this.setModal(true);
        initComponents();
        Element e = new Element("Configuration");
        e.setAttribute("name", "Configuration");
        Document doc = new Document(e);
        for (Document configDoc : config.getDocumentIterator()) {
            Element newE = (Element) ((Document) configDoc.clone()).getRootElement().detach();
            newE.setAttribute(ROOT, "yes");
            e.addContent(newE);
        }
        updateTheGraph(doc);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        XPathExpression = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        configurationGraph = getTheGraph();
        jButton1 = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText("Please select the part of the configuration where the faults could be injected");

        XPathExpression.setEditable(false);
        XPathExpression.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                XPathExpressionPropertyChange(evt);
            }
        });

        jScrollPane1.setAutoscrolls(true);

        configurationGraph.setDisconnectable(false);
        configurationGraph.setDragEnabled(true);
        configurationGraph.setDropEnabled(false);
        jScrollPane1.setViewportView(configurationGraph);

        jButton1.setText("Done");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(XPathExpression, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE))
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(XPathExpression, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void XPathExpressionPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_XPathExpressionPropertyChange
        if (XPathExpression.getText() == null || XPathExpression.getText().trim().equals("")) {
            // setProblem("Please select a target for the fault injection");
            return;
        }
        // setProblem(null);
    }//GEN-LAST:event_XPathExpressionPropertyChange

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField XPathExpression;
    private javax.swing.ButtonGroup buttonGroup1;
    private org.jgraph.JGraph configurationGraph;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
