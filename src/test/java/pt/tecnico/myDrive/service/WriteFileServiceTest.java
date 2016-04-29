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

	private static final String PASSWORD_2 = "pass76534";
	private static final String PASSWORD_1 = "pass55816";

	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		new User(myDrive, "diogo", PASSWORD_2, "DiogoFer", "rwxd----", null);
		User userToAdd = new User(myDrive, "joseluisvf", PASSWORD_1, "JoseLuis", "rwxd----", null);
		Dir whereToAdd = (Dir)myDrive.getFileByPathname("/home/joseluisvf", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		Dir newDir = new Dir(myDrive, "new_dir", userToAdd.getMask(), whereToAdd);
		new PlainFile(myDrive, userToAdd, "More Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", newDir);
		new PlainFile(myDrive, userToAdd, "A cold shower", userToAdd.getMask(), "When the heater is off, there is no salvation.", newDir);
		new App(myDrive, userToAdd, "app",userToAdd.getMask(),null,whereToAdd);
		new Link(myDrive, userToAdd, "link",userToAdd.getMask(),"/home/joseluisvf/app",whereToAdd);
		Dir otherUserHome = (Dir)myDrive.getFileByPathname("/home/diogo", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "File created by different user.", userToAdd.getMask(), "File with no permissions for other users.", otherUserHome);
	}

	@Test
	public void writeExistingFile(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", PASSWORD_1);
		WriteFileService service = new WriteFileService(token,"Lusty Tales","abcd");
		service.execute();
		Session session = loginManager.getSessionByToken(token);
		PlainFile existingFile = (PlainFile)myDrive.getFileByPathname("/home/joseluisvf/Lusty Tales",false, session.getOwner());
		assertEquals("The content found <" + existingFile.getContent() + "> is not the same as what is expected <abcd>", existingFile.getContent(),"abcd");
	}

	@Test(expected = InvalidTokenException.class)
	public void writeWithInvalidToken() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", PASSWORD_1);
		Long wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		while(token == wrongToken){
			wrongToken = new Long(new BigInteger(64, new Random()).longValue()); 
		}
		Session session = loginManager.getSessionByToken(token);
		WriteFileService service = new WriteFileService(wrongToken,"Lusty Tales","abcd");
		service.execute();
		assertEquals(myDrive.getFileByPathname("/home/joseluisvf/Lusty Tales",false, session.getOwner()),"abcd");
	}

	@Test(expected = NoPlainFileException.class)
	public void writeUnexistingFile(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", PASSWORD_1);
		WriteFileService service = new WriteFileService(token,"Unexisting File","abcd");
		service.execute();
	}

	@Test(expected=WrongTypeOfFileFoundException.class)
	public void writeOnInvalidFile(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", PASSWORD_1);
		WriteFileService service = new WriteFileService(token,"new_dir","abcd");
		service.execute();

	}
	
	@Test(expected=WrongContentException.class)
	public void writeWrongContent(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", PASSWORD_1);
		WriteFileService service = new WriteFileService(token,"link", null);
		service.execute();
	}
	
	@Test
	public void writeRightContent(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", PASSWORD_1);
		WriteFileService service = new WriteFileService(token,"link","/abcd/efgh");
		service.execute();
	}

	@Test(expected =PermissionDeniedException.class)
	public void writeWithWrongPermission(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("diogo", PASSWORD_2);
		WriteFileService service = new WriteFileService(token,"File created by different user.","abcd");
		service.execute();
	}
	
}
