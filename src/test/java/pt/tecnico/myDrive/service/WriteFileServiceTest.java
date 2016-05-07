package pt.tecnico.myDrive.service;
/*Changes the contents of a text file, not a directory. This service receives a token, the file
name and a contents. All the contents of the file is replaced by the new contents.*/
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;
import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.*;
public class WriteFileServiceTest extends AbstractServiceTest {

	private static final String USERNAME_2 = "diogo";
	private static final String USERNAME_1 = "joseluisvf";
	private static final String PASSWORD_2 = "pass76534";
	private static final String PASSWORD_1 = "pass55816";
	private Long token;
	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		
		User userToAdd = new User(myDrive, USERNAME_1, PASSWORD_1, "JoseLuis", "rwxd----", null);
		new User(myDrive, USERNAME_2, PASSWORD_2, "DiogoFer", "rwxd----", null);
		
		LoginManager loginManager = myDrive.getLoginManager();
		token = loginManager.createSession(USERNAME_1, PASSWORD_1);
		
		Dir whereToAdd = (Dir)myDrive.getFileByPathname("/home/joseluisvf", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "plain", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		new App(myDrive, userToAdd, "app", userToAdd.getMask(), "package.class.method", whereToAdd);
		new Link(myDrive, userToAdd, "link", userToAdd.getMask(),"/home/joseluisvf/plain",whereToAdd);
		Dir newDir = new Dir(myDrive, "dir", userToAdd.getMask(), whereToAdd);
		
		new PlainFile(myDrive, userToAdd, "More Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", newDir);
		new PlainFile(myDrive, userToAdd, "A cold shower", userToAdd.getMask(), "When the heater is off, there is no salvation.", newDir);
		Dir otherUserHome = (Dir)myDrive.getFileByPathname("/home/diogo", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "File created by different user.", userToAdd.getMask(), "File with no permissions for other users.", otherUserHome);
	}

	@Test
	public void success(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Session session = loginManager.getSessionByToken(token);
		
		WriteFileService service = new WriteFileService(token, "/home/joseluisvf/plain", "abcd");
		service.execute();

		PlainFile existingFile = (PlainFile)myDrive.getFileByPathname("/home/joseluisvf/plain",false, session.getOwner());
		assertEquals(existingFile.getContent(),"abcd");
	}
	
	@Test
	public void writeRightContent(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Session session = loginManager.getSessionByToken(token);
		
		WriteFileService service = new WriteFileService(token,"/home/joseluisvf/link","/abcd/efgh");
		service.execute();
		
		PlainFile existingFile = (PlainFile)myDrive.getFileByPathname("/home/joseluisvf/plain",false, session.getOwner());
		assertEquals(existingFile.getContent(),"/abcd/efgh");
		
	}

	@Test(expected = InvalidTokenException.class)
	public void writeWithInvalidToken() {
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		
		WriteFileService service = new WriteFileService(wrongToken, "/home/joseluisvf/plain","abcd");
		service.execute();
	}

	@Test(expected = NoDirException.class)
	public void writeUnexistingFile(){
		WriteFileService service = new WriteFileService(token, "/home/joseluisvf/plain2","abcd");
		service.execute();
	}

	@Test(expected=WrongTypeOfFileFoundException.class)
	public void writeOnInvalidFile(){
		WriteFileService service = new WriteFileService(token,"/home/joseluisvf/dir","abcd");
		service.execute();
	}
	
	@Test(expected=WrongContentException.class)
	public void writeWrongContentinPlain(){
		WriteFileService service = new WriteFileService(token,"/home/joseluisvf/plain", null);
		service.execute();
	}
	
	@Test(expected=WrongContentException.class)
	public void writeWrongContentinApp(){
		WriteFileService service = new WriteFileService(token,"/home/joseluisvf/app", "dsak-fds- ");
		service.execute();
	}
	
	@Test(expected =PermissionDeniedException.class)
	public void writeWithWrongPermission(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token2 = loginManager.createSession(USERNAME_2, PASSWORD_2);
		
		WriteFileService service = new WriteFileService(token2,"/home/joseluisvf/plain","abcd");
		service.execute();
	}
	
	
}
