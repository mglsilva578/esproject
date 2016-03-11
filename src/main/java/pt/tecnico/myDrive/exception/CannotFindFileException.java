package pt.tecnico.myDrive.exception;

public class CannotFindFileException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotFindFileException(){
		super();
	}
	
	public CannotFindFileException(String name){
		super("Cannot find file with name <" + name +"> in directory");
	}
}
