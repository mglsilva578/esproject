package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.CannotDeleteDirInUseBySessionException;
import pt.tecnico.myDrive.exception.CannotDeleteDotOrDotDotException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.NoDirException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;

public class DeleteFileTest extends AbstractServiceTest{

	private static final String EXISTING_USERNAME1 = "joseluisvf";
	private static final String EXISTING_USERNAME1_PASSWORD = "password";
	private static final String EXISTING_USER1_EXISTING_DIR = "new_dir";
	private static final String EXISTING_USER1_DIR_IN_EXISTING_DIR = "non empty dir";
	private static final String EXISTING_USER1_EXISTING_FILE1 = "Lusty Tales";
	private static final String EXISTING_USERNAME2 = "vfluisjose";
	private static final String EXISTING_USERNAME2_PASSWORD = "password";
	private static final String EXISTING_USER2_EXISTING_FILE_OWNED_DIFFERENT_USER = "Os Lusiadas";
	private static final String EXISTING_USER2_EXISTING_DIR_OWNED_DIFFERENT_USER = "cannot_delete_this";
	private static final String EXISTING_USERNAME3 = "Careless";
	private static final String EXISTING_USERNAME3_PASSWORD = "password";
	private static final String NON_EXISTING_FILE = "I don't exist.";

	private Long existingUser1Token;
	private Long existingUser2Token;
	private Long existingUser3Token;
	private Dir existingUser1CurrentDir;
	private Dir existingUser3CurrentDir;
	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		User existingUser1 = new User(myDrive, EXISTING_USERNAME1, EXISTING_USERNAME1_PASSWORD, "JoseLuis", "rwxd----", null);
		Dir whereToAdd = (Dir)myDrive.getFileByPathname("/home/joseluisvf", false, existingUser1);
		new PlainFile(myDrive, existingUser1, EXISTING_USER1_EXISTING_FILE1, existingUser1.getMask(), "Lusty Argonian Maid", whereToAdd);
		Dir newDir = new Dir(myDrive, existingUser1, EXISTING_USER1_EXISTING_DIR, existingUser1.getMask(), whereToAdd);
		new PlainFile(myDrive, existingUser1, "More Lusty Tales", existingUser1.getMask(), "Lusty Argonian Maid", newDir);
		new PlainFile(myDrive, existingUser1, "A cold shower", existingUser1.getMask(), "When the heater is off, there is no salvation.", newDir);
		Dir nonEmptyDir = new Dir(myDrive, existingUser1, EXISTING_USER1_DIR_IN_EXISTING_DIR, existingUser1.getMask(), newDir);
		new PlainFile(myDrive, existingUser1, "More Lusty Tales", existingUser1.getMask(), "Lusty Argonian Maid", nonEmptyDir);
		new PlainFile(myDrive, existingUser1, "A cold shower", existingUser1.getMask(), "When the heater is off, there is no salvation.", nonEmptyDir);
		
		User anotherUser = new User(myDrive, EXISTING_USERNAME2, EXISTING_USERNAME2_PASSWORD, "JoseLuis", "rwxd----", null);
		Dir differentUserHome = (Dir)myDrive.getFileByPathname("/home/vfluisjose", false, anotherUser);
		new Dir(myDrive, existingUser1, EXISTING_USER2_EXISTING_DIR_OWNED_DIFFERENT_USER, existingUser1.getMask(), differentUserHome);
		new PlainFile(myDrive, existingUser1, EXISTING_USER2_EXISTING_FILE_OWNED_DIFFERENT_USER, existingUser1.getMask(), "As armas e os baroes assinalados, que da ocidental ...", differentUserHome);
		
		
		
		
		new User(myDrive, EXISTING_USERNAME3, EXISTING_USERNAME3_PASSWORD, "Careless Guy", "rwxd---d", "/home/joseluisvf/careless_guy_home");
		
		loginUsers();
	}
	
	private void loginUsers() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		this.existingUser1Token = loginManager.createSession(EXISTING_USERNAME1, EXISTING_USERNAME1_PASSWORD);
		this.existingUser2Token = loginManager.createSession(EXISTING_USERNAME2, EXISTING_USERNAME2_PASSWORD);
		this.existingUser3Token = loginManager.createSession(EXISTING_USERNAME3, EXISTING_USERNAME3_PASSWORD);
		this.existingUser1CurrentDir = loginManager.getSessionByToken(existingUser1Token).getCurrentDir();
		this.existingUser3CurrentDir = loginManager.getSessionByToken(existingUser3Token).getCurrentDir();
	}

	@Test(expected = NoDirException.class)
	public void shouldThrowOnDeleteExistingFile() {
		int currentDirSizeBefore = givenSizeCurrentDirExistingUser1();

		whenExistingFileIsDeleted();
		
		thenCurrentDirShouldBeAsExpected(currentDirSizeBefore, EXISTING_USER1_EXISTING_FILE1);
	}

	private int givenSizeCurrentDirExistingUser1() {
		return this.existingUser1CurrentDir.getSize();
	}
	
	private void whenExistingFileIsDeleted() {
		DeleteFileService service = new DeleteFileService(existingUser1Token, EXISTING_USER1_EXISTING_FILE1);
		service.execute();
	}

	private void thenCurrentDirShouldBeAsExpected(int currentDirSizeBefore, String filenameToCheck) {
		int currentDirSizeAfter = this.existingUser1CurrentDir.getSize();
		assertEquals("Invalid number of files in dir", currentDirSizeBefore - 1, currentDirSizeAfter);
		this.existingUser1CurrentDir.getFileByName(filenameToCheck);
	}
	
	@Test(expected = NoDirException.class)
	public void shouldThrowOnDeleteExistingDir() {
		int currentDirSizeBefore = givenSizeCurrentDirExistingUser1();
		
		whenExistingDirIsDeleted();
		
		thenCurrentDirShouldBeAsExpected(currentDirSizeBefore, EXISTING_USER1_EXISTING_DIR);
	}
	
	private void whenExistingDirIsDeleted() {
		DeleteFileService service = new DeleteFileService(existingUser1Token, EXISTING_USER1_EXISTING_DIR);
		service.execute();
	}
	/*
	@Test(expected = NoDirException.class)
	public void shouldThrowOnRecursivelyDeleteExistingDir() {
		int currentDirSizeBefore = givenSizeExistingDirExistingUser1();
		
		whenExistingDirIsDeleted();
		
		thenCurrentDirShouldBeAsExpected(currentDirSizeBefore, EXISTING_USER1_EXISTING_DIR);
	}

	private int givenSizeExistingDirExistingUser1() {
		Dir existingDir = (Dir)this.existingUser1CurrentDir.getFileByName(EXISTING_USER1_EXISTING_DIR);
		return existingDir.getSize();
	}
	*/

	@Test(expected = NoDirException.class)
	public void shouldThrowOnDeleteNonExistingFile() {
		whenTryToDeleteNonExistentFile();
	}
	
	private void whenTryToDeleteNonExistentFile() {
		DeleteFileService service = new DeleteFileService(existingUser1Token, NON_EXISTING_FILE);
		service.execute();
	}
	
	@Test(expected = CannotDeleteDotOrDotDotException.class)
	public void shouldThrowOnDeleteDot() {
		whenTryToDeleteDot();
	}
	
	private void whenTryToDeleteDot() {
		DeleteFileService service = new DeleteFileService(existingUser1Token, Dir.DOT_NAME);
		service.execute();
	}
	
	@Test(expected = CannotDeleteDotOrDotDotException.class)
	public void shouldThrowOnDeleteDotDot() {
		whenTryToDeleteDotDot();
	}
	
	private void whenTryToDeleteDotDot() {
		DeleteFileService service = new DeleteFileService(existingUser1Token, Dir.DOTDOT_NAME);
		service.execute();
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void shouldThrowOnDeleteDirWithoutHavingPermission() {
		whenTryToDeleteDirWithoutHavingPermissions();
	}
	
	private void whenTryToDeleteDirWithoutHavingPermissions() {
		DeleteFileService service = new DeleteFileService(existingUser2Token, EXISTING_USER2_EXISTING_DIR_OWNED_DIFFERENT_USER);
		service.execute();
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void shouldThrowOnDeleteFileWithoutHavingPermission() {
		whenTryToDeleteFileWithoutHavingPermissions();
	}
	
	private void whenTryToDeleteFileWithoutHavingPermissions() {
		DeleteFileService service = new DeleteFileService(existingUser2Token, EXISTING_USER2_EXISTING_FILE_OWNED_DIFFERENT_USER);
		service.execute();
	}

	@Test(expected = InvalidTokenException.class)
	public void deleteWithInvalidToken() {
		Long invalidToken = givenInvalidToken();
		whenTryToDeleteFileUsingInvalidToken(invalidToken);
	}
	
	private Long givenInvalidToken() {
		Long invalidToken = new Long(new BigInteger(64, new Random()).longValue());
		
		while (invalidTokenIsNotSameAsExistingTokens(invalidToken)) {
			invalidToken = new Long(new BigInteger(64, new Random()).longValue());
		}
		
		return invalidToken;
	}

	private boolean invalidTokenIsNotSameAsExistingTokens(Long invalidToken) {
		return (invalidToken == this.existingUser1Token) ||
				(invalidToken == this.existingUser2Token);
	}
	
	private void whenTryToDeleteFileUsingInvalidToken(Long invalidToken) {
		DeleteFileService service = new DeleteFileService(invalidToken, EXISTING_USER2_EXISTING_FILE_OWNED_DIFFERENT_USER);
		service.execute();
	}
	
	@Test(expected = CannotDeleteDirInUseBySessionException.class)
	public void shouldThrowOnDeleteAnotherCurrentDir() {
		whenTryToDeleteAnotherCurrentDir();
	}
	
	private void whenTryToDeleteAnotherCurrentDir() {
		DeleteFileService service = new DeleteFileService(this.existingUser1Token, this.existingUser3CurrentDir.getName());
		service.execute();
	}
}
