package pt.tecnico.myDrive.exception;

public class InvalidUsernameException extends MyDriveException {

	private static final long serialVersionUID = 7454896768786230954L;
	
	public InvalidUsernameException(String message){
		super("Username is invalid - " + message);
	}
}
