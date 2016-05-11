package pt.tecnico.myDrive.exception;

import pt.tecnico.myDrive.domain.File;

public class CannotSetFileContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotSetFileContentException(File file){
		super("This file " + file.getName() + "can't set his content because isn't a PlainFile");
	}
}
