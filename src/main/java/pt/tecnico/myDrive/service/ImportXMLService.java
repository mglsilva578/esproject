package pt.tecnico.myDrive.service;

import org.jdom2.Document;

import pt.tecnico.myDrive.exception.MyDriveException;

public class ImportXMLService extends MyDriveService{
	private final Document doc;
	
	public ImportXMLService(Document doc){
		this.doc = doc;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		getMyDrive().importXML(doc.getRootElement());
	}

}
