package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.MyDriveException;

public class LoginUserService extends MyDriveService{
	private String username;
	private String password;
	private Long token;
	
	public LoginUserService(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		MyDrive myDrive =getMyDrive();
		LoginManager loginManager = myDrive.getLoginManager();
		token = loginManager.createSession(username, password);
		return;
	}
	
	public Long getResult(){
		return this.token;
	}
}
