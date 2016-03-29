package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.CannotDeleteDotOrDotDotException;
import pt.tecnico.myDrive.exception.InvalidPasswordException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.NoDirException;
import pt.tecnico.myDrive.exception.UsernameDoesNotExistException;

public class DeleteFileTest extends AbstractServiceTest{

	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		User userToAdd = new User(myDrive, "joseluisvf", "55816", "JoseLuis", "rwxd----", null);
		Dir whereToAdd = (Dir)myDrive.getFileByPathname("/home/joseluisvf", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		Dir newDir = new Dir(myDrive, "new_dir", userToAdd.getMask(), whereToAdd);
		new PlainFile(myDrive, userToAdd, "More Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", newDir);
		new PlainFile(myDrive, userToAdd, "A cold shower", userToAdd.getMask(), "When the heater is off, there is no salvation.", newDir);
	}

	@Test(expected = NoDirException.class)
	public void deleteExistingFile() {
		String fileName = "Lusty Tales";
		String username = "joseluisvf";
		String password = "55816";
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		DeleteFileService service = new DeleteFileService(token, fileName);
		Session session = loginManager.getSessionByToken(token);
		Dir currentDir = session.getCurrentDir();
		int currentDirSizeBefore = currentDir.getSize();

		service.execute();
		int currentDirSizeAfter = currentDir.getSize();
		currentDir.getFileByName(fileName);
		assertEquals("Invalid number of files in dir", currentDirSizeBefore - 1, currentDirSizeAfter);
	}

	@Test(expected = NoDirException.class)
	public void deleteExistingDir() {
		String dirName = "new_dir";
		String username = "joseluisvf";
		String password = "55816";
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		DeleteFileService service = new DeleteFileService(token, dirName);
		Session session = loginManager.getSessionByToken(token);
		Dir currentDir = session.getCurrentDir();
		int currentDirSizeBefore = currentDir.getSize();

		service = new DeleteFileService(token, dirName);
		service.execute();
		currentDir.getFileByName(dirName);
		int currentDirSizeAfter = currentDir.getSize();
		assertEquals("Invalid number of files in dir", currentDirSizeBefore - 1, currentDirSizeAfter);
		log.trace("\n\nFINAL DO TEST\n\n");
	}

	@Test(expected = NoDirException.class)
	public void deleteInvalidFile() {
		String fileName = "I don't exist";
		String username = "joseluisvf";
		String password = "55816";
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		DeleteFileService service = new DeleteFileService(token, fileName);

		service.execute();
	}

	@Test(expected = CannotDeleteDotOrDotDotException.class)
	public void deleteDot() {
		String fileName = Dir.DOT_NAME;
		String username = "joseluisvf";
		String password = "55816";
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		DeleteFileService service = new DeleteFileService(token, fileName);

		service.execute();
	}
	
	@Test(expected = CannotDeleteDotOrDotDotException.class)
	public void deleteDotDot() {
		String fileName = Dir.DOTDOT_NAME;
		String username = "joseluisvf";
		String password = "55816";
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		DeleteFileService service = new DeleteFileService(token, fileName);

		service.execute();
	}
	
	@Test(expected = UsernameDoesNotExistException.class)
	public void deleteWithInvalidUsername() {
		String fileName = "Lusty Tales";
		String username = "jose_das_couves";
		String password = "55816";
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		DeleteFileService service = new DeleteFileService(token, fileName);

		service.execute();
	}

	@Test(expected = InvalidPasswordException.class)
	public void deleteWithInvalidPassword() {
		String fileName = "Lusty Tales";
		String username = "joseluisvf";
		String password = "11111";
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		DeleteFileService service = new DeleteFileService(token, fileName);

		service.execute();
	}

	@Test(expected = InvalidTokenException.class)
	public void deleteWithInvalidToken() {
		String fileName = "Lusty Tales";
		Long token = new Long(1);
		DeleteFileService service = new DeleteFileService(token, fileName);

		service.execute();
	}
}
