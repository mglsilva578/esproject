package pt.tecnico.myDrive.exception;

public class NoPlainFileException extends MyDriveException {
	private static final long serialVersionUID = 1L;
	
	public NoPlainFileException(String FileIsNotPlainFile){
		super("Not a PlainFile <" + FileIsNotPlainFile + ">");
	} 
}
