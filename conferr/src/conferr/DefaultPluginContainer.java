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

package conferr;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;


/**
 * This class is an abstract implementation of a plugin container, it caches a
 * plugin instance to avoid reloading classes each time.
 * 
 */

public abstract class DefaultPluginContainer extends ObservableBean implements PluginContainer{

    private Vector<Parameter> parameters = new Vector<Parameter>();
    private String pluginClass;
    private Plugin pluginInstance;
    private boolean doNotLoad = false;
    
    public Vector<Parameter> getParams() {
        return this.parameters;
    }

    public void setParams(Vector<Parameter> params) {
        
        Vector<Parameter> old = this.parameters;
        
        this.parameters = params;
        
        pcs.firePropertyChange("params", old, params);
        
    }

    public String getParameterValue(String name) {

        for (Parameter p : parameters) {
            if (p.getName().equals(name)) return p.getValue();
        }
           
        return "";
        
    }
    
    public void setParameterValue(String name, String value) {

        Plugin plugin = getPluginInstance();

        if ( plugin != null) {
        
            Vector<Parameter> newParams = new Vector<Parameter>();

            for (Parameter p : parameters) {
                if (!p.getName().equals(name)) newParams.add(p);
            }
        
            String type = "unknown";

            for (Parameter pp : plugin.getDefaultParameters()) {
                if (pp.getName().equals(name)) {
                    type = pp.getType();
                    break;
                }
            }                    

            newParams.add(new Parameter(name, value, type));

            
            Vector<Parameter> old = parameters;
            
            this.parameters = newParams;
            
            pcs.firePropertyChange("params", old, newParams);
        }
        
    }

    public String getPluginClass() {
        return pluginClass;
    }

    public synchronized void setPluginClass(String clazz) {
        String old = this.pluginClass;
        this.pluginClass = clazz;
        doNotLoad = false;
        pluginInstance = null;
        
        pcs.firePropertyChange("pluginClass", old, clazz);
        
        Plugin p = getPluginInstance();
        
        if (p == null) return;
        
            
        Vector<Parameter> pluginParams = p.getDefaultParameters();

        Vector<Parameter> newParams = new Vector<Parameter>();

        for (Parameter param : pluginParams) {
                String value = param.getValue();                
                for (Parameter p1 : this.parameters) {
                    if (p1.getName().equals(param.getName()) && p1.getType().equals(param.getType())) {
                        value = p1.getValue();
                    }
                }

            newParams.add(new Parameter(param.getName(), value, param.getType()));
        }
            
        Vector<Parameter> oldParams = this.parameters;
        this.setParams(newParams);
        
        pcs.firePropertyChange("params", oldParams, this.parameters);
                
    }

    public synchronized Plugin getPluginInstance() {
        
        if (doNotLoad) return null;
        
        try {
            
            ClassFinderBean cfb = getClassFinderBean();
            
            Class plugin = cfb.loadClass(pluginClass);

            pluginInstance = (Plugin) plugin.getConstructor().newInstance();

            return pluginInstance;
            
        } catch (NullPointerException ex) {
            
        } catch (InstantiationException ex) {
            
        } catch (IllegalAccessException ex) {
            
        } catch (IllegalArgumentException ex) {
            
        } catch (InvocationTargetException ex) {
            
        } catch (NoSuchMethodException ex) {
            
        } catch (SecurityException ex) {
            
        } catch (ClassNotFoundException ex) {
            
        }
        
        doNotLoad = true;
        return null;
        
    }

    public abstract ClassFinderBean getClassFinderBean();
    public abstract String getPluginInterface();

}
