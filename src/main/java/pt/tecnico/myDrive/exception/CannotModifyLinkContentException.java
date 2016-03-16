package pt.tecnico.myDrive.exception;

public class CannotModifyLinkContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotModifyLinkContentException(){
		super();
	}
	
	public CannotModifyLinkContentException(String linkName){
		super("Permission denied - cannot alter the contents of link with name <"+ linkName +">");
	}
}
