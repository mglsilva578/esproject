package pt.tecnico.myDrive.exception;

public class MyDriveException extends RuntimeException {

	private static final long serialVersionUID = -5495644594099515732L;
	
	public MyDriveException(){
		super();
	}
	
	public MyDriveException(String message){
		super(message);
	}
}
