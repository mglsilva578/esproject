package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
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
		MyDrive drive = getMyDrive();
		LoginManager loginManager = drive.getLoginManager();
		Session session = loginManager.getSessionByToken(token);
		Dir currentDir = session.getCurrentDir();
		User user = session.getOwner();
		
		String content = currentDir.readPlainFileContent(token, fileName, user);
		this.content = content;
	}

	public String getResult() {
		return this.content;
	}
}
