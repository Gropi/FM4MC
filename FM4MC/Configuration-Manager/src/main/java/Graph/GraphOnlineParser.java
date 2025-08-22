package Graph;

import Monitoring.Enums.MeasurableValues;
import Network.DataModel.CommunicationMessages;
import Structures.Graph.Edge;
import Structures.Graph.Graph;
import Structures.Graph.Vertex;
import Structures.Graph.interfaces.IVertex;
import Structures.IGraph;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Parser for graphonline.ru
 */
public class GraphOnlineParser {
    private final Logger _Logger;

    public GraphOnlineParser(Logger logger) {
        _Logger = logger;
    }

    public Graph loadBaseGraph(String fileName, int graphID) {
        // Instantiate the Factory
        Graph graph = null;
        var graphIDToUse = graphID;
        var edgeID = 0;

        try {
            var doc = getDocument(fileName);
            var elements = doc.getDocumentElement().getFirstChild().getChildNodes();
            for (int i = 0; i < elements.getLength(); i++) {
                var node = elements.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    var element = (Element) node;

                    if (element.getNodeName().equals("graph") && graphIDToUse < 0) {
                        graphIDToUse = Integer.parseInt(element.getAttribute("uidGraph"));
                    }
                    if (element.getNodeName().equals("node")) {
                        var vertex = handleVertex(element);
                        if (graph == null) {
                            graph = new Graph(graphIDToUse, vertex, true, fileName);
                        } else {
                            graph.addVertex(vertex);
                        }
                    } else if (element.getNodeName().equals("edge")) {
                        var source = graph.getVertexById(Integer.parseInt(element.getAttribute("source")));
                        var target = graph.getVertexById(Integer.parseInt(element.getAttribute("target")));
                        var edge = new Edge(source, target, edgeID);
                        edgeID++;

                        var latency = element.getAttribute("transmissionTime");
                        if (latency.isEmpty())
                            latency = "0";
                        edge.updateWeight(CommunicationMessages.Types.LATENCY.toString(), Integer.parseInt(latency));

                        graph.addEdge(edge);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IllegalArgumentException | IOException e) {
            _Logger.fatal("Failed to load file: " + fileName + "; Exception: " + e);
        }
        return graph;
    }

    public void saveGraphToXML(IGraph graph, String fileName) {
        try {
            createFolderIfNeeded(fileName);
            var doc = createDocument();
            var rootElement = doc.createElement("graphml");
            doc.appendChild(rootElement);
            var graphElement = doc.createElement("graph");
            graphElement.setAttribute("id", "Graph");
            graphElement.setAttribute("uidGraph", String.valueOf(graph.getGraphID()));
            // INFO: Because we do not use the UIDEdge, but it is present in the graphml files by default, we enter an arbitrary value.
            graphElement.setAttribute("uidEdge", UUID.randomUUID().toString());
            rootElement.appendChild(graphElement);

            addVertexNodes(doc, graphElement, graph.getAllVertices());
            addVertexEdges(doc, graphElement, graph.getAllEdges());

            var outputFile = new File(fileName);

            var in = new DOMSource(doc);
            var out = new StreamResult(outputFile);
            var transformer = getTransformer();

            transformer.transform(in, out);
        } catch (ParserConfigurationException | SAXException | TransformerException | IOException e) {
            _Logger.fatal("Exception: ", e);
        }
    }

    private void createFolderIfNeeded(String fileName) throws IOException {
        File targetFile = new File(fileName);
        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
    }

    private void addVertexNodes(Document doc, Element graph, List<IVertex> vertices) {
        for(var vertex : vertices) {
            var vertexElement = doc.createElement("node");
            //Graphml attributes
            vertexElement.setAttribute("id", String.valueOf(vertex.getId()));
            vertexElement.setAttribute("mainText", vertex.getLabel());
            vertexElement.setAttribute("upText", "");
            vertexElement.setAttribute("size", "30");
            vertexElement.setAttribute("positionX", 10 + vertex.getApplicationIndex()*50 + vertex.getApproximationIndex()*5 + "");
            vertexElement.setAttribute("positionY", 50 + vertex.getStage() * 50 + "");

            //Own attributes
            vertexElement.setAttribute("application", vertex.getApplicationIndex()+"");
            vertexElement.setAttribute("approximation", vertex.getApproximationIndex()+"");
            vertexElement.setAttribute("stage", vertex.getStage()+"");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.TIME, "executionTime");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.QoR, "QoR");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.CPU, "CPU");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.RAM, "RAM");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.ENERGY, "energy");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.PARAMETER_1, "parameter1");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.PARAMETER_2, "parameter2");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.PARAMETER_3, "parameter3");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.PARAMETER_4, "parameter4");
            setAttributeIfPresent(vertexElement, vertex, MeasurableValues.PARAMETER_5, "parameter5");

            //Add element to graph
            graph.appendChild(vertexElement);
        }
    }

    private void setAttributeIfPresent(Element vertexElement, IVertex vertex, MeasurableValues key, String attributeName) {
        var weight = vertex.getWeight(key.name());
        if (weight != null && weight.getValue() != null) {
            vertexElement.setAttribute(attributeName, weight.getValue().toString());
        }
    }

    private void setAttributeIfPresent(Element edgeElement, Edge edge, MeasurableValues key, String attributeName) {
        var weight = edge.getWeight(key.name());
        if (weight != null && weight.getValue() != null) {
            edgeElement.setAttribute(attributeName, weight.getValue().toString());
        }
    }

    private void addVertexEdges(Document doc, Element graph, List<Edge> edges) {
        for(var edge : edges) {
            var edgeElement = doc.createElement("edge");
            //Graphml attributes
            edgeElement.setAttribute("source", String.valueOf(edge.getSource().getId()));
            edgeElement.setAttribute("target", String.valueOf(edge.getDestination().getId()));
            edgeElement.setAttribute("isDirect", "true");
            edgeElement.setAttribute("weight", "1");
            edgeElement.setAttribute("useWeight", "false");
            edgeElement.setAttribute("id", String.valueOf(edge.id()));
            edgeElement.setAttribute("text", "");
            edgeElement.setAttribute("upText", "");
            edgeElement.setAttribute("arrayStyleStart", "");
            edgeElement.setAttribute("arrayStyleFinish", "");
            edgeElement.setAttribute("model_width", "4");
            edgeElement.setAttribute("model_type", "0");
            edgeElement.setAttribute("model_curveValue", "0.1");

            //Own attributes
            setAttributeIfPresent(edgeElement, edge, MeasurableValues.LATENCY, "transmissionTime");

            //Add element to graph
            graph.appendChild(edgeElement);
        }
    }

    private DocumentBuilderFactory getDBFactory() throws ParserConfigurationException {
        var dbf = DocumentBuilderFactory.newInstance();

        // optional, but recommended
        // process XML securely, avoid attacks like XML External Entities (XXE)
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        return dbf;
    }

    private Transformer getTransformer() throws TransformerConfigurationException {
        var transformerFactory = TransformerFactory.newInstance();
        var transformer = transformerFactory.newTransformer();

        return transformer;
    }

    private Document getDocument(String fileName) throws ParserConfigurationException, IOException, SAXException {
        var dbf = getDBFactory();

        // parse XML file
        var db = dbf.newDocumentBuilder();
        var doc = db.parse(new File(fileName));

        // optional, but recommended
        // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        return doc;
    }

    private Document createDocument() throws ParserConfigurationException, IOException, SAXException {
        var dbf = getDBFactory();

        //create Document
        var db = dbf.newDocumentBuilder();
        return db.newDocument();
    }

    private IVertex handleVertex(Element element) {
        var id = Integer.parseInt(element.getAttribute("id"));
        var label = element.getAttribute("mainText");
        var application = element.getAttribute("application");
        var approximation = element.getAttribute("approximation");
        var stage = element.getAttribute("stage");
        var qor = element.getAttribute(MeasurableValues.QoR.name());
        var executionTime = element.getAttribute("executionTime");
        var vertex = new Vertex(label, id, "");
        vertex.updateServiceName(label);
        vertex.updateWeight(CommunicationMessages.Types.CPU.toString(), 30);
        vertex.updateWeight(CommunicationMessages.Types.RAM.toString(), 120);

        if (!application.isEmpty())
            vertex.setApplicationIndex(Integer.parseInt(application));
        if (!approximation.isEmpty())
            vertex.setApproximationIndex(Integer.parseInt(approximation));
        if (!stage.isEmpty())
            vertex.setStage(Integer.parseInt(stage));
        if (!qor.isEmpty())
            vertex.setQoR(Integer.parseInt(qor));
        if (!executionTime.isEmpty())
            vertex.updateWeight(CommunicationMessages.Types.TIME.toString(), Integer.parseInt(executionTime));

        return vertex;
    }
}
