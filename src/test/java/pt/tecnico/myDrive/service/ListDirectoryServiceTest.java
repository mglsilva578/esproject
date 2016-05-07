package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.service.dto.FileDto;
import pt.tecnico.myDrive.service.AbstractServiceTest;

public class ListDirectoryServiceTest extends AbstractServiceTest {
	private String username = "mglsilva578";
	private String password = "password";
	private User userToAdd;
	private User userWithoutPerm;
	private Dir whereToAdd;
	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		userToAdd = new User(myDrive, username, password, "MiguelSilva", "rwxd----", null);
		userWithoutPerm = new User(myDrive, "R3Moura", "7400574005", "RicardoMoura", "rwxd----", null);
		
		whereToAdd = (Dir)myDrive.getFileByPathname("/home/mglsilva578", false, userToAdd);
		new PlainFile(myDrive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty1", whereToAdd);
		new PlainFile(myDrive, userToAdd, "More Lusty Tales", userToAdd.getMask(), "More Lusty2", whereToAdd);
		new PlainFile(myDrive, userToAdd, "A cold shower", userToAdd.getMask(), "When the heater is off, there is no salvation.", whereToAdd);
		new Link(myDrive, userToAdd, "link", userToAdd.getMask(), "/home/mglsilva578/Lusty Tales", whereToAdd);
		new App(myDrive, userToAdd, "app", userToAdd.getMask(), "package.class.method", whereToAdd);
		new PlainFile(myDrive, userToAdd, "plain", userToAdd.getMask(), "plainfile1", whereToAdd);
		Dir otherDir = new Dir(myDrive, userToAdd, "dir", userToAdd.getMask(), whereToAdd);
		new Link(myDrive, userToAdd, "linklink", userToAdd.getMask(), "/home/mglsilva578/Lusty Tales", otherDir);
		new App(myDrive, userToAdd, "appapp", userToAdd.getMask(), "package.class.method", otherDir);
		new PlainFile(myDrive, userToAdd, "plainplain", userToAdd.getMask(), "plainfile1", otherDir);
		new Dir(myDrive, userToAdd, "dirdir", userToAdd.getMask(), otherDir);
	}
	@Test
	public void success(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		ListDirectoryService service = new ListDirectoryService(token, "No Path");
		Session session = loginManager.getSessionByToken(token);
		session.setCurrentDir(whereToAdd);
		service.execute();
		List<FileDto> returnService = service.result();
		
		assertEquals("Lusty Tales", returnService.get(1).getName());
		assertEquals("More Lusty Tales", returnService.get(2).getName());
		assertEquals("A cold shower", returnService.get(0).getName());
		assertEquals("->Lusty1", returnService.get(5).getContent());
		assertEquals(whereToAdd.getFileSet().size(), returnService.size());
	}
	
	@Test
	public void successWithPath(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(username, password);
		ListDirectoryService service = new ListDirectoryService(token,"/home/mglsilva578/dir");
		service.execute();
		List<FileDto> returnService = service.result();
		
		assertEquals("appapp", returnService.get(0).getName());
		assertEquals("dirdir", returnService.get(1).getName());
		assertEquals("linklink", returnService.get(2).getName());
		assertEquals("plainplain", returnService.get(3).getName());
		assertEquals("->Lusty1", returnService.get(2).getContent());
	}

	@Test (expected = InvalidTokenException.class)
	public void TokenFail(){
		Long token = (long) 123123123;
		ListDirectoryService service = new ListDirectoryService(token, "No Path");
		service.execute();
	}
	
	@Test (expected = PermissionDeniedException.class)
	public void notPermissions(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("R3Moura", "7400574005");
		
		Session session = myDrive.getLoginManager().getSessionByToken(token);
		session.setCurrentDir(whereToAdd);
		
		ListDirectoryService service = new ListDirectoryService(token, "No Path");
		service.execute();
	}
	
	@Test (expected = PermissionDeniedException.class)
	public void notPermissionswithPath(){
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession("R3Moura", "7400574005");
		
		
		ListDirectoryService service = new ListDirectoryService(token, "/home/mglsilva578/dir");
		service.execute();
	}
}
