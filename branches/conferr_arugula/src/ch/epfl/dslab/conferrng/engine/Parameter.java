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

import org.jdom.Element;

/**
 * 
 * This class stores a parameter for a plugin.
 *  
 */
public class Parameter extends ObservableBean {

    private String name;
    private String value;
    private String type;
    /** the parameter is an XPATH expression */
    public static final String XPATH_EXPRESSION = "xpath";
    /** the parameter is a boolean value */
    public static final String BOOLEAN = "boolean";
    /** the parameter is an integer value */
    public static final String INTEGER = "integer";
    /** the parameter is a path to a file */
    public static final String FILE = "file";
    /** the parameter is a path to a directory */
    public static final String DIRECTORY = "directory";
    /** the parameter is a path regular expression */
    public static final String REGEX_STRING = "regex_string";
    /** the parameter is a string*/
    public static final String STRING = "string";
    /** the parameter is a configuration file identifier */
    public static final String CONFIGURATION_FILE = "configuration_file";

    public static final String CLASS_FILE="class_file";

    /**
     * Constructs a new parameter
     *
     * @param name the name of the parameter
     * @param value the initial value of the parameter
     * @param type the type of the parameter (see String constants of this class)
     */
    public Parameter(String name, String value, String type) {
        super();
        this.name = name;
        this.value = value;
        this.type = type;
        isSet=!(value==null || value.trim().equals(""));
    }

    /**
     * Returns the name of the parameter
     *
     * @return a String containing the name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the parameter
     *
     * @return a String value containing the value of the parameter
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the parameter.
     *
     * @param value the new value for the parameter.
     */
    public void setValue(String value) {
        isSet=!(value==null || value.trim().equals(""));
        String oldValue = this.value;
        this.value = value;
        pcs.firePropertyChange(value, oldValue, value);
    }

    /**
     *
     * Returns the type of the parameter
     *
     * @return a string (see type string constants in this class)
     */
    public String getType() {
        return type;
    }

    public void fromXML(Element e) {
        String value = e.getAttributeValue(getName());
        if (value == null || value.equals("")) {
            return;
        }
        isSet = true;
        this.value = value;
    }
    private boolean isSet;

    public boolean isSet(){
        return isSet;
    }
}
