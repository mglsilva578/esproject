package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.exception.MyDriveException;

public class LoginUserService extends MyDriveService{
	private String username;
	private String password;
	private Long token;
	
	public LoginUserService(String username, String password){
		//TODO 
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		//TODO
		return;
	}
	
	public Long getResult(){
		return this.token;
	}
}
