package pt.tecnico.myDrive.exception;

public class UsernameDoesNotExistException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public UsernameDoesNotExistException(){
		super();
	}
	
	public UsernameDoesNotExistException(String username){
		super("Could not find a user with username <" + username +"> in MyDrive");
	}
}
