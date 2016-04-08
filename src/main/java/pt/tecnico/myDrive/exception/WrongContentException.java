package pt.tecnico.myDrive.exception;

import pt.tecnico.myDrive.domain.File;

public class WrongContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public WrongContentException(){
		super();
	}
	
	public WrongContentException(String content, File file){
		super("Wrong content <"+ content + "> to specified file <" + file.getName()+">");
	}

}
