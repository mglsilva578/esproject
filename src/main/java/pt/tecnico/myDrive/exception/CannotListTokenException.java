package pt.tecnico.myDrive.exception;

public class CannotListTokenException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotListTokenException(){
		super("Listing of tokens ( or sessions ) is not permitted");
	}
}
