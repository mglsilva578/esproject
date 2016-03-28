package pt.tecnico.myDrive.exception;

public class InvalidTokenException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public InvalidTokenException(Long token){
		super("The token provided does not belong to any active session<" + token +">");
	}
}
