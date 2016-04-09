package pt.tecnico.myDrive.exception;

public class ExceedsLimitPathException extends MyDriveException  {
	private static final long serialVersionUID = 1L;

    public ExceedsLimitPathException() {
    	super("Path has more than 1024 characters");
    }
}
