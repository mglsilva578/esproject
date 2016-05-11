package pt.tecnico.myDrive.exception;

import pt.tecnico.myDrive.domain.File;

public class CannotGetFileContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotGetFileContentException(File file){
		super("This File" + file.getName() + "don't has content because isn't a PlainFile");
	}
}
