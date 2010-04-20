/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * @author slv
 */
public class ConfigurationWithError {

    private Configuration configuration;


    public ConfigurationWithError(Configuration c) {
        this.configuration = c;
    }

    private void write(String where, String what) {
        System.err.println("@@@Writing to "+where+"\n@@@Content\n"+what);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(where));
            pw.print(what);
            pw.flush();
            pw.close();
        } catch (Exception e) {
            if (pw != null) {
                pw.close();
            }
        }
    }

    public void writeConfiguration() {
        for (Map.Entry<String, String> entry : configuration.getMapFromFileNameToModified().entrySet()) {
            write(entry.getKey(), entry.getValue());
        }
    }
    public String getDescription(){
        String description = configuration.getDescription();
        return  description ==null || description.equals("") ? "no error injected" : description;
    }

    /**
     * the configuration after has been mutated
     * @return the new configuration
     */
    public Configuration getConfiguration(){
        return configuration;
    }

    public boolean getShouldSucceed(){
        return configuration.shouldItSucceed();
    }
}
