package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.NoDirException;

public class DeleteFileTest extends AbstractServiceTest{

	@Override
	protected void populate() {
		// recebe token e nome do ficheiro
		// caso seja uma directoria, todos os ficheiros nela contidos deverao ser apagados recursivamente
		MyDrive myDrive = MyDrive.getInstance();
		User userToAdd = new User(myDrive, "joseluisvf", "55816", "JoseLuis", "rwxd----", null);
		Dir whereToAdd = (Dir)myDrive.getFileByPathname("/home/joseluisvf", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		Dir newDir = new Dir(myDrive, "new_dir", userToAdd.getMask(), whereToAdd);
		new PlainFile(myDrive, userToAdd, "More Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", newDir);
		new PlainFile(myDrive, userToAdd, "A cold shower", userToAdd.getMask(), "When the heater is off, there is no salvation.", newDir);
		log.trace(myDrive.toString());
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
        
        
        // Try to remove a dir.
        String dirName = "new_dir";
        currentDirSizeBefore = currentDir.getSize();
        service = new DeleteFileService(token, dirName);
        service.execute();
        try{
        	currentDir.getFileByName(dirName);
        }catch(NoDirException nde){
        	log.trace("DeleteFileTest - Success!\n dir <" + dirName + "> was not found in <" + currentDir.getName() +"> after removal.");
        }
        currentDirSizeAfter = currentDir.getSize();
        assertEquals("Invalid number of files in dir", currentDirSizeBefore - 1, currentDirSizeAfter);
        
        log.trace(myDrive.toString());
    }
	
	
}
