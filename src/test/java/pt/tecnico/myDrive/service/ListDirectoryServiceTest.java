package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.service.dto.FileDto;

public class ListDirectoryServiceTest extends AbstractServiceTest {
	private String username = "mglsilva578";
	private String password = "68230";
	private User userToAdd;
	private Dir whereToAdd;
	@Override
	protected void populate() {
		// TODO Auto-generated method stub
		MyDrive myDrive = MyDrive.getInstance();
		userToAdd = new User(myDrive, username, password, "MiguelSilva", "rwxd----", null);
		whereToAdd = (Dir)myDrive.getFileByPathname("/home/mglsilva578", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		new PlainFile(myDrive, userToAdd, "More Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		new PlainFile(myDrive, userToAdd, "A cold shower", userToAdd.getMask(), "When the heater is off, there is no salvation.", whereToAdd);
	}
	@Test
	public void success(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		ListDirectoryService service = new ListDirectoryService(token);
		Session session = loginManager.getSessionByToken(token);
		session.setCurrentDir(whereToAdd);
		service.execute();
		List<FileDto> returnService = service.result();
		 
		//testing result parameters
		assertEquals("Lusty Tales", returnService.get(0).getName());
		assertEquals("More Lusty Tales", returnService.get(1).getName());
		assertEquals("A cold shower", returnService.get(2).getName());
		assertEquals(whereToAdd.getFileSet().size(), returnService.size());
		assertEquals(returnService.get(0).getOwner(), userToAdd.getName());
	}
	
	@Test (expected = InvalidTokenException.class)
	public void TokenFail(){
		Long token = (long) 123123123;
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
	}
	
	

}
