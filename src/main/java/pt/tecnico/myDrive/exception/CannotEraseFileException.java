package pt.tecnico.myDrive.exception;

public class CannotEraseFileException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotEraseFileException(){
		super();
	}
	
	public CannotEraseFileException(String username){
		super("Exists user with same username <" + username +"> in MyDrive");
	}
}
