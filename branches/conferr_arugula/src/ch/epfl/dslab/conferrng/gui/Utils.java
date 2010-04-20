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
package ch.epfl.dslab.conferrng.gui;

import ch.epfl.dslab.conferrng.engine.*;
import java.awt.Component;
import java.io.File;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * Utility class that provides methods that display frequently used input dialogs.
 */
public class Utils {

    public static String loadClass(Component root, FaultInjectionPlan plan, String text, String title, String interfaceName) {
        ClassFinderBean cfbean = new ClassFinderBean(plan);

        Vector<String> classes = cfbean.getClassesNamesIncludingAbstract(interfaceName);

        if (classes.size() == 0) {
            JOptionPane.showMessageDialog(root, "Unable to find suitable plugins, please make sure that the plugin JARs have been added to the fault injection plan.");
            return null;
        }

        String[] classesA = classes.toArray(new String[classes.size()]);

        return (String) JOptionPane.showInputDialog(root, text, title,
                JOptionPane.INFORMATION_MESSAGE, null,
                classesA, classesA[0]);

    }

    public static String loadDirectory(Component root, FaultInjectionPlan plan, String title) {
        JFileChooser chooser = new JFileChooser();

        chooser.setCurrentDirectory(new File(plan.getAbsolutePath(plan.getResolver().getBaseDirectory())));
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
            return plan.getAbsolutePath(chooser.getSelectedFile().getPath());
        } else {
            return null;
        }

    }

    public static String loadFile(Component root, FaultInjectionPlan plan, String title) {
        return loadFile(root, plan.getResolver(), title);
    }

    public static String loadFile(Component root, FileResolver resolver, String title) {
        return loadFile(root, resolver, title, null, null);
    }

    public static String loadFile(Component root, FaultInjectionPlan plan, String title, final String type, final String description) {
        return loadFile(root, plan.getResolver(), title, type, description, true);
    }

    public static String loadFile(Component root, FileResolver resolver, String title, final String type, final String description) {
        return loadFile(root, resolver, title, type, description, true);
    }

    public static String loadFile(Component root, FaultInjectionPlan plan, String title, final String type, final String description, boolean relative) {
        return loadFile(root, plan.getResolver(), title, type, description, relative);
    }

    public static String loadFile(Component root, FileResolver resolver, String title, final String type, final String description, boolean relative) {
        JFileChooser chooser = new JFileChooser();

        if (type != null) {
            chooser.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File arg0) {

                    return arg0.isDirectory() || arg0.getPath().toLowerCase().endsWith(type);
                }

                @Override
                public String getDescription() {
                    return description;
                }
            });
        }

        chooser.setDialogTitle(title);

        chooser.setCurrentDirectory(new File(resolver.getAbsolutePath(resolver.getBaseDirectory())));

        if (chooser.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
            String chosenFile = chooser.getSelectedFile().getAbsolutePath();
            if (relative) {

                resolver.setBaseDirectory(new File(chosenFile).getParent());
            }

            return chosenFile;

        } else {
            return null;
        }
    }

    public static String saveFile(Component root, FaultInjectionPlan plan, String title) {
        return saveFile(root, plan, title, null, null);
    }

    public static String saveFile(Component root, FaultInjectionPlan plan, String title, final String type, final String description) {
        return saveFile(root, plan, title, type, description, true);
    }

    public static String saveFile(Component root, FaultInjectionPlan plan, String title, final String type, final String description, boolean relative) {
        return saveFile(root, plan.getResolver(), title, type, description, relative);
    }

    public static String saveFile(Component root, FileResolver resolver, String title, final String type, final String description, boolean relative) {
        JFileChooser chooser = new JFileChooser();

        if (type != null) {
            chooser.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File arg0) {

                    return arg0.isDirectory() || arg0.getPath().toLowerCase().endsWith(type);
                }

                @Override
                public String getDescription() {
                    return description;
                }
            });
        }

        chooser.setDialogTitle(title);
        chooser.setCurrentDirectory(new File(resolver.getAbsolutePath(resolver.getBaseDirectory())));

        if (chooser.showSaveDialog(root) == JFileChooser.APPROVE_OPTION) {

            File f = chooser.getSelectedFile();

            if (!f.exists() || JOptionPane.showConfirmDialog(root, "File already exists, do you want to overwrite it?") == JOptionPane.YES_OPTION) {
                if (relative) {
                    return resolver.getRelativePath(f.getPath());
                } else {
                    return f.getPath();
                }
            } else {
                return null;
            }

        } else {

            return null;

        }
    }

    public static String getXPath(FaultInjectionPlan plan) {
        if (plan.iterator().hasNext()) {
            XPathSelectDialog xpathDialog = new XPathSelectDialog(plan.iterator().next().getConfiguration());
            xpathDialog.setVisible(true);
            return xpathDialog.getTheXPathExpression();
        }
        return "Nothing can be selected";
    }
}
