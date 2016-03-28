package pt.tecnico.myDrive.exception;

public class CannotDeleteSlashDirException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotDeleteSlashDirException(){
		super("Permission denied - cannot delete the root directory");
	}
}
