package de.maxflo.it.infrastruktur.archimate.vergleich.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author fn/mh
 */
public class XMLFileReader {
           

/*
    08.04.2016
    SAX und StAX Parser, schauen welcher besser ist...
    
    07.04.2016
    Mal so weit, dass man das File einparsen kann...
*/
    
    private static boolean logging = true;
    private static Document doc;
    private static int count = 0;
    
    
    
    public static void main(String[] args) {
        //readFile_SAX();
        readFile_StAX();
        //iterateFileTree();
    }
    
    
        private static void readFile_StAX() {
        try {
            String filePath = "Archisurance.archimate_KURZ";


            Reader fileReader = null;
            try {
                fileReader = new FileReader(filePath);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }

            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = null;
            try {
                xmlEventReader = xmlInputFactory.createXMLEventReader(fileReader);
            } catch (XMLStreamException ex) {
                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }


            while (xmlEventReader.hasNext()) {
                
                XMLEvent xmlEvent = null;
                try {
                    xmlEvent = xmlEventReader.nextEvent();
                } catch (XMLStreamException ex) {
                    Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if (xmlEvent.isStartElement()) {
                    
                    //Get event as start element. 
                    StartElement startElement = xmlEvent.asStartElement();
                    print("Start Element: " + startElement.getName());

                    //Iterate and process attributes. 
                    Iterator iterator = startElement.getAttributes();
                    while (iterator.hasNext()) {
                        Attribute attribute = (Attribute) iterator.next();
                        QName name = attribute.getName();
                        String value = attribute.getValue();
                        print("Attribute name: " + name);
                        print("Attribute value: " + value);
                    }
                }
                
                //Check if event is the end element. 
                if (xmlEvent.isEndElement()) {
                //Get event as end element. 
                    EndElement endElement = xmlEvent.asEndElement();
                    print("End Element: " + endElement.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    
    private static void readFile_SAX() {
        //Original file (Muster)
        File archiFile = new File("Archisurance.archimate_KURZ");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        doc = null;    
        try {
             doc = dBuilder.parse(archiFile);
        } catch (SAXException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Normalisieren
        doc.getDocumentElement().normalize();
        
        Element root = doc.getDocumentElement();
        print(root.getNodeName());

        //Ebenen innerhalb 
        //archimate:model
        NodeList allChilds = doc.getElementsByTagName("*");
        treeIterRek(allChilds);
    }
    
    //Rekurisiver XML Baumdurchlauf^^
    private static void treeIterRek(NodeList allChilds) {
        
        print("-------TiefenEbene: "+count+++"--------");
        for (int temp = 0; temp < allChilds.getLength(); temp++) {
            
            Node node = allChilds.item(temp);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                print("NodeName= " + node.getNodeName() + " ");
                print("NodeCont= " + node.getTextContent() +" ");
                print("\n");

                //Alle Attribute
                if (node.hasAttributes()) {

                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        
                        print("NodeAtt= " + tempNode.getNodeName()+ " ");
                        print("NodeVal= " + tempNode.getNodeValue());
                        print("\n");

                    }
                    //Rek...
                    if (node.hasChildNodes()) {
                        treeIterRek(node.getChildNodes());
                    }
                }
            }
        }
    }
    
    private static void print(String toPrint) {
        if(logging) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.INFO, toPrint);
        } else {
            System.out.print(toPrint); 
        }
    }
}
