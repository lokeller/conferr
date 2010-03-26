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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This bean loads classes from the jars specified in the associated fault 
 * injection plan instance.
 * 
 * KNOWN BUGS: This class under some circumstances load too many times the 
 * classes (because it does no caching) and therefore it can make the VM
 * run out of Perm space.
 * 
 */

public class ClassFinderBean {

    private String interfaceName;
    private FaultInjectionPlan plan;

    public ClassFinderBean(FaultInjectionPlan plan) {
        this.plan = plan;
    }

    public Vector<String> getClassesNames() {

        Vector<String> rets = new Vector<String>();
        for (String jar : plan.getJars()) {
            rets.addAll(getClassesNames(plan.getAbsolutePath(jar)));
        }
        return rets;
    }

    
    public Class loadClass(String name) throws ClassNotFoundException {
        
        for (String jar : plan.getJars()) {
            try {                
                
                UnsecureURLClassLoader loader = new UnsecureURLClassLoader(new URL[] { new URL("file://" + plan.getAbsolutePath(jar))}, this.getClass().getClassLoader());
                
                return loader.loadClass( name );
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(ClassFinderBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                
            }
            
        }
            
        throw new ClassNotFoundException(name);
    }
    
    private Vector<String> getClassesNames(String fileName) {
        
        Vector<String> rets = new Vector<String>();
        
        try {
            
            UnsecureURLClassLoader loader = new UnsecureURLClassLoader(new URL[] {new URL("file://" + fileName)}, this.getClass().getClassLoader());
            
            java.util.jar.JarFile file = new JarFile(fileName);
            
            Enumeration<JarEntry> e = file.entries();
            
            while ( e.hasMoreElements()) {
                
                JarEntry entry = e.nextElement();
                
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    Class clazz = loader.loadClass(entry.getName().replace('/', '.').replaceAll("\\.class$", ""));
                                       
                    /* check that the class is not abstract */
                    if ((clazz.getModifiers() & 0x0400) > 0) continue;                                    
                    
                    boolean ok = false;
                    
                    Class clazzS = clazz;
                    
                    while ( clazzS.getSuperclass() != null) {
                        for (Class i : clazzS.getInterfaces() ) {
                            if (i.getName().equals(getInterfaceName())) {
                                ok = true;
                                break;
                            }
                        }                                             
                        clazzS = clazzS.getSuperclass();
                    }
                    
                    if (ok) rets.add(clazz.getName());
                }
                
                
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassFinderBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClassFinderBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return rets;
        
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    
    class UnsecureURLClassLoader extends URLClassLoader {

        public UnsecureURLClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        protected PermissionCollection getPermissions(CodeSource codesource) {
            PermissionCollection coll = super.getPermissions(codesource);
            coll.add(new AllPermission());
            return coll;
        }
        
        
        
    }
    
}
