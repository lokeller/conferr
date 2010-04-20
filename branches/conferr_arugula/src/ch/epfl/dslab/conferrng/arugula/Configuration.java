/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jdom.Document;

/**
 *
 * @author slv
 */
public class Configuration {

    private final Map<String, String> mapFromFileNameToContents;
    private final Map<String, Document> mapFromStringToDocument;
    private final Map<Document, String> mapFromOriginalToModified;
    private final Map<Document, Document> mapFromModifiedDocumentToOriginalDocument;

    public Configuration(Map<String, String> initialConfiguration) {
        mapFromModifiedDocumentToOriginalDocument = new HashMap<Document, Document>();
        mapFromFileNameToContents = initialConfiguration;
        mapFromStringToDocument = new HashMap<String, Document>();
        mapFromOriginalToModified = new HashMap<Document, String>();
        for (String s : initialConfiguration.values()) {
            mapFromStringToDocument.put(s, null);
        }

    }

    public void addDocumentForFile(String config, Document doc) {
        mapFromStringToDocument.put(config, doc);
        mapFromModifiedDocumentToOriginalDocument.put(doc, (Document) doc.clone());
    }

    public Iterable<String> getInputFileNameIterator() {
        return new Iterable<String>() {

            @Override
            public Iterator<String> iterator() {
                return mapFromFileNameToContents.keySet().iterator();
            }
        };
    }

    public Iterable<String> getFileIterator() {
        return new Iterable<String>() {

            @Override
            public Iterator<String> iterator() {
                return mapFromStringToDocument.keySet().iterator();
            }
        };
    }

    public Iterable<Document> getDocumentIterator() {
        return new Iterable<Document>() {

            @Override
            public Iterator<Document> iterator() {
                return mapFromStringToDocument.values().iterator();
            }
        };
    }

    public void addModifiedVersion(Document original, String modified) {
        mapFromOriginalToModified.put(original, modified);
    }

    public Configuration copy() {
        Configuration newConfig = new Configuration(this.mapFromFileNameToContents);
        for (Map.Entry<String, Document> pair : mapFromStringToDocument.entrySet()) {
            newConfig.addDocumentForFile(pair.getKey(), (Document) pair.getValue().clone());
            newConfig.addModifiedVersion(pair.getValue(), mapFromOriginalToModified.get(pair.getValue()));
        }
        newConfig.shouldItSucceed = this.shouldItSucceed;
        return newConfig;
    }

    public Map<String, String> getMapFromFileNameToModified() {
        Map<String, String> result = new HashMap<String, String>();
        for (String fileName : mapFromFileNameToContents.keySet()) {
            String original = mapFromFileNameToContents.get(fileName);
            String newVersion = mapFromOriginalToModified.get(mapFromStringToDocument.get(original));
            result.put(fileName, newVersion);
        }
        return result;
    }

    public String getContentsForInputFileName(String name) {
        return mapFromFileNameToContents.get(name);
    }

    public Document getModifiedDocumentForInputFile(String inputFile) {
        return mapFromStringToDocument.get(mapFromFileNameToContents.get(inputFile));
    }

    public String getModifiedConfigurationFile(String inputFile) {
        String configContents = mapFromFileNameToContents.get(inputFile);
        Document modDoc = mapFromStringToDocument.get(configContents);
        String result = mapFromOriginalToModified.get(modDoc);
        return result != null ? result : configContents;
    }
    private String description = "";

    public void addErrorDescription(String description) {
        if (description != null) {
            this.description += description + " ";
        }
    }

    public String getDescription() {
        return description;
    }
    private boolean shouldItSucceed = true;

    public void setExpectedBehavior(boolean shouldItSucceed) {
        this.shouldItSucceed &= shouldItSucceed;
    }

    public boolean shouldItSucceed() {
        return shouldItSucceed;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Configuration)) {
            return false;
        }
        if (obj == null) {
            return false;
        }
        final Configuration other = (Configuration) obj;
        if (this.mapFromFileNameToContents.equals(other.mapFromFileNameToContents) && (this.mapFromFileNameToContents == null || !this.mapFromFileNameToContents.equals(other.mapFromFileNameToContents))) {
            return false;
        }
        if (this.mapFromStringToDocument.equals(other.mapFromStringToDocument) && (this.mapFromStringToDocument == null || !this.mapFromStringToDocument.equals(other.mapFromStringToDocument))) {
            return false;
        }
        if (this.mapFromOriginalToModified.equals(other.mapFromOriginalToModified) && (this.mapFromOriginalToModified == null || !this.mapFromOriginalToModified.equals(other.mapFromOriginalToModified))) {
            return false;
        }
        if (this.mapFromModifiedDocumentToOriginalDocument.equals(other.mapFromModifiedDocumentToOriginalDocument) && (this.mapFromModifiedDocumentToOriginalDocument == null || !this.mapFromModifiedDocumentToOriginalDocument.equals(other.mapFromModifiedDocumentToOriginalDocument))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return mapFromFileNameToContents.hashCode()
                * mapFromModifiedDocumentToOriginalDocument.hashCode()
                * mapFromOriginalToModified.hashCode()
                * mapFromStringToDocument.hashCode();
    }
}
