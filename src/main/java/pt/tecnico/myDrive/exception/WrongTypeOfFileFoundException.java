package pt.tecnico.myDrive.exception;

public class WrongTypeOfFileFoundException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public WrongTypeOfFileFoundException(){
		super();
	}
	
	public WrongTypeOfFileFoundException(String fileName, String classToCast){
		super("The file with name <" + fileName +"> cannot be cast to the expected type <" + classToCast + ">");
	}
}
