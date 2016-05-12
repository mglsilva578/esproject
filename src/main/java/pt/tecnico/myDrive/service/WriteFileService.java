package pt.tecnico.myDrive.service;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;



public class WriteFileService extends MyDriveService {
	private Long token;
	private String path;
	private String newContent;

	public WriteFileService (Long token, String path, String newContent){
		this.token = token;
		this.path = path;
		this.newContent = newContent;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		MyDrive drive =getMyDrive();
		drive.changePlainFileContent(token, path, newContent);
	}
	


}
