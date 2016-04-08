package pt.tecnico.myDrive.exception;

public class TypeDoesNotExistException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public TypeDoesNotExistException(){
		super();
	}
	
	public TypeDoesNotExistException(String wrongType){
		super("This type doesn't exist: <" + wrongType + ">");
	}
}
