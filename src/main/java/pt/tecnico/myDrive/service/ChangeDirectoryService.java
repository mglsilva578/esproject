package pt.tecnico.myDrive.service;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.MyDriveException;

/*Changes the current working directory. This service receives the token and a path, relative
or absolute. The service returns the absolute path to the new current working directory.
Changing the working directory ’.’ allows the return the current working directory.*/

public class ChangeDirectoryService extends MyDriveService {
	private Long token;
	private String path;
	private Dir currentDir;

	public ChangeDirectoryService(Long token,String path){
		this.token=token;
		this.path=path;
	}
	@Override
	protected void dispatch() throws MyDriveException {
		Session session = getMyDrive().getLoginManager().getSessionByToken(this.token);
		if(path==Dir.DOT_NAME){
			currentDir= session.getCurrentDir();
		}
		currentDir= (Dir)getMyDrive().getFileByPathname(path, false, getMyDrive().getLoginManager().getSessionByToken(token).getOwner());
		session.setCurrentDir(currentDir);
	}
	
	protected String Result(){
		return currentDir.getPath();
	}

}
