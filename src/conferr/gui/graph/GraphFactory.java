/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package conferr.gui.graph;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

/**
 *
 * @author slv
 */
public class GraphFactory {

    private static Map<Element, GraphVertex> mapFromElementToVertex;

    public static DirectedGraph<GraphVertex, DefaultEdge> createGraph(Document doc) {
        ListenableDirectedGraph<GraphVertex, DefaultEdge> graph = new ListenableDirectedWeightedGraph<GraphVertex, DefaultEdge>(DefaultEdge.class);
        mapFromElementToVertex = new HashMap<Element, GraphVertex>();
        try {
            populateTheGraph(doc, graph);
        } catch (Exception ex) {
            Logger.getLogger(GraphFactory.class.getName()).log(Level.SEVERE, null, ex);
        }  finally {
            mapFromElementToVertex.clear();
        }
        return graph;

    }

    private static boolean filterOut(Element e) {
        return (e.getName().equals("comment"));
    }

    private static void populateTheGraph(Document doc, DirectedGraph<GraphVertex, DefaultEdge> graph) {
        if (doc == null) {
            System.err.println("Document is null");
            return;
        }else{
            System.err.println("NOT null");
        }

        List<Element> elements = new Vector<Element>();
        elements.add(doc.getRootElement());
        Element current = doc.getRootElement();
        addElementToTheGraph(current, graph);
        while (!elements.isEmpty()) {
            current = elements.get(0);
            elements.remove(0);
            for (Object o : current.getChildren()) {
                Element e = (Element) o;
                if (filterOut(e)) {
                    continue;
                }
                addElementToTheGraph(e, graph);
//                String end = getStringOf(e);
//                if (!graph.containsVertex(end)) {
//                    graph.addVertex(end);
//                }
//                graph.addEdge(start, end);
                elements.add(e);
            }
        }
    }

    private static void addElementToTheGraph(Element e, DirectedGraph<GraphVertex, DefaultEdge> graph) {
        GraphVertex parent = mapFromElementToVertex.get(e.getParentElement());
        if (parent == null) {
            GraphVertex root = new GraphVertexElement(e);
            graph.addVertex(root);
            mapFromElementToVertex.put(e, root);
            return;
        }

        final GraphVertex vertex = new GraphVertexElement(e);
        graph.containsVertex(vertex);
        mapFromElementToVertex.put(e, vertex);
        graph.addVertex(vertex);
        graph.addEdge(parent, vertex);
        for (Object o : e.getAttributes()) {
            Attribute a = (Attribute) o;
            if(a.getName().startsWith("_"))
                continue;
            GraphVertex attrib = new GraphVertexAttribute(a);

            graph.addVertex(attrib);
            graph.addEdge(vertex, attrib);
        }
        for (Object o : e.getContent()) {
            Content c = (Content) o;
            if (c instanceof Text) {
                GraphVertex text = new GraphVertexText((Text) c);
                graph.addVertex(text);
                graph.addEdge(vertex, text);

            }
        }

    }
}
