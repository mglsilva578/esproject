package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Extension;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.WrongContentException;

@RunWith(JMockit.class)
public class MockTest extends AbstractServiceTest{
	
	private User user1;
	private MyDrive myDrive;
	private static final String USERNAME1 = "username1";
	private static final String PASS1 = "passwoord1";
	private static final String CONTENT1_PLAIN_FILE = "content1 (PlainFile)";
	private static Long token;
	
	@Override
	protected void populate() {
		new Expectations(){{
			myDrive = MyDrive.getInstance();
			user1 = new User(myDrive, USERNAME1, PASS1, "name1", "rwxd----", null);
			token = myDrive.getLoginManager().createSession(USERNAME1, PASS1);
			new Extension("pdf", "adobe_reader", user1);
			new Extension("txt", "bloco_de_notas", user1);
		}};
		
	}
	
	@Test
	public void successMockAssociation(){
		final String path = "/home/username1/texto.pdf";
		final String[] arg = null;
		final Long token;
		new MockUp<ExecuteFileService>() {
			@Mock 
			void dispacth() throws MyDriveException{
				for(Extension extension : user1.getExtensionSet()){
					if(path.contains(extension.getFileExtension())){
						App app = (App) myDrive.getFileByName(extension.getFileName());
						app.execute(arg);
					}
				}
			}
		};
		token = myDrive.getLoginManager().createSession(USERNAME1, PASS1);
		ExecuteFileService service = new ExecuteFileService(token, path, arg);
		service.execute();
				
	}
	@Test(expected=WrongContentException.class)
	public void failnoassociation(){
		final String path = "/home/username1/plain2.org";
	 	final String args = "a";
	 	final Long token;
	 
	 	
	 	new MockUp<ExecuteFileService>(){
	 		@Mock
	 		void dispacth() throws MyDriveException{
	 			throw new WrongContentException();
	 		}
	 	};
	 	token = myDrive.getLoginManager().createSession(USERNAME1, PASS1);
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
