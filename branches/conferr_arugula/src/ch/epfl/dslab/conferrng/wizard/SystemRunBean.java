/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.epfl.dslab.conferrng.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author slv
 */
public class SystemRunBean {

    private String modifiedConfigFile;
    public static final String PROP_MODIFIEDCONFIGFILE = "modifiedConfigFile";

    /**
     * Get the value of modifiedConfigFile
     *
     * @return the value of modifiedConfigFile
     */
    public String getModifiedConfigFile() {
        return modifiedConfigFile;
    }

    /**
     * Set the value of modifiedConfigFile
     *
     * @param modifiedConfigFile new value of modifiedConfigFile
     */
    public void setModifiedConfigFile(String modifiedConfigFile) {
        String oldModifiedConfigFile = this.modifiedConfigFile;
        this.modifiedConfigFile = modifiedConfigFile;
        propertyChangeSupport.firePropertyChange(PROP_MODIFIEDCONFIGFILE, oldModifiedConfigFile, modifiedConfigFile);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    private String startupScript;
    public static final String PROP_STARTUPSCRIPT = "startupScript";

    /**
     * Get the value of startupScript
     *
     * @return the value of startupScript
     */
    public String getStartupScript() {
        return startupScript;
    }

    /**
     * Set the value of startupScript
     *
     * @param startupScript new value of startupScript
     */
    public void setStartupScript(String startupScript) {
        String oldStartupScript = this.startupScript;
        this.startupScript = startupScript;
        propertyChangeSupport.firePropertyChange(PROP_STARTUPSCRIPT, oldStartupScript, startupScript);
    }
    protected String shutdownScript;

    /**
     * Get the value of shutdownScript
     *
     * @return the value of shutdownScript
     */
    public String getShutdownScript() {
        return shutdownScript;
    }

    /**
     * Set the value of shutdownScript
     *
     * @param shutdownScript new value of shutdownScript
     */
    public void setShutdownScript(String shutdownScript) {
        this.shutdownScript = shutdownScript;
    }
    private String benchmarkFolder;
    public static final String PROP_BENCHMARKFOLDER = "benchmarkFolder";

    /**
     * Get the value of benchmarkFolder
     *
     * @return the value of benchmarkFolder
     */
    public String getBenchmarkFolder() {
        return benchmarkFolder;
    }

    /**
     * Set the value of benchmarkFolder
     *
     * @param benchmarkFolder new value of benchmarkFolder
     */
    public void setBenchmarkFolder(String benchmarkFolder) {
        String oldBenchmarkFolder = this.benchmarkFolder;
        this.benchmarkFolder = benchmarkFolder;
        propertyChangeSupport.firePropertyChange(PROP_BENCHMARKFOLDER, oldBenchmarkFolder, benchmarkFolder);
    }

}
