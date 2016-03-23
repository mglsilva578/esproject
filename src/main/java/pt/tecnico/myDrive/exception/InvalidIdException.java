package pt.tecnico.myDrive.exception;

public class InvalidIdException extends MyDriveException {

	private static final long serialVersionUID = 1L;

    public InvalidIdException(String id){
		super("Unable to parse the id <" + id +"> as an Integer.");
	}
}
