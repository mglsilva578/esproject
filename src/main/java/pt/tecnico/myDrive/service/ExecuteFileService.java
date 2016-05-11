package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.domain.Session;


public class ExecuteFileService extends MyDriveService{
	private Long _token;
	private String _path;
	private String[] _args;
	public ExecuteFileService(Long token, String path, String... args){
		_token = token;
		_path = path;
		_args = args;
	}

	public final void dispatch() throws MyDriveException {
		MyDrive myDrive = getMyDrive();
		Session session = myDrive.getLoginManager().getSessionByToken(_token);
		User user = session.getOwner();
		File file = myDrive.getFileByPathname(_path, false, user);
		file.execute(_args);
	}

	public final String Result(){
		return null;
	}
}
