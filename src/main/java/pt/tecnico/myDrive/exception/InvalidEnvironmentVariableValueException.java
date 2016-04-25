package pt.tecnico.myDrive.exception;

public class InvalidEnvironmentVariableValueException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public InvalidEnvironmentVariableValueException(String invalidValue){
		super("The value provided for an environment variable is invalid <" + invalidValue + ">");
	}
}
