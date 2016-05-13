package pt.tecnico.myDrive.service;

import org.junit.Test;


import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.CannotExecuteDirectoryException;
import pt.tecnico.myDrive.exception.NoDirException;



public class ExecuteFileServiceTest extends AbstractServiceTest {
	private static final String USERNAME1 = "username1";
	private static final String PASS1 = "password1";
	private static final String USERNAME2 = "username2";
	private static final String PASS2 = "password2";
	private User user1;
	private Dir homeDir1;
	private MyDrive myDrive;
	private ExecuteFileService service;
	
	@Override
	protected void populate() {
		myDrive = MyDrive.getInstance();
		user1 = new User(myDrive, USERNAME1, PASS1, "name1", "rwxd----", null);
		new User(myDrive, USERNAME2, PASS2, "name2", "rwxd----", null);
		homeDir1 = (Dir) myDrive.getFileByPathname("/home/username1", false, user1);
		new PlainFile(myDrive, user1, "plain1", user1.getMask(), "", homeDir1);
		new PlainFile(myDrive, user1, "plain2.org", user1.getMask(), "", homeDir1);
		new Link(myDrive, user1, "link1", user1.getMask(), "/home/username1/plain1", homeDir1);
		new App(myDrive, user1, "app1", user1.getMask(), "pt.tecnico.myDrive.domain", homeDir1);
		new Dir(myDrive, user1, "dir1", user1.getMask(), homeDir1);
		new App(myDrive, user1, "adobe_reader", user1.getMask(), "pt.tecnico.myDrive.domain.pdf", homeDir1);
		new App(myDrive, user1, "bloco_de_notas", user1.getMask(), "pt.tecnico.myDrive.domain.txt", homeDir1);
		
	}
	
	@Test
	public void sucessInExecutePlainFile(){
		String[] args= null;
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		service = new ExecuteFileService(token,"/home/username1/plain1",args);
		service.execute();
		
	}
	
	@Test
	public void successInExecuteLink(){
		String[] args = null;
		Long token = myDrive.getLoginManager().createSession(USERNAME1, PASS1);
		service = new ExecuteFileService(token, "/home/username1/link1", args);
		service.execute();
	}
	
	@Test
	public void successInExecuteApp(){
		String[] args = null;
		Long token = myDrive.getLoginManager().createSession(USERNAME1, PASS1);
		service = new ExecuteFileService(token, "/home/username1/app1", args);
		service.execute();
	}
	
	@Test(expected= CannotExecuteDirectoryException.class)
	public void cannotExecuteDirectory(){
		String[] args = null;
		final String pathToDir = "/home/username1/dir1";
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		service = new ExecuteFileService(token, pathToDir, args);
		service.execute();
	}

	@Test(expected= NoDirException.class)
	public void unexistingPlainFile(){
		String[] args = null;
		LoginManager loginmanager = myDrive.getLoginManager();
		Long token = loginmanager.createSession(USERNAME1, PASS1);
		service = new ExecuteFileService(token,"/local/invalid",args);
		service.execute();
	}
	

}
