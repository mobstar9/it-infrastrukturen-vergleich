package de.maxflo.it.infrastruktur.archimate.vergleich.logic;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
       
    static Document doc;
    static int count = 0;
    
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
         System.out.println(root.getNodeName());


        
        //Ebenen innerhalb 
        //archimate:model
        NodeList allChilds = doc.getElementsByTagName("*");
        
        
        
       treeIterRek(allChilds);
    }


    
    //Rekurisiver XML Baumdurchlauf^^
    private static void treeIterRek(NodeList allChilds) {
        
        System.out.println("-------TiefenEbene: "+count+++"--------");
        
        for (int temp = 0; temp < allChilds.getLength(); temp++) {
            
            Node node = allChilds.item(temp);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.print("NodeName= " + node.getNodeName() + " ");
                System.out.print("NodeCont= " + node.getTextContent() +" ");
                System.out.print("NodeVal= " + node.getNodeValue());   
                System.out.print("\n"); 

                //Alle Attribute
                if (node.hasAttributes()) {

                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        
                        System.out.print("NodeAtt= " + tempNode.getNodeName()+ " ");
                        System.out.print("NodeVal= " + tempNode.getNodeValue());   
                        System.out.print("\n"); 
                    }
                    //Rek...
                    if (node.hasChildNodes()) {
                        treeIterRek(node.getChildNodes());
                    }
                }
            }
        }
    }
}
