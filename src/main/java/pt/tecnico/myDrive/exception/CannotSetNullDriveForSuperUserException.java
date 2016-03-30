package pt.tecnico.myDrive.exception;

public class CannotSetNullDriveForSuperUserException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotSetNullDriveForSuperUserException(){
		super("Permission denied - cannot set a null drive for the super user.");
	}
}
