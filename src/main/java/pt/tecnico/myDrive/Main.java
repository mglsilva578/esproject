package pt.tecnico.myDrive;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.SuperUser;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;

public class Main {
	// FenixFramework will try automatic initialization when first accessed
	public static void main(String [] args) {
		System.out.println("Welcome to MyDrive Application");
		try {
			Main.setup();
		}catch(MyDriveException mde){
			System.out.println(mde.getMessage());
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally {
			// ensure an orderly shutdown
			FenixFramework.shutdown();
		}
	}
	@Atomic
	public static void init(){
		//TODO clean MyDrive
	}

	@Atomic
	public static void setup() {
		MyDrive drive = MyDrive.getInstance();
		if(drive.isEmpty()){
			SuperUser rootUser = new SuperUser(drive);
			Dir slash = new Dir(drive, rootUser, "/", rootUser.getMask());
			slash.setParent(slash);	
			slash.setPath("/");
			drive.setRootDir(slash);
			
			Dir home = new Dir(drive, rootUser, "home", rootUser.getMask());
			slash.addFile( home );
			Dir rootDir = drive.createUserDir(rootUser);
			rootUser.setHomeDir(rootDir.getPath());
			/*
			User userToAdd = new User(drive, "zttr", "76534", "Diogo", "rwxd----");
			userToAdd = new User(drive, "mglsilva578", "68230", "Miguel", "rwxd----");
			userToAdd = new User(drive, "R3Moura", "74005", "Ricardo", "rwxd----");
			userToAdd = new User(drive, "joseluisvf", "55816", "JoseLuis", "rwxd----");
			userToAdd = new User(drive, "ist176544", "76544", "Daniel", "rwxd----");
			*/
			/*
			Dir rootDir = new Dir(drive, rootUser, "rootDir", "rwxdr-x-");
			rootDir.addFile(new File(drive, drive.getUserByUsername("joseluisvf"), "coiso.txt", "rwxdr-x-"));
			rootDir.addFile(new File(drive, drive.getUserByUsername("joseluisvf"), "coiso_the_sequel.txt", "rwxdr-x-"));
			*/
			
			System.out.println("\n\n\n");
			System.out.println("Users da drive : \n" + drive.getUserSet() + "----------------------");
			System.out.println("Directorios da drive : \n" + drive.getFileSet().toString());
		}else{
			System.out.println("\n\n MyDrive is not empty! \n\n");
			return;
		}
	}


	public static void applicationCodeGoesHere() {
		someTransaction();
	}

	@Atomic
	public static void someTransaction() {
		System.out.println("FenixFramework's root object is: " + FenixFramework.getDomainRoot());
	}
	@Atomic
	public static void xmlScan(Document file){
		MyDrive md = FenixFramework.getDomainRoot().getMydrive().getInstance();
		md.importXML(file.getRootElement());
			
		
	}
}
