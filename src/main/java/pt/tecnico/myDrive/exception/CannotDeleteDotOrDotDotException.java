package pt.tecnico.myDrive.exception;

import pt.tecnico.myDrive.domain.Dir;

public class CannotDeleteDotOrDotDotException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotDeleteDotOrDotDotException(){
		super("Permission denied - cannot delete " + Dir.DOT_NAME +" or " + Dir.DOTDOT_NAME);
	}
}
