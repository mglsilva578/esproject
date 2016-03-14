package pt.tecnico.myDrive;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.SuperUser;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;

public class Main {
	static final Logger log = LogManager.getRootLogger();

	public static void main(String [] args){
		log.trace("Welcome to MyDrive Application");
		try{
			Main.setup();
			Main.testMyDrive();
			Main.printMyDrive();
		}catch(MyDriveException mde){
			log.error(mde.getMessage());
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			FenixFramework.shutdown();
		}
	}

	@Atomic
	public static void init(){
		//TODO clean MyDrive
	}

	@Atomic
	public static void setup(){
		MyDrive drive = MyDrive.getInstance();
		if(drive.isEmpty()){
			SuperUser rootUser = new SuperUser(drive);
			Dir slash = new Dir(drive, rootUser, Dir.SLASH_NAME, rootUser.getMask());
			Dir home = new Dir(drive, rootUser, "home", rootUser.getMask(), slash);
			new Dir(drive, rootUser, "root", rootUser.getMask(), home);
			
			//TODO apagar
			Main.additionalSetup();
		}
		else{
			return;
		}
	}
	
	private static void additionalSetup(){
		MyDrive drive = MyDrive.getInstance();
		SuperUser rootUser = (SuperUser)drive.getUserByUsername(SuperUser.NAME);
		Dir slash = drive.getRootDir();
		Dir home2 = new Dir(drive, rootUser, "home2", rootUser.getMask(), slash);    
	    Dir home3 = new Dir(drive, rootUser, "home3", rootUser.getMask(), slash);
	    Dir home4 = new Dir(drive, rootUser, "home4", rootUser.getMask(), home3);
	    Dir home5 = new Dir(drive, rootUser, "home5", rootUser.getMask(), home4);
	    
	    User userToAdd = new User(drive, "zttr", "76534", "Diogo", "rwxd----");
	    userToAdd = new User(drive, "mglsilva578", "68230", "Miguel", "rwxd----");
	    userToAdd = new User(drive, "R3Moura", "74005", "Ricardo", "rwxd----");
	    userToAdd = new User(drive, "joseluisvf", "55816", "JoseLuis", "rwxd----");
	    userToAdd = new User(drive, "ist176544", "76544", "Daniel", "rwxd----");

	    PlainFile plainFile = new PlainFile(drive, userToAdd, "plainfile1", userToAdd.getMask(), "Lusty Argonian Maid", home3);
	    String content = plainFile.readContent("/home3/plainfile1");
	    
	    Link link = new Link(drive, userToAdd, "link1", userToAdd.getMask(),"/home/home3/plainfile1", home4);
	    content = link.readContent("/home3/home4/link1");
	    
	    App app = new App(drive, userToAdd, "app1", userToAdd.getMask(),"package.class.method", home5);
	    content = app.readContent("/home3/home4/home5/app1");

	}
	
	@Atomic
	public static void testMyDrive(){
		MyDrive drive = MyDrive.getInstance();
		try{
			try{
				Tests.createHomeReadmeWithUsersList(drive);
			}catch( MyDriveException mde){
				log.error(mde.getMessage());
			}
			
			try{
				Tests.createDirUsrLocalBin(drive);
			}catch( MyDriveException mde){
				log.error(mde.getMessage());
			}
			
			try{
				Tests.printContentOfHomeReadme(drive);
			}catch( MyDriveException mde){
				log.error(mde.getMessage());
			}		
			
			try{
				Tests.removeDirUsrLocalBin(drive);
			}catch( MyDriveException mde){
				log.error(mde.getMessage());
			}
			
			try{
				Tests.removeFileHomeReadme(drive);
			}catch( MyDriveException mde){
				log.error(mde.getMessage());
			}
			
			try{
				Tests.listContentsHome(drive);
			}catch( MyDriveException mde){
				log.error(mde.getMessage());
			}
			
			try{
				Tests.listDirContent(drive);
			}catch( MyDriveException mde){
				log.error(mde.getMessage());
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}

	@Atomic
	public static void printMyDrive(){
		MyDrive drive = MyDrive.getInstance();
		log.trace(drive.toString());
	}

	@Atomic
	public static void xmlScan(Document file){
		MyDrive md = FenixFramework.getDomainRoot().getMydrive().getInstance();
		md.importXML(file.getRootElement());
	}

}
