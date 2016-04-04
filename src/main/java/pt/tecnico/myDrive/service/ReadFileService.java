package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;

public class ReadFileService extends MyDriveService{
	private Long token;
	private String fileName;
	private String content;
	
	public ReadFileService(Long token, String fileName){
		this.token = token;
		this.fileName = fileName;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		MyDrive myDrive = getMyDrive();
		Session session = myDrive.getLoginManager().getSessionByToken(this.token);
		User whoWantsToRead = session.getOwner();
		String content = myDrive.readPlainFileContent(whoWantsToRead, fileName);
		this.content = content;
	}

	public String getResult() {
		return this.content;
	}
}
