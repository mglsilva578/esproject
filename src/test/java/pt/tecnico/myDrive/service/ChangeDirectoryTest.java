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
	private String newPath = "/home/newPath/Added";
	private String failPath = "NaoExiste";
	
	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		userToAdd = new User(myDrive, username, password, "MiguelSilva", "rwxd----", null);
		Dir whereToAdd = (Dir)myDrive.getFileByPathname("/home/mglsilva578", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		Dir newDir = new Dir(myDrive, "new_dir", userToAdd.getMask(), whereToAdd);
		new PlainFile(myDrive, userToAdd, "More Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", newDir);
		new PlainFile(myDrive, userToAdd, "A cold shower", userToAdd.getMask(), "When the heater is off, there is no salvation.", newDir);
		
		
	}
	
	@Test
	public void success(){
		MyDrive mydrive = MyDrive.getInstance();
		LoginManager loginmanager = mydrive.getLoginManager();
		Long token = loginmanager.createSession(username, password);
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(token, newPath);
		Dir currentDir = loginmanager.getSessionByToken(token).getCurrentDir();
		String oldPath = currentDir.getPath();
		changeDirectory.execute();
		String resultService = changeDirectory.Result();
		assertEquals(oldPath, resultService);
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
	}
}
