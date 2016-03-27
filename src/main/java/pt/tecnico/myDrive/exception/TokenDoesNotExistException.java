package pt.tecnico.myDrive.exception;

public class TokenDoesNotExistException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public TokenDoesNotExistException(Long token){
		super("The token given does not belong to any active session <" + token +">");
	}
}
