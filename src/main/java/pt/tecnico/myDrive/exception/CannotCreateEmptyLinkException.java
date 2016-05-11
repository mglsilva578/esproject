package pt.tecnico.myDrive.exception;

public class CannotCreateEmptyLinkException extends MyDriveException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CannotCreateEmptyLinkException(){
		super("Links must point to a File!");
	}
}
