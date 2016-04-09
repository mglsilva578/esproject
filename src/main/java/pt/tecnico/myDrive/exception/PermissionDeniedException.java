package pt.tecnico.myDrive.exception;

import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.User;

public class PermissionDeniedException extends MyDriveException {
	public static final String DELETE = "delete";
	public static final String READ = "read";
	public static final String WRITE = "write";
	private static final long serialVersionUID = 1L;
	
	public PermissionDeniedException(User user, String permission, File file){
		super("DENIED -  User <" + user +"> does not have permission to <" + permission + "> the file <"+ file +">");
	}
}
