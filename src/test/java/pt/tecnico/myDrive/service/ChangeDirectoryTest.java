package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidPathnameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;

public class ChangeDirectoryTest extends AbstractServiceTest{
	private String username = "mglsilva578";
	private String password = "68230";
	private User userToAdd;
	private String newPath = "/home/mglsilva578/added";
	private String failPath = "NaoExiste";
	private Dir path;
	
	
	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		userToAdd = new User(myDrive, username, password, "MiguelSilva", "rwxd----", null);
		Dir whereToAdd = (Dir)myDrive.getFileByPathname("/home/mglsilva578", false, userToAdd);
		path = new Dir(myDrive, "added", userToAdd.getMask(), whereToAdd);
		
		
	}
	
	@Test
	public void success(){
		MyDrive mydrive = MyDrive.getInstance();
		LoginManager loginmanager = mydrive.getLoginManager();
		Long token = loginmanager.createSession(username, password);
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(token, newPath);
		Dir currentDir = loginmanager.getSessionByToken(token).getCurrentDir();
		assertEquals("/home", currentDir.getPath());
		changeDirectory.execute();
		currentDir = loginmanager.getSessionByToken(token).getCurrentDir();
		String resultService = changeDirectory.Result();
		assertEquals(path.getPath(), resultService);
	}
	
	@Test
	public void successCurrentDir(){
		MyDrive mydrive = MyDrive.getInstance();
		LoginManager loginmanager = mydrive.getLoginManager();
		Long token = loginmanager.createSession(username, password);
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(token, ".");
		changeDirectory.execute();
		String curentdir = loginmanager.getSessionByToken(token).getCurrentDir().getPath();
		assertEquals(curentdir, changeDirectory.Result());
	}
	
	@Test(expected = InvalidTokenException.class)
	public void tokenFail(){
		Long token = (long) 123124211;
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(token, newPath);
		changeDirectory.execute();
	}
	
	@Test(expected = InvalidPathnameException.class)
	public void pathFail(){
		MyDrive mydrive = MyDrive.getInstance();
		LoginManager loginmanager = mydrive.getLoginManager();
		Long token = loginmanager.createSession(username, password);
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(token, failPath);
		changeDirectory.execute();
	}
}
