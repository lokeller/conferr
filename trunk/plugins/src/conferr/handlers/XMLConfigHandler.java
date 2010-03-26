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
package conferr.handlers;

import conferr.ConfigurationFile;
import conferr.Handler;
import conferr.Parameter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


public class XMLConfigHandler implements Handler {
    
    @Override
    public Document parseConfiguration(Reader input, ConfigurationFile file) {
        try {

            SAXBuilder builder = new SAXBuilder();

            return builder.build(input);
            
        } catch (JDOMException ex) {
            throw new RuntimeException("Unable to parse file", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to parse file", ex);
        }        
        
    }

    @Override
    public void serializeConfiguration(Document config, Writer out, ConfigurationFile file) {

        try {
            XMLOutputter outputter = new XMLOutputter();
            outputter.output(config, out);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLConfigHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public Vector<Parameter> getDefaultParameters() {
        return new Vector<Parameter>();
    }

    

    
    
}
