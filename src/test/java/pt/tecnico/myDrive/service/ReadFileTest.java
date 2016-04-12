package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidPathnameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.NoPlainFileException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class ReadFileTest extends AbstractServiceTest{

	private static final String USERNAME1 = "username1";
	private static final String PASS1 = "pass1";
	private static final String USERNAME2 = "username2";
	private static final String PASS2 = "pass2";

	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		User user1 = new User(myDrive, USERNAME1, PASS1, "name1", "rwxd----", null);
		new User(myDrive, USERNAME2, PASS2, "name2", "rwxd----", null);
		Dir homeDir1 = (Dir)myDrive.getFileByPathname("/home/username1", false, user1);
		new PlainFile(myDrive, user1, "plain1", user1.getMask(), "content1 (PlainFile)", homeDir1);
		new Link(myDrive, user1, "link1", user1.getMask(), "/home/username1/plain1", homeDir1);
		new App(myDrive, user1, "app1", user1.getMask(), "package.class.method", homeDir1);
		new Dir(myDrive, user1, "dir1", user1.getMask(), homeDir1);
	}

	@Test
    public void successInPlainFile() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		ReadFileService service = new ReadFileService(token, "/home/username1/plain1");
        service.execute();
        String content = service.getResult();
        assertEquals("content1 (PlainFile)",content);
    }
	
	@Test
    public void successInLink() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		ReadFileService service = new ReadFileService(token, "/home/username1/link1");
        service.execute();
        String content = service.getResult();
        assertEquals("content1 (PlainFile)",content);
    }
	
	@Test
    public void successInApp() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		ReadFileService service = new ReadFileService(token, "/home/username1/app1");
        service.execute();
        String content = service.getResult();
        assertEquals("package.class.method", content);
    }
	
	@Test(expected = InvalidTokenException.class)
    public void InvalidTokenPlainFile() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		ReadFileService service = new ReadFileService(wrongToken, "/home/username1/plain1");
        service.execute();
        String content = service.getResult();
        assertEquals("content1 (PlainFile)", content);
    }
	
	@Test(expected = InvalidTokenException.class)
    public void InvalidTokenLink() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		ReadFileService service = new ReadFileService(wrongToken, "/home/username1/link1");
        service.execute();
        String content = service.getResult();
        assertEquals("/home/username1/plain1", content);
    }
	
	@Test(expected = InvalidTokenException.class)
    public void InvalidTokenApp() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		ReadFileService service = new ReadFileService(wrongToken, "/home/username1/app1");
        service.execute();
        String content = service.getResult();
        assertEquals("package.class.method", content);
    }
	
	@Test(expected = InvalidPathnameException.class)
    public void InvalidPathPlainFile() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		ReadFileService service = new ReadFileService(token, "/home/username1/plain2");
        service.execute();
    }
	
	@Test(expected = InvalidPathnameException.class)
    public void InvalidPathLink() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		ReadFileService service = new ReadFileService(token, "/home/username1/link2");
        service.execute();
    }
	
	@Test(expected = InvalidPathnameException.class)
    public void InvalidPathApp() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		ReadFileService service = new ReadFileService(token, "/home/username1/app2");
        service.execute();
    }
	
	@Test(expected = NoPlainFileException.class)
    public void InvalidPathDir() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		ReadFileService service = new ReadFileService(token, "/home/username1");
        service.execute();
    }
	
	@Test(expected = PermissionDeniedException.class)
    public void InvalidPermissionPlainFile() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME2, PASS2);
		ReadFileService service = new ReadFileService(token, "/home/username1/plain1");
        service.execute();
    }
	
	@Test(expected = PermissionDeniedException.class)
    public void InvalidPermissionLink() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME2, PASS2);
		ReadFileService service = new ReadFileService(token, "/home/username1/link1");
        service.execute();
    }
	
	@Test(expected = PermissionDeniedException.class)
    public void InvalidPermissionApp() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME2, PASS2);
		ReadFileService service = new ReadFileService(token, "/home/username1/app1");
        service.execute();
    }
	
}