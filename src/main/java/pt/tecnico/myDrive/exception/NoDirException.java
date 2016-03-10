package pt.tecnico.myDrive.exception;

public class NoDirException extends MyDriveException {
	private static final long serialVersionUID = 1L;
	
	public NoDirException(String message){
		super("Invalid Path"+message);
	} 
}
