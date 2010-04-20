/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.engine;

import java.io.File;
import java.io.IOException;

/**
 *
 * Resolve relative names to absolute names
 *
 */
public class FileResolver extends ObservableBean {

    private String baseDirectory = ".";


    protected String currentDirectory = System.getProperty("user.dir");

    public static final String PROP_CURRENTDIRECTORY = "currentDirectory";

    public FileResolver() {
    }

    public FileResolver(String baseDirectory, String currentDirectory) {
        this.baseDirectory = baseDirectory;
        this.currentDirectory = currentDirectory;
    }

    /**
     * Get the value of current directory, that will be used to subsitute . in base directory
     *
     * @return the value of currentDirectory
     */
    public String getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Set the value of current directory, that will be used to subsitute . in base directory
     *
     * @param currentDirectory new value of currentDirectory
     */
    public void setCurrentDirectory(String currentDirectory) {
        String oldCurrentDirectory = this.currentDirectory;
        this.currentDirectory = currentDirectory;
        pcs.firePropertyChange(PROP_CURRENTDIRECTORY, oldCurrentDirectory, currentDirectory);
    }



    /**
     * Return the directory used as root for all relative paths in the plan
     *
     * @return a string
     */
    public String getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Sets the directory used as root for all relative paths in the plan
     *
     * @param baseDirectory a string
     */
    public void setBaseDirectory(String baseDirectory) {
        String old = this.baseDirectory;
        this.baseDirectory = baseDirectory;
        pcs.firePropertyChange("baseDirectory", old, this.baseDirectory);
    }

        /**
     *
     * Gets the absolute version of a path using the baseDir of this plan
     *
     * @param path a path relative to the baseDir of this plan
     * @return the absolute path
     */
    public String getAbsolutePath(String path) {

        if (baseDirectory == null) {
            return path;
        }
        if (new File(path).isAbsolute()) {
            return path;
        } else if (getBaseDirectory().equals(".") && getCurrentDirectory() != null) {
            return new File(getCurrentDirectory()) + File.separator + path;
        } else {
            return getBaseDirectory() + File.separator + path;
        }
    }

    /**
     * Transforms an path to a path relative to the baseDir of this plan
     *
     * @param path a path (relative to the current directory or absolute)
     * @return a path relative to the basedir of the plan
     */
    public String getRelativePath(String path) {
        try {

            File baseFile = new File(getAbsolutePath(getBaseDirectory()));

            if (path.startsWith(baseFile.getCanonicalPath())) {
                return path.substring(baseFile.getCanonicalPath().length() + 1);
            } else {
                return path;
            }
        } catch (IOException ex) {
            return path;
        }
    }

}
