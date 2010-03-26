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
import java.awt.Component;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.transform.TransformerException;
import org.jdom.Document;

/**
 * Renderer of results, it converts the result from an integer to a string and
 * associates a tooltip to the result cells
 * 
 */

public class ResultTableRenderer extends DefaultTableCellRenderer {

    private FaultInjectionEngine engine;
    
    public ResultTableRenderer( FaultInjectionEngine engine ) {
        this.engine = engine;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
        
        JComponent c = (JComponent) super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);

        
        FaultInjectionResult r = engine.getResults().get(arg4);
        
        if (arg5 == 1) {
            switch (r.getResult().getRetVal()) {
                case 0:
                    ((JLabel) c).setText("Startup error");
                    break;
                case 1:
                    ((JLabel) c).setText("Shutdown error");
                    break;
                case 2:
                    ((JLabel) c).setText("Test error");
                    break;
                case 3:
                    ((JLabel) c).setText("OK");
                    break;
                case 4:
                    ((JLabel) c).setText("Internal error");
                    break;
                case 5:
                    ((JLabel) c).setText("Impossible configuration");
            }
        }
        
        try {

            String tooltip = "<html>";
            
            for (Map.Entry<ConfigurationFile, Document> entry : r.getConfigDocuments().entrySet()) {
                   tooltip += "<b>" + entry.getKey().getName() + "</b>";
                   String val = ConfigurationDiff.getNativeDiff(entry.getKey().getDocument(), entry.getValue(), entry.getKey());
                   val = val.replace("<", "&lt;").replace(">", "&gt;");
                   tooltip += "<pre>" + val + "</pre>";
            }
            
            tooltip += "</html>";
            
            c.setToolTipText(tooltip);
     
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResultTableRenderer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ResultTableRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        return c;
    }

    
    
}
