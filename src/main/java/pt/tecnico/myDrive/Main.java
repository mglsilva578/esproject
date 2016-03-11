package pt.tecnico.myDrive;

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
			SuperUser superUser = new SuperUser(drive);

			User userToAdd = new User(drive, "zttr", "76534", "Diogo", "rwxdr-x-");
			userToAdd = new User(drive, "mglsilva578", "68230", "Miguel", "rwxdr-x-");
			userToAdd = new User(drive, "R3Moura", "74005", "Ricardo", "rwxdr-x-");
			userToAdd = new User(drive, "joseluisvf", "55816", "JoseLuis", "rwxdr-x-");
			userToAdd = new User(drive, "ist176544", "76544", "Daniel", "rwxdr-x-");
			
			Dir rootDir = new Dir(drive, superUser, "rootDir", "rwxdr-x-");
			rootDir.addFile(new File(drive, drive.getUserByUsername("joseluisvf"), "coiso.txt", "rwxdr-x-"));
			rootDir.addFile(new File(drive, drive.getUserByUsername("joseluisvf"), "coiso_the_sequel.txt", "rwxdr-x-"));
			drive.setRootDir(rootDir);
			
			System.out.println("Conteudos da drive : \n" + drive.getUserSet() + "----------------------");
		}else{
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
	
}
