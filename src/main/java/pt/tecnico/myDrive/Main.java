package pt.tecnico.myDrive;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Nobody;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.SuperUser;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.util.DataExportationHelper;

public class Main {
	static final Logger log = LogManager.getRootLogger();

	public static void main(String [] args){
		log.trace("Welcome to MyDrive Application");
		try{
			if(args.length == 0){
				setup();
			}else{
				for (String s: args) xmlScan(new File(s));				
			}
			Main.printMyDrive();
			Main.exportDataToFile();
		}catch(MyDriveException mde){
			log.error(mde.getMessage());
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			FenixFramework.shutdown();
		}
	}

	@Atomic
	public static void init() {
		log.trace("Init: " + FenixFramework.getDomainRoot());
		MyDrive.getInstance().cleanup();
		setup();
	}

	@Atomic
	private static void setup(){
		MyDrive drive = MyDrive.getInstance();
		if(drive.isEmpty()){
			SuperUser rootUser = new SuperUser(drive);
			Dir slash = new Dir(drive, rootUser, Dir.SLASH_NAME, rootUser.getMask());
			drive.setRootDir(slash);
			Dir home = new Dir(drive, rootUser, "home", rootUser.getMask(), slash);
			new Dir(drive, rootUser, "root", rootUser.getMask(), home);
			Nobody nobody = new Nobody(drive);
			//additionalSetup();
			//testLogin();
			//testPermissions();
		}
		else{
			log.trace("MyDrive is not empty for which reason no setup was done.");
			return;
		}
	}

	private static void testLogin(){
		MyDrive drive = MyDrive.getInstance();
		LoginManager login = drive.getLoginManager();
		Long token1, token2, token3;
		// criar sessoes
		token1 = login.createSession("joseluisvf", "55816");
		token2 = login.createSession("joseluisvf", "55816");
		token3 = login.createSession("R3Moura", "74005");
		// usar sessoes 
		login.getSessionByToken(token1);
		login.getSessionByToken(token2);
		
		log.trace(token1 + "|" + token2 + "|" + token3 +"\n");
		log.trace(login.toString());
	}
	
	private static void testPermissions(){
		MyDrive drive = MyDrive.getInstance();
		User userToAdd = new User(drive, "ist176544", "76544", "Daniel", "rwxd----", null);
		Dir whereToAdd = (Dir)drive.getFileByPathname("/home/ist176544", false, drive.getUserByUsername("ist176544"));
		PlainFile file= new PlainFile(drive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
		User userToAdd2 = new User(drive, "ist176", "7654", "Danel", "rwxd----", null);
		log.trace(file.hasPermissionsForDelete(userToAdd2));
		log.trace(file.hasPermissionsForRead(userToAdd2));
		log.trace(file.hasPermissionsForWrite(userToAdd2));
		log.trace(file.hasPermissionsForExecute(userToAdd2));
		log.trace(file.hasPermissionsForDelete(userToAdd));
		log.trace(file.hasPermissionsForRead(userToAdd));
		log.trace(file.hasPermissionsForWrite(userToAdd));
		log.trace(file.hasPermissionsForExecute(userToAdd));
		
		
	}
	
	
	private static void additionalSetup(){
		MyDrive drive = MyDrive.getInstance();
		try{
			
			User userToAdd = new User(drive, "zttr", "76534", "Diogo", "rwxd----", null);
			
			userToAdd = new User(drive, "mglsilva578", "68230", "Miguel", "rwxd----", null);
			userToAdd = new User(drive, "R3Moura", "74005", "Ricardo", "rwxd----", null);
			userToAdd = new User(drive, "joseluisvf", "55816", "JoseLuis", "rwxd----", null);
			userToAdd = new User(drive, "ist176544", "76544", "Daniel", "rwxd----", null);
			
			Dir whereToAdd = (Dir)drive.getFileByPathname("/home/joseluisvf", false, drive.getUserByUsername("joseluisvf"));
			new PlainFile(drive, userToAdd, "Lusty Tales", userToAdd.getMask(), "Lusty Argonian Maid", whereToAdd);
			
			whereToAdd = (Dir)drive.getFileByPathname("/home/zttr", false, drive.getUserByUsername("zttr"));
			new Link(drive, userToAdd, "link1", userToAdd.getMask(),"/home/joseluisvf/Lusty Tales", whereToAdd);
			whereToAdd.changePlainFileContent("link1", "Even more lust", userToAdd);
			
			new App(drive, userToAdd, "app1", userToAdd.getMask(),"package.class.method", whereToAdd);
			whereToAdd = (Dir)drive.getFileByPathname("/home/R3Moura", false, drive.getUserByUsername("R3Moura"));
			new App(drive, userToAdd, "Skyrim", userToAdd.getMask(),"package.class.method", whereToAdd);
			new Dir(drive, userToAdd, "TestePermissoes", "--xd--x-", whereToAdd);
		}catch ( Exception e){
			e.printStackTrace();
		}
	}

	@Atomic
	private static void exportDataToFile(){
		MyDrive drive = MyDrive.getInstance();
		DataExportationHelper.writeDocumentToLocalStorage(drive.exportXML());
	}
	@Atomic
	private static void testMyDrive(){
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
				Main.xmlPrint();
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
	private static void printMyDrive(){
		MyDrive drive = MyDrive.getInstance();
		log.trace(drive.toString());
	}

	@Atomic
	private static void xmlPrint() {
		log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
		Document doc = MyDrive.getInstance().exportXML();
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		try { xmlOutput.output(doc, new PrintStream(System.out));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Atomic
	private static void xmlScan(File file) {
		log.trace("xmlScan: " + FenixFramework.getDomainRoot());
		MyDrive drive = MyDrive.getInstance();
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document)builder.build(file);
			drive.importXML(document.getRootElement());
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

}
