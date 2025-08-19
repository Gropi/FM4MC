package IO.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;

public class EdgeHardwareInformationParser {

    public static EdgeNodeHardwareInformation parseHardwareFromXML(String message) throws ParserConfigurationException, SAXException {

        if (!message.startsWith("<")) {
            var firstIndex = message.indexOf("<");
            message = message.substring(firstIndex);
        }
        var doc = convertStringToXMLDocument(message);
        doc.getDocumentElement().normalize();
        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        return createEdgeNodeHardwareInformationElement(doc.getDocumentElement());
    }

    private static Document convertStringToXMLDocument(String xmlString) {
        //Parser that produces DOM object trees from XML content
        var factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            var doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void doSomething(Node node, EdgeNodeHardwareInformation edgeInfo) {
        if (!node.getNodeName().equals("list")) {
            if (node.hasAttributes()) {
                var attributes = node.getAttributes();
                if (attributes.getNamedItem("class") != null) {
                    var lshwClass = LshwClass.valueOf(attributes.getNamedItem("class").getNodeValue().toUpperCase());
                    var hardwareElements = edgeInfo.hardwareInformationMap.get(lshwClass);
                    //hardwareElements.add(node);
                }
            }
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeName().equals("node")) {
                //calls this method for all the children which is Element
                doSomething(currentNode, edgeInfo);
            }
        }
    }

    private static EdgeNodeHardwareInformation createEdgeNodeHardwareInformationElement(Node node) {
        var edgeNodeHardwareInformation = new EdgeNodeHardwareInformation();
        doSomething(node, edgeNodeHardwareInformation);
        return edgeNodeHardwareInformation;
    }
}
