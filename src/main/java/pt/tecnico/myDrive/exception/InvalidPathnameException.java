package pt.tecnico.myDrive.exception;

public class InvalidPathnameException extends MyDriveException {

	private static final long serialVersionUID = 1L;


	public InvalidPathnameException(String pathname){
		super("Invalid pathname <" + pathname +">");
	}
   
}
