package de.maxflo.it.infrastruktur.vergleich.archimate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.Transform;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author fn/mh
 */
public class XMLFileReader {
    /*
        11.04.2016
        So hab das "alte" Zeug jetzt mal in ne bak ausgelagert der Übersicht halber.
        Habs mal so weit, dass ich die in der 2ten Datei fehlenden Zeilen bzw Nodes extrahiert hab.
        (In dem Fall wären das die ROT einzufärbenden)
        Die könnten wir jetzt händisch suchen und ersetzen oder mit nem DOM Library.
        Mit DOMSource hab ichs nich so hingekriegt der zerwürfelt alles...glaub händisch is am besten auch zum farblichen taggen.
        

     */
    
    private static boolean logging = true;
    private static int count = 0;

    public static void main(String[] args) throws SAXException, IOException, TransformerException {
        readAndCompFilesLibrary();
    }
    
private static void readAndCompFilesLibrary() throws SAXException, IOException  {
//String fileOrg = "Archisurance_kurz.archimate";
        //String fileMod = "Archisurance_kurz_changed.archimate";
        String fileOrg = "Archisurance_BusinessCorpV_Mod-CustInfoServ.archimate";
        String fileMod = "Archisurance_BusinessCorpV_Mod-ClaimRegServ.archimate";

        //Einzel Strings...nicht so optimal
        String line1 = "";
        String line2 = "";
        try (
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileOrg));
                BufferedReader bufferedReader2 = new BufferedReader(new FileReader(fileMod));) {

            String oneL = "";
            while ((oneL = bufferedReader.readLine()) != null) {
                line1 += (oneL + "\n");
            }
            while ((oneL = bufferedReader2.readLine()) != null) {
                line2 += (oneL + "\n");
            }

        } catch (IOException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);


        
        DifferenceProcessor proc = new DifferenceProcessor();
        Diff aDiff = new Diff(line1, line2);
        aDiff.overrideDifferenceListener(proc);
        

        //Idee: Immer nur die erste und die dann "berichtigen"
        
        boolean differences = aDiff.similar();


        DetailedDiff toCheck = new DetailedDiff(aDiff);
        toCheck.haltComparison(DifferenceConstants.CHILD_NODELIST_LENGTH);
        toCheck.getAllDifferences();

    }

    private static void print(String toPrint) {
        if (logging) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.INFO, toPrint);
        } else {
            System.out.println(toPrint);
        }
    }
    
    private static void printFileLine(String lineToPrint){
        
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("test.archimate",true));
            PrintWriter pw = new PrintWriter(bw);
            pw.println(lineToPrint);
            pw.flush();
            pw.close();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public static class DifferenceProcessor implements DifferenceListener {

        @Override
        public int differenceFound(Difference dfrnc) {
            //Mod CustInf bzw. Org
            Node n1 = dfrnc.getControlNodeDetail().getNode();

            //Mod ClaimRegServ bzw. Mod
            Node n2 = dfrnc.getTestNodeDetail().getNode();


            print(dfrnc.getDescription()+"------------------------------------------------");
            print("Erkannte Änderung: " + dfrnc.toString());
            printFileLine(dfrnc.toString());
            print(dfrnc.getId()+"");
            print(dfrnc.getControlNodeDetail().getValue());
            print(dfrnc.toString().length()+"");
           
/*
            
            print("Erkannte Änderung: " + dfrnc.getId());
            print("Erkannte Änderung: " + DifferenceConstants.CHILD_NODELIST_LENGTH_ID);
            print("Erkannte Änderung: " + dfrnc.getControlNodeDetail().getXpathLocation().toString());

            print("Erkannte Änderung: " + dfrnc.toString());

            print("Erkannte Änderung: " + dfrnc.getControlNodeDetail().getValue());
            print("Erkannte Änderung: " + dfrnc.getControlNodeDetail().getNode());

            print("Erkannte Änderung: " + dfrnc.getTestNodeDetail().getValue());
            print("Erkannte Änderung: " + dfrnc.getTestNodeDetail().getNode());

            print("------------------------------------------------");
            print("------------------------------------------------");
*/         


/*
ZB Hat File:
    CustInfoSrv Zeile 1225 3 Childs
    ClaimRegSrv Zeile 1225 2 Childs

    zB vorher 2, jetzt 3 also grün weil neu
    zB vorher 3, jetzt 2 also rot weil alt

*/
            switch (dfrnc.getId()) {
                case DifferenceConstants.CHILD_NODELIST_LENGTH_ID :

                    int childsC = Integer.parseInt(dfrnc.getControlNodeDetail().getValue());
                    int childsT = Integer.parseInt(dfrnc.getTestNodeDetail().getValue());

                    //Mehr C Knoten als T Konten, also fehlende finden...
                    if (childsC > childsT) {

                        List<Node> found = new ArrayList<>();

                        NodeList cConv = n1.getChildNodes();
                        NodeList tConv = n2.getChildNodes();

                        for (int i = 0; i < cConv.getLength(); i++) {
                            boolean sameFound = false;
                            Transform t1 = new Transform(cConv.item(i));
                            String oneCc = null;
                            try {
                                oneCc = t1.getResultString();
                            } catch (TransformerException ex) {
                                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            for (int j = 0; j < tConv.getLength(); j++) {
                                Transform t2 = new Transform(tConv.item(j));
                                String oneTc = null;
                                try {
                                    oneTc = t2.getResultString();
                                } catch (TransformerException ex) {
                                    Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if (oneTc.equals(oneCc)) {
                                    sameFound = true;
                                }
                            }
                            if (!sameFound) {

                                //Fehlende Knoten, einfügen in Original Dokument und evtl. rot taggen.
                                found.add(cConv.item(i));
                                print("Child fehlt in ModFile :\n" + oneCc);  
                            }
                        }
                    }
                    break;
                case DifferenceConstants.CHILD_NODELIST_SEQUENCE_ID:
                    //Platzhalter
                    break;
            }

            return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
        }

        @Override
        public void skippedComparison(Node node, Node node1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
