package pt.tecnico.myDrive.exception;

public class ImportDocumentException extends MyDriveException {

	private static final long serialVersionUID = 7454896768786230954L;
	
	public ImportDocumentException(){
		super("ImportXML is invalid - missing non optional attribute");
	}
	
	public ImportDocumentException(String message){
		super("ImportXml is invalid - " + message);
	}
}
