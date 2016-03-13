package pt.tecnico.myDrive.exception;

public class InvalidPathameException extends MyDriveException {

	private static final long serialVersionUID = 1L;


	public InvalidPathameException(String pathname){
		super("Invalid pathname <" + pathname +">");
	}
   
}
