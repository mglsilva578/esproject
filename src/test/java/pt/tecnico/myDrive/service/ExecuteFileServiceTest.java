package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Extension;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.CannotFindFileException;
import pt.tecnico.myDrive.exception.InvalidAppMethodException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ExecuteFileServiceTest extends AbstractServiceTest {
	private static final String USERNAME1 = "username1";
	private static final String PASS1 = "password1";
	private static final String USERNAME2 = "username2";
	private static final String PASS2 = "password2";
	private User user1;
	private Dir homeDir1;
	private MyDrive myDrive;
	@Override
	protected void populate() {
		myDrive = MyDrive.getInstance();
		user1 = new User(myDrive, USERNAME1, PASS1, "name1", "rwxd----", null);
		new User(myDrive, USERNAME2, PASS2, "name2", "rwxd----", null);
		homeDir1 = (Dir) myDrive.getFileByPathname("/home/username1", false, user1);
		new PlainFile(myDrive, user1, "plain1", user1.getMask(), "", homeDir1);
		new Link(myDrive, user1, "link1", user1.getMask(), "/home/username1/plain1", homeDir1);
		new App(myDrive, user1, "app1", user1.getMask(), "pt.tecnico.myDrive.domain", homeDir1);
		new Dir(myDrive, user1, "dir1", user1.getMask(), homeDir1);
		new App(myDrive, user1, "adobe_reader", user1.getMask(), "pt.tecnico.myDrive.domain.pdf", homeDir1);
		new App(myDrive, user1, "bloco_de_notas", user1.getMask(), "pt.tecnico.myDrive.domain.txt", homeDir1);
		new Extension("pdf", "adobe_reader", user1);
		new Extension("txt", "bloco_de_notas", user1);
	}
	
	@Test
	public void sucessInExecute(){
		String[] args= null;
		LoginManager loginManager = myDrive.getLoginManager();
		Long token = loginManager.createSession(USERNAME1, PASS1);
		ExecuteFileService service = new ExecuteFileService(token,"/home/username1/plain1",args);
		service.execute();
		/*PlainFile plain1 = (PlainFile) myDrive.getFileByPathname("/home/username1/plain1", false,user1);
		String content = plain1.getContent();
		assertEquals("args",content);*/
	}

	@Test(expected= CannotFindFileException.class)
	public void unexistingPlainFile(){
		String[] args = null;
		LoginManager loginmanager = myDrive.getLoginManager();
		Long token = loginmanager.createSession(USERNAME1, PASS1);
		ExecuteFileService service = new ExecuteFileService(token,"/local/invalid",args);
		service.execute();
	}
	
	@Test(expected=WrongTypeOfFileFoundException.class)
	public void wrongTypeOfFile(){
		String[] args = null;
		LoginManager loginmanager = myDrive.getLoginManager();
		Long token = loginmanager.createSession(USERNAME1, PASS1);
		ExecuteFileService service = new ExecuteFileService(token,"/home/username1",args);
		service.execute();
	}
	
	@Test
	public void successMockAssociation(){
		String path = "/home/username1/texto.pdf";
		String[] arg = null;
		new MockUp<ExecuteFileService>(){
			@Mock
			void dispacth() {
				for(Extension extension : user1.getExtensionSet()){
					if(path.contains(extension.getFileExtension())){
						App app = (App) myDrive.getFileByName(extension.getFileName());
						app.execute(arg);
					}
				}
					
			}
		};
		Long token = myDrive.getLoginManager().createSession(USERNAME1, PASS1);
		ExecuteFileService service = new ExecuteFileService(token, path, arg);
		service.execute();
				
	}
	
	@Test(expected=InvalidAppMethodException.class)
	public void failnoassociation(){
		String path = "não existe";
		String[] args = null;
		new MockUp<ExecuteFileService>(){
			@Mock
			void dispacth() throws MyDriveException{
				throw new InvalidAppMethodException("não existe");
			}
		};
		Long token = myDrive.getLoginManager().createSession(USERNAME1, PASS1);
		ExecuteFileService service = new ExecuteFileService(token, path, args);
		service.execute();
	}

}
