package pt.tecnico.myDrive.exception;

public class InvalidPermissionsException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public InvalidPermissionsException(String permissions){
		super("The permissions provided are invalid, either in length or acceptable characters <" + permissions +">.");
	}
}
