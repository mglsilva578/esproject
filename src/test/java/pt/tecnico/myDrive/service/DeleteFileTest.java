package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
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

	@Test
    public void success() {
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
        try{
        	currentDir.getFileByName(fileName);
        }catch(NoDirException nde){
        	log.trace("DeleteFileTest - Success!\n file <" + fileName + "> was not found in <" + currentDir.getName() +"> after removal.");
        }
        assertEquals("Invalid number of files in dir", currentDirSizeBefore - 1, currentDirSizeAfter);
        
        String dirName = "new_dir";
        currentDirSizeBefore = currentDir.getSize();
        service = new DeleteFileService(token, dirName);
        service.execute();
        try{
        	currentDir.getFileByName(dirName);
        }catch(NoDirException nde){
        	//TODO o que fazer aqui? Não temos um assert null porque o getFileByName lanca excepcao. Nao metemos nada ou e' preferivel como esta' ?
        	log.trace("DeleteFileTest - Success!\n dir <" + dirName + "> was not found in <" + currentDir.getName() +"> after removal.");
        }
        currentDirSizeAfter = currentDir.getSize();
        assertEquals("Invalid number of files in dir", currentDirSizeBefore - 1, currentDirSizeAfter);
    }
	
	@Test(expected = NoDirException.class)
    public void removeInvalidFile() {
		String fileName = "I don't exist";
		String username = "joseluisvf";
		String password = "55816";
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		DeleteFileService service = new DeleteFileService(token, fileName);

		service.execute();
    }
	
	@Test(expected = UsernameDoesNotExistException.class)
    public void removeWithInvalidUsername() {
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
    public void removeWithInvalidPassword() {
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
    public void removeWithInvalidToken() {
		String fileName = "Lusty Tales";
		Long token = new Long(1);
		DeleteFileService service = new DeleteFileService(token, fileName);

		service.execute();
    }
}
