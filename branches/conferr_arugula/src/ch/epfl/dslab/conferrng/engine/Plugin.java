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
package ch.epfl.dslab.conferrng.engine;

import java.beans.PropertyChangeListener;
import java.util.Vector;
import org.jdom.Element;

/**
 * This interface represent a plugin i.e. an object that can be loaded from the
 * jars specified in the fault injection plan. Each plugin provides a set of
 * parameters that can be modified by the user.
 *
 */
public interface Plugin {

    /**
     * Return the plugin parameters list
     *
     * @return a vector of parameters
     */
    public Vector<Parameter> getParameters();

    /**
     * Returns the value of the specified parameter or null if the parameter
     * doesn't exists.
     *
     * @param name the name of the parameter
     *
     * @return the value of the parameter, or null if the parameter doesn't exits
     */
    public String getParameterValue(String name);

    /**
     * Sets the value of the specified parameter. If the parameter doesn't exits
     * the function does nothing.
     *
     * @param name the name of the parameter
     * @param value the new value for the parameter
     */
    public void setParameterValue(String name, String value);

    /**
     * Returns an XML fragment describing the plugin suitable reloading it with
     * PluginFactory
     *
     * @return an XML fragment
     */
    public Element toXML();

    /**
     * Returns (non-unique) identifier this plugin instance
     *
     * @return a non unique string identifier
     */
    public String getIdentifier();

    /**
     * Sets the (non-unique) identifier of this plugin instance
     *
     * @param identifier a String
     */
    public void setIdentifier(String identifier);

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);
}
