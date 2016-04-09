package pt.tecnico.myDrive.service;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;



public class WriteFileService extends MyDriveService {
	private Long token;
	private String filename;
	private String newContent;

	public WriteFileService (Long token, String filename, String newContent){
		this.token = token;
		this.filename = filename;
		this.newContent = newContent;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		Session session = getMyDrive().getLoginManager().getSessionByToken(this.token);
		Dir currentDir = session.getCurrentDir();
		User whoWantsToChangeFile = session.getOwner();
		//log.trace("SERVICE - \nuser" + whoWantsToChangeFile.toString() +"\n"+"In dir : \n" + currentDir.toString());
		currentDir.changePlainFileContent(this.filename, this.newContent, whoWantsToChangeFile);
	}
	


}
