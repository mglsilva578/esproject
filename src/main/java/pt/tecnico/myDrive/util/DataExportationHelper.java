package pt.tecnico.myDrive.util;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.tecnico.myDrive.exception.CannotWriteExportedDataException;

public abstract class DataExportationHelper {
	private static String exportedDataFileName = "exported_data.xml";
	private static String locationOfExportedData = System.getProperty("user.dir") + "/info/" + exportedDataFileName;
	
	public static void writeDocumentToLocalStorage(Document dataToWrite){
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			xmlOutputter.output(dataToWrite, new FileOutputStream(locationOfExportedData));
		} catch (IOException ioe) {
			final Logger log = LogManager.getRootLogger();
			log.error(new CannotWriteExportedDataException(locationOfExportedData).getMessage());
			log.error(ioe.getMessage());
		}
	}
}
