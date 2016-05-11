package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidPathnameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;

public class ChangeDirectoryTest extends AbstractServiceTest{
	private String username = "mglsilva578";
	private String password = "password";
	private User userToAdd;
	private String newPath = "/home/mglsilva578/added";
	private String failPath = "NaoExiste";
	private Dir whereToAdd;
	private Dir newDir;
	private App app;
	private PlainFile plain;



	@Override
	protected void populate(){
		MyDrive myDrive = MyDrive.getInstance();
		userToAdd = new User(myDrive, username, password, "MiguelSilva", "rwxd----", null);
		whereToAdd = (Dir)myDrive.getFileByPathname("/home/mglsilva578", false, userToAdd);
		newDir = new Dir(myDrive, "added", userToAdd.getMask(), whereToAdd);
		app = new App(myDrive, userToAdd, "app", userToAdd.getMask(), "package.class.method", whereToAdd);
		plain =new PlainFile(myDrive, userToAdd, "plain", userToAdd.getMask(), "content1 (PlainFile)",whereToAdd);
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
		assertEquals(newPath, resultService);
		assertEquals("added", currentDir.getName());
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

	@Test(expected= WrongTypeOfFileFoundException.class)
	public void changesToApp(){
		MyDrive mydrive = MyDrive.getInstance();
		LoginManager loginmanager = mydrive.getLoginManager();
		Long token = loginmanager.createSession(username, password);
		log.trace(app.getPath());
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(token, app.getPath() + "/app");
		changeDirectory.execute();
	}

	@Test(expected= WrongTypeOfFileFoundException.class)
	public void changesToPlainFile(){
		MyDrive mydrive = MyDrive.getInstance();
		LoginManager loginmanager = mydrive.getLoginManager();
		Long token = loginmanager.createSession(username, password);
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(token, plain.getPath() + "/plain");
		changeDirectory.execute();
	}
}
