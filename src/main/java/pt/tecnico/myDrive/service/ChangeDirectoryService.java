package pt.tecnico.myDrive.service;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.MyDriveException;


public class ChangeDirectoryService extends MyDriveService {
	private Long token;
	private String path;
	private Dir currentDir;
	private String result;

	public ChangeDirectoryService(Long token,String path){
		this.token=token;
		this.path=path;
	}
	
	public ChangeDirectoryService(Long token) {
		this.token=token;
		this.path=Dir.DOT_NAME;
	}
	@Override
	protected void dispatch() throws MyDriveException {
		Session session = getMyDrive().getLoginManager().getSessionByToken(this.token);
		File f;
		if(path==Dir.DOT_NAME){
			currentDir= session.getCurrentDir();
			result = currentDir.getPath();
		}
		else{
			f= getMyDrive().getFileByPathname(path, false, getMyDrive().getLoginManager().getSessionByToken(token).getOwner());
			f.confirmFileIsDir();
			currentDir = (Dir) f;
			session.setCurrentDir(currentDir);
			result = currentDir.getPath() + "/" + currentDir.getName();
		}
	}

	public String Result(){
		return result;
	}
}
