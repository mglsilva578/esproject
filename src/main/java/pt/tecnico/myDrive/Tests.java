package pt.tecnico.myDrive;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.SuperUser;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;

public class Tests {
	private static final Logger log = LogManager.getRootLogger();
	
	public static void createHomeReadmeWithUsersList(MyDrive drive){
		File fileFound = drive.getFileByPathname("/home", false, null);
		if(fileFound instanceof Dir){
			Dir home = (Dir)fileFound;
			User superUser = drive.getUserByUsername(SuperUser.NAME);
			new PlainFile(drive, superUser, "README", "rwxd----", "lista de utilizadores", home);
			log.debug("createHomeReadmeWithUsersList SUCCESS!");			
		}else{
			throw new WrongTypeOfFileFoundException(fileFound.getName(), "Dir");
		}
	}

	public static void createDirUsrLocalBin(MyDrive drive){
		Dir slash = drive.getRootDir();
		Dir usr = new Dir(drive, "usr", "rwxd----", slash);
		Dir local = new Dir(drive, "local", "rwxd----", usr);
		new Dir(drive, "bin", "rwxd----", local);
		log.debug("createDirUsrLocalBin SUCCESS!");
	}
	
	public static void printContentOfHomeReadme(MyDrive drive){
		File readme = drive.getFileByPathname("/home/README", false, null);
		if(readme instanceof PlainFile){
			log.info("printing contents of /home/README :\n" + ((PlainFile)readme).getContent());
			log.debug("printContentOfHomeReadme SUCCESS!");
		}else{
			throw new WrongTypeOfFileFoundException(readme.getName(), "PlainFile");
		}
	}
	
	public static void removeDirUsrLocalBin(MyDrive drive){
		drive.removePlainFileOrEmptyDirectoryByPathname("/usr/local/bin");
		log.debug("removeDirUsrLocalBin SUCCESS!");
	}
	
	public static Document printXMLExport(MyDrive drive){
		return drive.exportXML();
	}

	public static void removeFileHomeReadme(MyDrive drive){
		drive.removePlainFileOrEmptyDirectoryByPathname("/home/README");
		log.debug("removeFileHomeReadme SUCCESS!");
	}
	
	public static void listContentsHome(MyDrive drive){
		log.trace("listing contents of /home :\n" + drive.listDirContent("/home"));
		log.debug("listContentsHome SUCCESS!");
	}
	
	public static void listDirContent(MyDrive drive){
		File fileFound = drive.getFileByPathname("/home", false, null);
		Dir home;
		if(fileFound instanceof Dir){
			home = (Dir)fileFound;
			new Dir(drive, "a", "rwxd----", home);
			new Dir(drive, "b", "rwxd----",home);
			drive.listDirContent("/home");
			log.debug("lisDirContent SUCCESS!");			
		}else{
			throw new WrongTypeOfFileFoundException(fileFound.getName(), "Dir");
		}
	}
}
