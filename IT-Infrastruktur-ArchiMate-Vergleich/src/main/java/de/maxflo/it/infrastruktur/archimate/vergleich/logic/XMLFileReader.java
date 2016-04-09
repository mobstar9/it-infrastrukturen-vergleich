package de.maxflo.it.infrastruktur.archimate.vergleich.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
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
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
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
    
    09.04.2016
    Eigener Vergleicher
    ------------------
    Versuch, mal testweise beide "nebeneinander iterieren" und Änderungen rausschreiben
    Zeilen mail und phone vertauscht^^Noch nicht alle möglichen Fälle abgedeckt!!
    
    XMLUnit Vergleicher
    ------------------
    Joa...scheint seine Arbeit gut zu machen kannst dir ja mal anschaun

    
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
        //readFile_StAX();
        //readAndCompFilesSelfmade();
        readAndCompFilesLibrary();
        
    }
    
    private static void readAndCompFilesLibrary() {

        String fileOrg = "Archisurance_kurz.archimate";
        String fileMod = "Archisurance_kurz_changed.archimate";

        //Einzel Strings...nicht so optimal
        String line1 = "";
        String line2 = "";
        try (
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileOrg));
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(fileMod));) {

            String oneL = "";
            while ((oneL = bufferedReader.readLine()) != null) {
                line1+=oneL;
            }
            while ((oneL = bufferedReader2.readLine()) != null) {
                line2+=oneL;
            }

        } catch (IOException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        DetailedDiff diff = null;
        try {
            diff = new DetailedDiff(XMLUnit.compareXML(line1, line2));
        } catch (SAXException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<?> allDifferences = diff.getAllDifferences();
        
        print(diff.toString());
    }
    
    
    
  private static void readAndCompFilesSelfmade() {

        try {
            String fileOrg = "Archisurance_kurz.archimate";
            String fileMod = "Archisurance_kurz_changed.archimate";
                
            Reader readerO = null;
            Reader readerM = null;
            try {
                readerO = new FileReader(fileOrg);
                readerM = new FileReader(fileMod);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }

            XMLInputFactory xmlInputFactoryO = XMLInputFactory.newInstance();
            XMLInputFactory xmlInputFactoryM = XMLInputFactory.newInstance();
            
            XMLEventReader xmlEventReaderO = null;
            XMLEventReader xmlEventReaderM = null;
            try {
                xmlEventReaderO = xmlInputFactoryO.createXMLEventReader(readerO);
                xmlEventReaderM = xmlInputFactoryM.createXMLEventReader(readerM);
            } catch (XMLStreamException ex) {
                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }


            //Orginal File
            while (xmlEventReaderO.hasNext()) {

                XMLEvent xmlEventO = null;
                try {
                    xmlEventO = xmlEventReaderO.nextEvent();
                } catch (XMLStreamException ex) {
                    Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //Andres auch noch weitere Elemente?
                XMLEvent xmlEventM = null;
                if(xmlEventReaderM.hasNext()) {
                    try {
                        xmlEventM = xmlEventReaderM.nextEvent();
                    } catch (XMLStreamException ex) {
                        Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
      
                
                //Erstes
                if (xmlEventO.isStartElement()) {
                    
                    //Org Startelement
                    StartElement startElO = xmlEventO.asStartElement();
                    
                    //Modifiziertes File auch StartElement, prüfen
                    StartElement startElM = null;
                    if(xmlEventM.isStartElement()) {
                        startElM = xmlEventM.asStartElement();

                        QName nameO = startElO.getName();
                        QName nameM = startElM.getName();
                        
                        //TODO: Alles prüfen...
                       if(!nameO.equals(nameM)) {
                           print("Änderung erkannt, orginal "+ nameO.toString());
                           print("Änderung erkannt, mod "+ nameM.toString()) ;
                        }
  
                    } else {
                        //Sonst ModElement weiter, restliche Fälle noch abdecken
                        if(xmlEventReaderM.hasNext()) {
                            try {
                                xmlEventM = xmlEventReaderM.nextEvent();
                            } catch (XMLStreamException ex) {
                                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } 
                    }

                    //Iteration Attribute (innerhalb Startelemente)
                    Iterator iterO = startElO.getAttributes();
                    Iterator iterM = startElM.getAttributes();
                    while (iterO.hasNext()) {
                        Attribute attributO = (Attribute) iterO.next();
                        QName nameO = attributO.getName();
                        String valueO = attributO.getValue();

                        if(iterM.hasNext()) {
                            Attribute attributM = (Attribute) iterM.next();
                            QName nameM = attributM.getName();
                            String valueM = attributM.getValue();
                            
                            //TODO: Alles prüfen...
                            if(!valueM.equals(valueO)) {
                                print("Änderung erkannt, orginal "+ valueO.toString());
                                print("Änderung erkannt, mod "+ valueM.toString()) ;
                            }
                            
                        } else {
                            //Attribute fehlen in Mod an dieser Stelle...
                        }
                    }
                }
                
                //End Elemente
                if (xmlEventO.isEndElement()) {
                    EndElement endElO = xmlEventO.asEndElement();
                    QName nameEndO = endElO.getName();
                    
                    if(xmlEventM.isEndElement()) {
                        EndElement endElM = xmlEventM.asEndElement();
                        QName nameEndM = endElM.getName();
                        
                        //TODO: Alles prüfen...
                        if(!nameEndM.equals(nameEndO)) {
                           print("Änderung erkannt, orginal "+ nameEndO.toString());
                           print("Änderung erkannt, mod "+ nameEndM.toString()) ;
                        }
                    }
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        
        
        
    private static void readFile_StAX() {
        
        
        try {
            String fileOrg = "Archisurance_kurz.archimate";
            String fileMod = "Archisurance_kurz_changed.archimate";
                
            Reader readerO = null;
            Reader readerM = null;
            try {
                readerO = new FileReader(fileOrg);
                readerM = new FileReader(fileMod);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }

            XMLInputFactory xmlInputFactoryO = XMLInputFactory.newInstance();
            XMLInputFactory xmlInputFactoryM = XMLInputFactory.newInstance();
            
            XMLEventReader xmlEventReaderO = null;
            XMLEventReader xmlEventReaderM = null;
            try {
                xmlEventReaderO = xmlInputFactoryO.createXMLEventReader(readerO);
                xmlEventReaderM = xmlInputFactoryO.createXMLEventReader(readerM);
            } catch (XMLStreamException ex) {
                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }


            //Orginal File
            while (xmlEventReaderO.hasNext()) {

                XMLEvent xmlEventO = null;
                try {
                    xmlEventO = xmlEventReaderO.nextEvent();
                } catch (XMLStreamException ex) {
                    Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
                }

                
                if (xmlEventO.isStartElement()) {
                    
                    //Get event as start element. 
                    StartElement startElement = xmlEventO.asStartElement();
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
                if (xmlEventO.isEndElement()) {
                //Get event as end element. 
                    EndElement endElement = xmlEventO.asEndElement();
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
            System.out.println(toPrint); 
        }
    }
}
