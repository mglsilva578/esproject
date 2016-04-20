package pt.tecnico.myDrive.exception;

public class CannotReadPasswordException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public CannotReadPasswordException (){
		super("Permission denied - cannot read password");
	}
}



