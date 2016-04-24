package pt.tecnico.myDrive.exception;

public class CannotSetOwnerOfSession extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotSetOwnerOfSession(){
		super("Permission denied - cannot set the owner of a session");
	}
}
