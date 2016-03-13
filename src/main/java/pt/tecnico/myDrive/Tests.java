package pt.tecnico.myDrive;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.SuperUser;
import pt.tecnico.myDrive.domain.User;

public class Tests {
	private static final Logger log = LogManager.getRootLogger();
	public static void createHomeReadmeWithUsersList(MyDrive drive){
		Dir home = (Dir)drive.getFileByPathname("/home");
		User superUser = drive.getUserByUsername(SuperUser.NAME);
		new PlainFile(drive, superUser, "README", "rwxd----", "lista de utilizadores", home);
		log.debug("createHomeReadmeWithUsersList SUCCESS!");
	}

	public static void createDirUsrLocalBin(MyDrive drive){
		Dir slash = drive.getRootDir();
		Dir usr = new Dir(drive, "usr", "rwxd----", slash);
		Dir local = new Dir(drive, "local", "rwxd----", usr);
		new Dir(drive, "bin", "rwxd----", local);
		log.debug("createDirUsrLocalBin SUCCESS!");
	}
	
	public static void printContentOfHomeReadme(MyDrive drive){
		File readme = drive.getFileByPathname("/home/README");
		if(readme instanceof PlainFile){
			System.out.println(((PlainFile)readme).getContent());
			log.debug("printContentOfHomeReadme SUCCESS!");
		}else{
			log.debug("printContentOfHomeReadme SILENT FAIL ...");
		}
	}
	
	public static void removeDirUsrLocalBin(MyDrive drive){
		drive.removePlainFileOrEmptyDirectoryByPathname("/usr/local/bin");
		log.debug("removeDirUsrLocalBin SUCCESS!");
	}
	//Imprimir a exportacao em XML do sistema de ficheiros
	public static void Teste05(){}

	public static void removeFileHomeReadme(MyDrive drive){
		drive.removePlainFileOrEmptyDirectoryByPathname("/home/README");
		log.debug("removeFileHomeReadme SUCCESS!");
	}
	//Imprimir a listagem simples da directoria /home
	public static void listContentsHome(MyDrive drive){
		Dir home = (Dir)drive.getFileByPathname("/home");
		// TODO
	}
	
	public static void listDirContent(MyDrive drive){
		Dir slash = drive.getRootDir();
		Dir home = new Dir(drive, "home", "rwxd----", slash);
		Dir a = new Dir(drive, "a", "rwxd----", home);
		Dir b = new Dir(drive, "b", "rwxd----",home);
		drive.listDirContent("/home");
		log.debug("lisDirContent SUCCESS!");
		
	}
}
