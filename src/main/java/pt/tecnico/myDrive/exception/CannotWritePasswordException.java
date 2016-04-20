package pt.tecnico.myDrive.exception;

public class CannotWritePasswordException extends MyDriveException{

	private static final long serialVersionUID = 1L;

	public CannotWritePasswordException (){
		super("Permission denied - cannot write password");
	}
}
