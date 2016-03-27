package pt.tecnico.myDrive.exception;

public class CannotListSessionsException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotListSessionsException(){
		super("Listing of sessions is not permitted");
	}
}
