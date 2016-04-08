package pt.tecnico.myDrive.exception;

public class CannotDeleteRootException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public CannotDeleteRootException (){
		super("Permission denied - cannot delete root");
	}
}

