package pt.tecnico.myDrive.service;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidTokenException;

public class ListDirectoryServiceTest extends AbstractServiceTest {
	private String username = "mglsilva578";
	private String password = "68230";
	private User userToAdd;
	@Override
	protected void populate() {
		// TODO Auto-generated method stub
		MyDrive myDrive = MyDrive.getInstance();
		userToAdd = new User(myDrive, username, password, "MiguelSilva", "rwxd----", null);
		Dir whereToAdd = (Dir)myDrive.getFileByPathname("/home/mglsilva578", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		Dir newDir = new Dir(myDrive, "new_dir", userToAdd.getMask(), whereToAdd);
		new PlainFile(myDrive, userToAdd, "More Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", newDir);
		new PlainFile(myDrive, userToAdd, "A cold shower", userToAdd.getMask(), "When the heater is off, there is no salvation.", newDir);
		
		
	}
	@Test
	public void success(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		ListDirectoryService service = new ListDirectoryService(token);
		Dir currentDir = loginManager.getSessionByToken(token).getCurrentDir();
		service.execute();
			
	}
	
	@Test (expected = InvalidTokenException.class)
	public void TokenFail(){
		Long token = (long) 123123123;
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
	}
	
	

}
