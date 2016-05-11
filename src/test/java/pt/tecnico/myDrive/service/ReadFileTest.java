package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileDoesNotExistException;
import pt.tecnico.myDrive.exception.InvalidPathnameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.NoPlainFileException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

@RunWith(JMockit.class)
public class ReadFileTest extends AbstractServiceTest{

	private static final String CONTENT1_APP = "package.class.method";
	private static final String CONTENT1_LINK = "/home/username1/plain1";
	private static final String CONTENT1_PLAIN_FILE = "content1 (PlainFile)";
	private static final String USERNAME1 = "username1";
	private static final String PASS1 = "passwoord1";
	private static final String USERNAME2 = "username2";
	private static final String PASS2 = "password2";
	private Long token;
	private Dir CurrentDir;

	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		User user1 = new User(myDrive, USERNAME1, PASS1, "name1", "rwxd----", null);
		new User(myDrive, USERNAME2, PASS2, "name2", "rwxd----", null);
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		Session session = loginManager.getSessionByToken(token);
		this.token = token;
		CurrentDir = session.getCurrentDir(); 	
		
		new PlainFile(myDrive, user1, "plain1", user1.getMask(), CONTENT1_PLAIN_FILE, CurrentDir);
		new Link(myDrive, user1, "link1", user1.getMask(), CONTENT1_LINK, CurrentDir);
		new App(myDrive, user1, "app1", user1.getMask(), CONTENT1_APP, CurrentDir);
		new Dir(myDrive, user1, "dir1", user1.getMask(), CurrentDir);
		
		new Link(myDrive, user1, "linkENV", user1.getMask(), "/home/$USER/plain1", CurrentDir);

	}

	@Test
    public void successInPlainFile() {
		ReadFileService service = new ReadFileService(token, "plain1");
        service.execute();
        String content = service.getResult();
        assertEquals(CONTENT1_PLAIN_FILE,content);
    }
	
	@Test
    public void successInLink() {
		ReadFileService service = new ReadFileService(token, "link1");
        service.execute();
        String content = service.getResult();
        assertEquals(CONTENT1_PLAIN_FILE,content);
    }
	
	@Test
    public void successInApp() {
		ReadFileService service = new ReadFileService(token, "app1");
        service.execute();
        String content = service.getResult();
        assertEquals(CONTENT1_APP, content);
    }
	
	@Test(expected = InvalidTokenException.class)
    public void invalidTokenPlainFile() {
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		ReadFileService service = new ReadFileService(wrongToken, "plain1");
        service.execute();
    }
	
	@Test(expected = InvalidTokenException.class)
    public void invalidTokenLink() {
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		ReadFileService service = new ReadFileService(wrongToken, "link1");
        service.execute();
    }
	
	@Test(expected = InvalidTokenException.class)
    public void invalidTokenApp() {
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		ReadFileService service = new ReadFileService(wrongToken, "app1");
        service.execute();
    }
	
	@Test(expected = FileDoesNotExistException.class)
    public void invalidNamePlainFile() {
		ReadFileService service = new ReadFileService(token, "plain2");
        service.execute();
    }
	
	@Test(expected = FileDoesNotExistException.class)
    public void invalidNameLink() {
		ReadFileService service = new ReadFileService(token, "link2");
        service.execute();
    }
	
	@Test(expected = FileDoesNotExistException.class)
    public void invalidNameApp() {
		ReadFileService service = new ReadFileService(token, "app2");
        service.execute();
    }
	
	@Test(expected = NoPlainFileException.class)
    public void invalidReadDir() {
		ReadFileService service = new ReadFileService(token, "dir1");
        service.execute();
    }
	
	@Test(expected = PermissionDeniedException.class)
    public void invalidPermissionPlainFile() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token1 = loginManager.createSession(USERNAME2, PASS2);
		Session session = loginManager.getSessionByToken(token1);
		session.setCurrentDir(CurrentDir);
		ReadFileService service = new ReadFileService(token1, "plain1");
        service.execute();
    }
	
	@Test(expected = PermissionDeniedException.class)
    public void invalidPermissionLink() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token1 = loginManager.createSession(USERNAME2, PASS2);
		Session session = loginManager.getSessionByToken(token1);
		session.setCurrentDir(CurrentDir);
		ReadFileService service = new ReadFileService(token1, "link1");
        service.execute();
    }
	
	@Test(expected = PermissionDeniedException.class)
    public void invalidPermissionApp() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token1 = loginManager.createSession(USERNAME2, PASS2);
		Session session = loginManager.getSessionByToken(token1);
		session.setCurrentDir(CurrentDir);
		ReadFileService service = new ReadFileService(token1, "app1");
        service.execute();
    }
	
	@Test
    public void successMock() {
        new MockUp<ReadFileService>() {
	  @Mock void dispatch() {  }
	  @Mock String result() { return CONTENT1_PLAIN_FILE; }
	};

	ReadFileService service = new ReadFileService(token, "linkENV");
        service.execute();
        log.trace(service.getResult() + " \n\n\nAAAA");
        assertEquals(service.getResult(), CONTENT1_PLAIN_FILE);
    }
	
}