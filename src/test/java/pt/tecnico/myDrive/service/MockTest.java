package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Extension;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.WrongContentException;

@RunWith(JMockit.class)
public class MockTest extends AbstractServiceTest{
	
	private Dir homeDir1;
	private User user1;
	@Mocked private MyDrive myDrive;
	private static final String USERNAME1 = "username1";
	private static final String PASS1 = "password1";
	private static final String CONTENT1_PLAIN_FILE = "content1 (PlainFile)";
	private static Long token = (long) 1;
	private App appAdobe;
	private PlainFile plainOrg;
	private Dir dir;
	private App appOrg;
	
	@Override
	protected void populate() {
		new Expectations(){{
			myDrive = MyDrive.getInstance();
			user1 = new User(myDrive, USERNAME1, PASS1, "name1", "rwxd----", null);
			//token = myDrive.getLoginManager().createSession(USERNAME1, PASS1);
			new Extension("pdf", "adobe_reader", user1);
			new Extension("txt", "bloco_de_notas", user1);
			//homeDir1 = (Dir) myDrive.getFileByPathname("/home/username1", false, user1);
			dir = new Dir(myDrive, user1, "dir1", user1.getMask());
			new PlainFile(myDrive, user1, "plain1", user1.getMask(), "");
			plainOrg = new PlainFile(myDrive, user1, "plain2.org", user1.getMask(), "");
			dir = new Dir(myDrive, user1, "dir1", user1.getMask());
			new App(myDrive, user1, "app1", user1.getMask(), "pt.tecnico.myDrive.domain", dir);
			appAdobe = new App(myDrive, user1, "adobe_reader", user1.getMask(), "pt.tecnico.myDrive.domain.pdf", dir);
			new App(myDrive, user1, "bloco_de_notas", user1.getMask(), "pt.tecnico.myDrive.domain.txt", dir);
			appOrg = new App(myDrive, user1, "org", user1.getMask(), "pt.tecnico.myDrive.domain.org", dir);
		}};
		
	}
	
	@Test
	public void successMockAssociation(){
		final String path = "/home/username1/texto.pdf";
		final String[] arg = new String[]{"a"};
				
		new Expectations(){ {
			myDrive.getLoginManager().getSessionByToken(token).getOwner();
			result = user1;
			myDrive.getFileByPathname(path, false, user1);
			result = appAdobe;
		}};
		
		ExecuteFileService service = new ExecuteFileService(token, path, arg);
		service.execute();
				
	}
	@Test(expected=WrongContentException.class)
	public void failnoassociation(){
		final String path = "/home/username1/plain2.org";
	 	final String args = "a";
	 	
	 	new Expectations(){{
	 		myDrive.getLoginManager().getSessionByToken(token).getOwner();
	 		result = user1;
	 		myDrive.getFileByPathname(path, false, user1);
	 		result = appOrg;
	 	}};
	 
	 	ExecuteFileService service = new ExecuteFileService(token, path, args);
		service.execute();
	}
	@Test
    public void successMockWrite() {
        new MockUp<WriteFileServiceTest>() {
        	@Mock
        	void dispacth() throws MyDriveException{}
        };
        
        WriteFileService service = new WriteFileService(token,"/home/$USER/plain","abcd");
        service.execute();
        
    }
	@Test
    public void successMockRead() {
        new MockUp<ReadFileService>() {
        	@Mock void dispatch() {  }
        	@Mock String result() { return CONTENT1_PLAIN_FILE; }
        };

        ReadFileService service = new ReadFileService(token, "linkENV");
        service.execute();
        assertEquals(service.getResult(), CONTENT1_PLAIN_FILE);
    }
	
	

}
