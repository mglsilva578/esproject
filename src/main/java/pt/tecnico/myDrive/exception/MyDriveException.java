package pt.tecnico.myDrive.exception;

public class MyDriveException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public MyDriveException(){
		super();
	}
	
	public MyDriveException(String message){
		super("!!!\tMyDriveException\n!!!\t" + message + "\n!!!");
	}
}
