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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Model to show in a tree the templates that compose an error generator.
 */

public class FaultScenarioSetTreeModel implements TreeModel  {
    
    private FaultClassTreeNode root;
    
    private Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();
    
    public class FaultClassTreeNode implements PropertyChangeListener {
                
        private FaultScenarioSet value;
        private FaultClassTreeNode parent;
        private int pos;
        
        private Vector<FaultClassTreeNode> children = new Vector<FaultScenarioSetTreeModel.FaultClassTreeNode>();

        public FaultClassTreeNode(FaultScenarioSet value, FaultScenarioSetTreeModel.FaultClassTreeNode parent, int pos) {
            this.value = value;
            this.parent = parent;
            this.pos = pos; 
            if (value != null)
                value.addPropertyChangeListener(this);
            
            loadChildren();
        }                    
        
        private void loadChildren() {
            
            for (FaultClassTreeNode t: getChildren()) {
                    t.deregister();
            }            
            
            getChildren().clear();
             if (getValue() != null && getValue().getFaultTemplateInstance() != null) {
                for (int i = 0 ; i < getValue().getFaultTemplateInstance().getMaxChildren() || i < getValue().getChildren().size(); i++) {
                    if (getValue().getChildren().size() > i) {
                        children.add(new FaultClassTreeNode(getValue().getChildren().get(i), this, i));
                    } else {
                        getChildren().add(new FaultClassTreeNode(null, this, i));
                    }
                }
            
            }
        }
        
        
        @Override
        public String toString() {
            if (getParent() != null) {
                FaultTemplate a = getParent().getValue().getFaultTemplateInstance();
                if (a != null) {
                    if ( getValue() != null) {
                        return  a.getChildName(pos) + ": " + getValue().getName();
                    } else {
                          return  a.getChildName(pos) + ": not set";
                    }
                }
                else throw new RuntimeException();
            } else return getValue().getName();
        }

        public void propertyChange(PropertyChangeEvent arg0) {
            if (arg0.getPropertyName().equals("children"))  {
                
                loadChildren();
                
                FaultScenarioSetTreeModel.this.structureChange(this);
            } else if ( arg0.getPropertyName().equals("name")) 
                FaultScenarioSetTreeModel.this.nodeChange(this);
        }                
        
        public void deregister() {
            for ( FaultClassTreeNode n : getChildren()) {
                n.deregister();               
            }
            if (getValue() != null) getValue().removePropertyChangeListener(this);
        }

        public FaultScenarioSetTreeModel.FaultClassTreeNode getParent() {
            return parent;
        }

        public int getPos() {
            return pos;
        }
        
        public FaultScenarioSet getValue() {
            return value;
        }

        public void setValue(

        FaultScenarioSet value) {
            this.value = value;
        }


        public void setParent(FaultClassTreeNode parent) {
            this.parent = parent;
        }


        public Vector<FaultClassTreeNode> getChildren() {
            return children;
        }

        public void setChildren(Vector<FaultClassTreeNode> children) {
            this.children = children;
        }
        
    }

    public FaultScenarioSetTreeModel(FaultScenarioSet root) {
        this.root = new FaultClassTreeNode(root, null, 0);        
    }                
    
    public Object getRoot() {
        return root;
    }

    public Object getChild(Object arg0, int arg1) {
        return ((FaultClassTreeNode) arg0).getChildren().get(arg1);        
    }

    public int getChildCount(Object arg0) {        
        return ((FaultClassTreeNode) arg0).getChildren().size();
    }

    public boolean isLeaf(Object arg0) {        
        if (((FaultClassTreeNode) arg0).getValue() == null) return true;
        FaultTemplate a = ((FaultClassTreeNode) arg0).getValue().getFaultTemplateInstance();
        if (a == null) return true;
        else return a.getMaxChildren() == 0;
    }

    public void valueForPathChanged(TreePath arg0, Object arg1) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public int getIndexOfChild(Object arg0, Object arg1) {
        FaultClassTreeNode n = (FaultClassTreeNode) arg0;
        return n.getParent().getValue().getChildren().indexOf(n.getValue());
    }

    public void addTreeModelListener(TreeModelListener arg0) {
        listeners.add(arg0);
    }

    public void removeTreeModelListener(TreeModelListener arg0) {
        listeners.remove(arg0);
    }

    public void structureChange(FaultClassTreeNode n) {        
                
        Vector v = new Vector();
        
        while (n != null) {
            v.insertElementAt(n, 0);
            n = n.getParent();
        }
        
        for (TreeModelListener l : listeners) {                        
            l.treeStructureChanged(new TreeModelEvent(this, v.toArray()));            
        }
        
    }

    public void nodeChange(FaultClassTreeNode n) {        
        
        Vector v = new Vector();
        
        while (n != null) {
            v.insertElementAt(n, 0);
            n = n.getParent();
        }
        
        for (TreeModelListener l : listeners) {                        
            l.treeNodesChanged(new TreeModelEvent(this, v.toArray()));            
        }
        
    }
    
}
