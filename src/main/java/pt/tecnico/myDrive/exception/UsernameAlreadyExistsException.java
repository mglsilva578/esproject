package pt.tecnico.myDrive.exception;

public class UsernameAlreadyExistsException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public UsernameAlreadyExistsException(){
		super();
	}
	
	public UsernameAlreadyExistsException(String username){
		super("Exists user with same username <" + username +"> in MyDrive");
	}
}
