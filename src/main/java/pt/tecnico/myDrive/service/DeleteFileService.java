package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.MyDriveException;

public class DeleteFileService extends MyDriveService{
	private Long token;
	private String fileName;
	
	public DeleteFileService(Long token, String fileName){
		this.token = token;
		this.fileName = fileName;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		Session session = getMyDrive().getLoginManager().getSessionByToken(this.token);
		Dir currentDir = session.getCurrentDir();
		currentDir.deleteFile(this.fileName);
	}

}
