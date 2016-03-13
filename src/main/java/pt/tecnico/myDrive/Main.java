package pt.tecnico.myDrive;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.SuperUser;
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
		}
		else{
			return;
		}
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
