package ch.epfl.dslab.conferrng.arugula;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

public class SelectedNode {

    private final String XPathString;
    public static final String id = "CONFERR_ID";
    private final Integer value;
    private final Document doc;
    private final ch.epfl.dslab.conferrng.arugula.Configuration config;

    public static boolean areTheseDocumentsTheSame(Document d1, Document d2) {
        try {
            return d1.getProperty(id).equals(d2.getProperty(id));
        } catch (Throwable t) {
            return false;
        }
    }

    public SelectedNode(Object o, SelectedNode node) {
        this(XPathUtils.getXPathUnknown(o), node.getDocument(), node.config);
    }

    public SelectedNode(String XPath, Document doc, Configuration configuration) {
        this.XPathString = XPath;
        if (doc.getProperty(id) == null) {
            doc.setProperty(id, doc.hashCode());
        }
        this.value = (Integer) doc.getProperty(id);
        this.doc = doc;
        config = configuration;
    }

    public void addDescription(String description) {
        config.addErrorDescription(description);
    }

    private Document getDocument() {
        return doc;
    }

    /**
     * Returns an XML element
     * @param config the configuration in which it looks
     * @return the XML element that has the same XPath expression
     */
    public Object getNode(Configuration config) {
        if (XPathString == null || XPathString.equals("")) {
            return null;
        }

        XPath p;
        try {
            p = XPath.newInstance(XPathString);
        } catch (JDOMException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        for (Document newDoc : config.getDocumentIterator()) {
            if (newDoc.getProperty(id).equals(value)) {
                try {
                    for (Object o : p.selectNodes(newDoc)) {
                        if (o instanceof Document) {
                            return ((Document) o).getRootElement();
                        }
                        return o;
                    }
                } catch (JDOMException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    /**
     * Returns an XML element
     * @return the XML element that has the same XPath expression
     */
    public Object getNode() {
        return getNode(config);
    }

    public SelectedNode findYourself(Configuration config) {
        for (Document newDoc : config.getDocumentIterator()) {
            Object prop = newDoc.getProperty(id);
            if ((Integer) prop == value) {
                return new SelectedNode(XPathString, newDoc, config);
            }
        }
        return null;
    }

    public void addYourConfiguration(Collection<Configuration> listOfConfigurations) {
        listOfConfigurations.add(config);
    }

    private Configuration copyYourConfiguration() {
        return config.copy();
    }

    public SelectedNode splitYourself() {
        return findYourself(copyYourConfiguration());
    }

    @Override
    public String toString() {
        return getNode() + " [" + XPathString + "]";
    }

    public void setExpectedBehavior(boolean shouldItSucceed) {
        config.setExpectedBehavior(shouldItSucceed);
    }

    @Override
    public int hashCode() {
        return XPathString.hashCode() * config.hashCode() * doc.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SelectedNode)) {
            return false;
        }
        SelectedNode other = (SelectedNode) o;
        if (config.equals(other.config)) {
            return getNode().equals(other.getNode());
        }
        return false;
    }
}
