package pt.tecnico.myDrive.exception;

public class CannotSetLastActiveDateOfSession extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotSetLastActiveDateOfSession(){
		super("Permission denied - cannot set the last active date of a session");
	}
}
