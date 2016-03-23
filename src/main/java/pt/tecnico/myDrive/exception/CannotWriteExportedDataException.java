package pt.tecnico.myDrive.exception;

public class CannotWriteExportedDataException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotWriteExportedDataException(){
		super();
	}
	
	public CannotWriteExportedDataException(String path){
		super("Cannot write the exported XML data to <" + path +">");
	}
}
