package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.CannotFindFileException;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class ExecuteFileServiceTest extends AbstractServiceTest {
//Execute a text file. 
//This service receives a token, the file path and a list of arguments (strings).
	private static final String USERNAME1 = "username1";
	private static final String PASS1 = "password1";
	private static final String USERNAME2 = "username2";
	private static final String PASS2 = "password2";
	private User user1;
	private Dir homeDir1;

	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		User user1 = new User(myDrive, USERNAME1, PASS1, "name1", "rwxd----", null);
		new User(myDrive, USERNAME2, PASS2, "name2", "rwxd----", null);
		Dir homeDir1 = (Dir)myDrive.getFileByPathname("/home/username1", false, user1);
		new PlainFile(myDrive, user1, "plain1", user1.getMask(), "", homeDir1);
		new Link(myDrive, user1, "link1", user1.getMask(), "/home/username1/plain1", homeDir1);
		new App(myDrive, user1, "app1", user1.getMask(), "package.class.method", homeDir1);
		new Dir(myDrive, user1, "dir1", user1.getMask(), homeDir1);
	}
	
	@Test
	public void sucessInExecute(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		ExecuteFileService service = new ExecuteFileService(token,"/home/username1/plain1","args");
		service.execute();
		PlainFile plain1 = (PlainFile) myDrive.getFileByPathname("/home/username1/plain1", false,user1);
		String content = plain1.getContent();
		assertEquals("args",content);
	}

	@Test(expected= CannotFindFileException.class)
	public void unexistingPlainFile(){
		MyDrive myDrive=MyDrive.getInstance();
		LoginManager loginmanager = myDrive.getLoginManager();
		Long token = loginmanager.createSession(USERNAME1, PASS1);
		ExecuteFileService service = new ExecuteFileService(token,"/local/invalid","args");
		service.execute();
	}
	
	@Test(expected=WrongTypeOfFileFoundException.class)
	public void wrongTypeOfFile(){
		MyDrive myDrive=MyDrive.getInstance();
		LoginManager loginmanager = myDrive.getLoginManager();
		Long token = loginmanager.createSession(USERNAME1, PASS1);
		ExecuteFileService service = new ExecuteFileService(token,"/home/username1","args");
		service.execute();
	}

}
