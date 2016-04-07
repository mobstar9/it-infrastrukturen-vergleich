package de.maxflo.it.infrastruktur.archimate.vergleich.logic;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
    07.04.2016
    
    Mal so weit, dass man das File einparsen kann.
*/
    
    private static boolean logging = true;

    
    private static Document doc;
    private static int count = 0;
    
    
    
    public static void main(String[] args) {
        readFile();
        iterateFileTree();
    }
    
    
    private static void readFile() {

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
    }

/*
   Angenommen archimate:model ist Ebene 0
   Statische Ebenen, immer gleich viele oder dynamisch?
   Habs jetzt mal als dynamischen XML Baum gemacht...
*/
    private static void iterateFileTree() {
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
