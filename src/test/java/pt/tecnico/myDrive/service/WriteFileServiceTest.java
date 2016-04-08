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

	@Override
	protected void populate() {
		//user jose
		//Dir wheretoadd tem ficheiro lusty tales tem dir newdir
		//Dir newDir tem ficheiro More Lusty Tales e cold shower
		MyDrive myDrive = MyDrive.getInstance();
		User userToAdd1 = new User(myDrive, "diogo", "76534", "DiogoFer", "rwxd----", null);
		User userToAdd = new User(myDrive, "joseluisvf", "55816", "JoseLuis", "rwxd----", null);
		Dir whereToAdd = (Dir)myDrive.getFileByPathname("/home/joseluisvf", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		Dir newDir = new Dir(myDrive, "new_dir", userToAdd.getMask(), whereToAdd);
		new PlainFile(myDrive, userToAdd, "More Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", newDir);
		new PlainFile(myDrive, userToAdd, "A cold shower", userToAdd.getMask(), "When the heater is off, there is no salvation.", newDir);
		new App(myDrive, userToAdd, "app",userToAdd.getMask(),null,whereToAdd);
		
	}

	@Test
	public void writeExistingFile(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", "55816");
		WriteFileService service = new WriteFileService(token,"Lusty Tales","abcd");
		service.execute();
		Session session = loginManager.getSessionByToken(token);
		assertEquals(myDrive.getFileByPathname("/home/joseluisvf/Lusty Tales",false, session.getOwner()),"abcd");
	}


	@Test(expected = InvalidTokenException.class)
	public void writeWithInvalidToken() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", "55816");
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
		Long token = loginManager.createSession("joseluisvf", "55816");
		WriteFileService service = new WriteFileService(token,"Unexisting File","abcd");
		service.execute();
	}

	@Test(expected=WrongTypeOfFileFoundException.class)
	public void writeOnInvalidFile(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", "55816");
		WriteFileService service = new WriteFileService(token,"new_dir","abcd");
		service.execute();

	}
	@Test(expected=WrongContentException.class)
	public void writeWrongContent(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("joseluisvf", "55816");
		WriteFileService service = new WriteFileService(token,"app","abcd");
		service.execute();
	}

	@Test(expected =PermissionDeniedException.class)
	public void writeWithWrongPermission(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("diogo", "76534");
		WriteFileService service = new WriteFileService(token,"Lusty Tales","abcd");
		service.execute();

	}
}
