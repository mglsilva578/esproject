package pt.tecnico.myDrive.exception;

public class NoDirException extends MyDriveException {
	private static final long serialVersionUID = 1L;
	
	public NoDirException(String dirNotFound){
		super("Unable to find the dir with name <" + dirNotFound + ">");
	} 
	
	public NoDirException(String dirNotFound, String whereItWasSupposedToBe){
		super("Unable to find the dir with name <" + dirNotFound + "> in <" + whereItWasSupposedToBe + ">");
	}
}
