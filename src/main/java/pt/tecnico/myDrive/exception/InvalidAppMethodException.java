package pt.tecnico.myDrive.exception;

public class InvalidAppMethodException extends MyDriveException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidAppMethodException(String message){
		super("There's no method by the name: " + message);
	}
	

}
