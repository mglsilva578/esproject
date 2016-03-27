package pt.tecnico.myDrive.exception;

public class InvalidPasswordException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public InvalidPasswordException(String username, String password){
		super("Invalid password for user with username<" + username +"> : <" + password + ">");
	}
}
