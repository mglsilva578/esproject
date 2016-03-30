package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;

public class CreateFileService extends MyDriveService{

	private Long _token;
	private String _fileName;
	private String _docType;
	private String _content;
	public CreateFileService(Long token,String fileName, String type, String content){
		_token = token;
		_fileName = fileName;
		_docType = type;
		_content = content;
	}
	@Override
	protected void dispatch() throws MyDriveException {
		Session session =  getMyDrive().getLoginManager().getSessionByToken(_token);
		User owner = session.getOwner();
		Dir currentDir = session.getCurrentDir();
		currentDir.createFile(owner, _fileName, _docType, _content);
	}

}
