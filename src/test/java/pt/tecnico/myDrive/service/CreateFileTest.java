package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.InvalidPathnameException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.NoPlainFileException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TypeDoesNotExistException;
import pt.tecnico.myDrive.exception.WrongContentException;

public class CreateFileTest extends AbstractServiceTest{

	private static final String TYPE_APP = "app";
	private static final String TYPE_LINK = "link";
	private static final String TYPE_PLAINFILE = "plainFile";
	private static final String TYPE_DIR = "dir";
	private static final String USERNAME1 = "username1";
	private static final String PASS1 = "pass1";
	private MyDrive myDrive;
	private User user1;
	private Long token1;
	private Dir currentDir1;

	@Override
	protected void populate() {
		myDrive = MyDrive.getInstance();
		user1 = new User(myDrive, USERNAME1, PASS1, "name1", "rwxd----", null);
		token1 = myDrive.getLoginManager().createSession(USERNAME1, PASS1); 
		Session sessionUser1 = myDrive.getLoginManager().getSessionByToken(token1);
		currentDir1 = sessionUser1.getCurrentDir();
		Dir dirAAA = (Dir) myDrive.getFileByPathname("/home", false, user1);
		new PlainFile(myDrive, user1, "AAA", user1.getMask(), "content1 (PlainFile)", dirAAA);

		new PlainFile(myDrive, user1, "plain", user1.getMask(), "content1 (PlainFile)", currentDir1);
		new Link(myDrive, user1, "link", user1.getMask(), "/home/username1/plain1", currentDir1);
		new App(myDrive, user1, "app", user1.getMask(), "package.class.method", currentDir1);
		new Dir(myDrive, user1, "dir", user1.getMask(), currentDir1);
	}

	@Test
	public void successDir() {
		CreateFileService service = new CreateFileService(token1, "dir1", TYPE_DIR, null);
		service.execute();

		Dir dir = (Dir) currentDir1.getFileByName("dir1");
		assertEquals("dir1", dir.getName());
		assertEquals(user1.getMask(), dir.getPermissions());
	}

	@Test
	public void successPlainFile() {
		CreateFileService service = new CreateFileService(token1, "plain1", TYPE_PLAINFILE, "Content plain");
		service.execute();

		PlainFile plainFile = (PlainFile) currentDir1.getFileByName("plain1");
		assertEquals("plain1", plainFile.getName());
		assertEquals(user1.getMask(), plainFile.getPermissions());
		assertEquals("Content plain", plainFile.getContent());
	}

	@Test
	public void successLink() {
		CreateFileService service = new CreateFileService(token1, "link1", TYPE_LINK, "/home/AAA");
		service.execute();

		PlainFile link = (PlainFile)currentDir1.getFileByName("link1");
		assertEquals("AAA", link.getName());
		assertEquals(user1.getMask(), link.getPermissions());
		assertEquals("content1 (PlainFile)", link.getContent());
	}

	@Test
	public void successApp() {
		CreateFileService service = new CreateFileService(token1, "app1", TYPE_APP, "package.class.method");
		service.execute();

		App app = (App) currentDir1.getFileByName("app1");
		assertEquals("app1", app.getName());
		assertEquals(user1.getMask(), app.getPermissions());
		assertEquals("package.class.method", app.getContent());
	}

	@Test(expected = InvalidTokenException.class)
	public void InvalidTokenDir() {
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token1 == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		CreateFileService service = new CreateFileService(wrongToken, "dir1", TYPE_DIR, null);
		service.execute();

	}

	@Test(expected = InvalidTokenException.class)
	public void InvalidTokenPlainFile() {
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token1 == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		CreateFileService service = new CreateFileService(wrongToken, "plain1", TYPE_PLAINFILE, "Content plain");
		service.execute();
	}

	@Test(expected = InvalidTokenException.class)
	public void InvalidTokenLink() {
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token1 == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		CreateFileService service = new CreateFileService(wrongToken, "link1", TYPE_LINK, "/home/AAA");
		service.execute();
	}

	@Test(expected = InvalidTokenException.class)
	public void InvalidTokenApp() {
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token1 == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		CreateFileService service = new CreateFileService(wrongToken, "app1", TYPE_APP, "package.class.method");
		service.execute();
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void InvalidExistingDir() {
		CreateFileService service = new CreateFileService(token1, "dir", TYPE_DIR, null);
		service.execute();
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void InvalidExistingPlainFile() {
		CreateFileService service = new CreateFileService(token1, "plain", TYPE_PLAINFILE, "Content plain");
		service.execute();
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void InvalidExistingLink() {
		CreateFileService service = new CreateFileService(token1, "link", TYPE_LINK, "/home/AAA");
		service.execute();
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void InvalidExistingApp() {
		CreateFileService service = new CreateFileService(token1, "app", TYPE_APP, "package.class.method");
		service.execute();
	}

	@Test(expected = TypeDoesNotExistException.class)
	public void InvalidTypeFile() {
		CreateFileService service = new CreateFileService(token1, "file1", "type", "ABC");
		service.execute();
	}

	@Test(expected = WrongContentException.class)
	public void InvalidContentOfDir() {
		CreateFileService service = new CreateFileService(token1, "dir1", TYPE_DIR, "AAA");
		service.execute();
	}

	@Test(expected = WrongContentException.class)
	public void InvalidContentofLink() {
		CreateFileService service = new CreateFileService(token1, "link1", TYPE_LINK, "AAA");
		service.execute();
	}
}

