package pt.tecnico.myDrive.exception;

import pt.tecnico.myDrive.domain.Dir;

public class FileAlreadyExistsException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public FileAlreadyExistsException(){
		super();
	}
	
	public FileAlreadyExistsException(String name, Dir dir){
		super("Exists file with same name <" + name +"> in dir <" + dir.getName() + ">");
	}
}
