package de.maxflo.it.infrastruktur.archimate.vergleich.logic;

import com.thoughtworks.xstream.XStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fn/mh
 */
public class XMLFileReader {

    public static void main(String[] args) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("config.xml"); // load our xml file
            XStream xstream = new XStream();     // init XStream
           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileReader.close();
            } catch (IOException ex) {
                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
