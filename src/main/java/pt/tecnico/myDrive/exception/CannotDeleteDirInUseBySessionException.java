package pt.tecnico.myDrive.exception;

import pt.tecnico.myDrive.domain.Dir;

public class CannotDeleteDirInUseBySessionException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	public CannotDeleteDirInUseBySessionException(Dir dirToDelete){
		super("Permission denied - cannot delete <" + dirToDelete.getName() + "> because it is in use by a different user.");
	}
}
