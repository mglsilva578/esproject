package pt.tecnico.myDrive.exception;

public class InvalidEnvironmentVariableNameException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public InvalidEnvironmentVariableNameException(String invalidName){
		super("The name provided for an environment variable is invalid <" + invalidName + ">");
	}
}
