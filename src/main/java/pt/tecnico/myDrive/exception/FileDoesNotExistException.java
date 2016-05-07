package pt.tecnico.myDrive.exception;

public class FileDoesNotExistException extends MyDriveException {
	private static final long serialVersionUID = 1L;
	
	public FileDoesNotExistException(String FileIsNotPlainFile){
		super("File <" + FileIsNotPlainFile + "> doesn't exist.");
	} 
}
