package pt.tecnico.myDrive.exception;

public class CannotEraseFileException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotEraseFileException(){
		super();
	}
	
	public CannotEraseFileException(String fileName){
		super("Cannot erase the file with name <" + fileName +">");
	}
}
