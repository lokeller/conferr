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

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;

/**
 * Model used to show a jdom tree.
 */

public class JDomDocumentTree implements TreeModel {

    private Document doc;

    JDomDocumentTree(Document doc) {
        this.doc = doc;
    }
    
    public Object getRoot() {
        return doc.getRootElement();
    }

    public Object getChild(Object arg0, int arg1) {
        if ( arg0 instanceof  Element) {
            Element el = (Element) arg0;
            if (arg1 < el.getAttributes().size()) return el.getAttributes().get(arg1);
            else return el.getContent().get( arg1 - el.getAttributes().size());
        } else if ( arg0 instanceof Parent) {
            return ((Parent) arg0).getContent().get(arg1);
        } else {
            return null;
        }
    }

    public int getChildCount(Object arg0) {
        if ( arg0 instanceof  Element) {
            Element el = (Element) arg0;
            return el.getContentSize() + el.getAttributes().size();
        } else if ( arg0 instanceof Parent) {
            return ((Parent) arg0).getContent().size();
        } else {
            return 0;
        }
        
    }

    public boolean isLeaf(Object arg0) {                        
        return arg0 instanceof Attribute;
    }

    public void valueForPathChanged(TreePath arg0, Object arg1) {        
    }

    public int getIndexOfChild(Object arg0, Object arg1) {
        if (arg1 instanceof Attribute) {
            return ((Attribute)arg1).getParent().getAttributes().indexOf(arg1);
        } else if ( arg0 instanceof Element) {            
            return ((Parent) arg0).getContent().indexOf(arg1) + ((Element) arg0).getAttributes().size();
        } else {
            return ((Parent) arg0).getContent().indexOf(arg1);
        }
    }

    public void addTreeModelListener(TreeModelListener arg0) {        
    }

    public void removeTreeModelListener(TreeModelListener arg0) {        
    }

}
