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

import java.io.IOException;
import java.lang.reflect.Modifier;
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

    private FaultInjectionPlan plan;

    /**
     * Creates a class finder for the specified plan
     * 
     * @param plan a fault injection plan
     */
    public ClassFinderBean(FaultInjectionPlan plan) {
        this.plan = plan;
    }

    /**
     * Return all the classes in the jars specified in the fault plan that
     * implement the specified interface
     *
     * @param interfaceName the full name of the interface that must be implemented by the returned classes
     *
     * @return a vector containing full names of the classes in the jars that implement the interface
     */
    public Vector<String> getClassesNames(String interfaceName) {
        Vector<String> rets = new Vector<String>();
        for (String cls : getClassesNamesIncludingAbstract(interfaceName)) {
            try {
                if (!Modifier.isAbstract(Class.forName(cls).getModifiers())) {
                    rets.add(cls);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClassFinderBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return rets;
    }

    public Vector<String> getClassesNamesIncludingAbstract(String interfaceName) {
        Vector<String> rets = new Vector<String>();
        for (String jar : plan.getJars()) {
            rets.addAll(getClassesNames(jar, interfaceName));
        }
        return rets;
    }

    /**
     *
     * Load the class from the JARs of the fault plan
     *
     * @param name the full name of the class to be loaded
     * @return a Class object from the JAR
     * @throws ClassNotFoundException
     */
    public Class loadClass(String name) throws ClassNotFoundException {

        for (String jar : plan.getJars()) {
            try {

                UnsecureURLClassLoader loader = new UnsecureURLClassLoader(new URL[]{new URL("file://" + plan.getAbsolutePath(jar))}, this.getClass().getClassLoader());

                return loader.loadClass(name);

            } catch (MalformedURLException ex) {
                Logger.getLogger(ClassFinderBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
            }

        }

        throw new ClassNotFoundException(name);
    }

    private Vector<String> getClassesNames(String fileName, String interfaceName) {

        Vector<String> rets = new Vector<String>();

        try {

            UnsecureURLClassLoader loader = new UnsecureURLClassLoader(new URL[]{new URL("file://" + fileName)}, this.getClass().getClassLoader());

            System.err.println("------------"+fileName+"------------");
            java.util.jar.JarFile file = new JarFile(fileName);

            Enumeration<JarEntry> e = file.entries();

            while (e.hasMoreElements()) {

                JarEntry entry = e.nextElement();

                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    Class clazz = loader.loadClass(entry.getName().replace('/', '.').replaceAll("\\.class$", ""));

                    /* check that the class is not abstract */
                    /* if ((clazz.getModifiers() & 0x0400) > 0) {
                    continue;
                    }*/

                    boolean ok = false;

                    Class clazzS = clazz;
                    if (clazzS.getName().equals(interfaceName)) {
                        ok = true;
                    }

                    while (clazzS.getSuperclass() != null && !ok) {
                        for (Class i : clazzS.getInterfaces()) {
                            if (i.getName().equals(interfaceName)) {
                                ok = true;
                                break;
                            }
                        }
                        clazzS = clazzS.getSuperclass();
                        if (clazzS.getName().equals(interfaceName)) {
                            ok = true;
                        }
                    }

                    if (ok) {
                        rets.add(clazz.getName());
                    }
                }


            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassFinderBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClassFinderBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rets;

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
