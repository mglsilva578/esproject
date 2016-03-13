package pt.tecnico.myDrive;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.SuperUser;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;

public class Main {
	static final Logger log = LogManager.getRootLogger();
	// FenixFramework will try automatic initialization when first accessed
	public static void main(String [] args){
		System.out.println("Welcome to MyDrive Application");
		try{
			Main.setup();
		}catch(MyDriveException mde){
			System.out.println(mde.getMessage());
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
			// ensure an orderly shutdown
			FenixFramework.shutdown();
		}
	}

	@Atomic
	public static void init(){
		//TODO clean MyDrive
	}

	@Atomic
	public static void setup(){
		try{
			MyDrive drive = MyDrive.getInstance();
			if(drive.isEmpty()){
				SuperUser rootUser = new SuperUser(drive);

				Dir slash = new Dir(drive, rootUser, Dir.SLASH_NAME, rootUser.getMask());
				Dir home = new Dir(drive, rootUser, "home", rootUser.getMask(), slash);
				Dir root = new Dir(drive, rootUser, "root", rootUser.getMask(), home);
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
				log.info("\nread content of PlainFile\t" + content);
				
				Link link = new Link(drive, userToAdd, "link1", userToAdd.getMask(),"/home/home3/plainfile1", home4);
				content = link.readContent("/home3/home4/link1");
				log.info("\nread content of Link\t" + content);
				
				App app = new App(drive, userToAdd, "app1", userToAdd.getMask(),"package.class.method", home5);
				content = app.readContent("/home3/home4/home5/app1");
				log.info("\nread content of App\t" + content);

				String pathname = "/home/joseluisvf";
				try{
					System.out.println("\n Vamos tentar com o pathname <" + pathname + ">");
					File resultado = drive.getFileByPathname(pathname);
					System.out.println("Resultado : ");
					System.out.println(resultado);
					
					pathname = "/home/root";
					System.out.println("\n Vamos tentar com o pathname <" + pathname + ">");
					resultado = drive.getFileByPathname(pathname);
					System.out.println("Resultado : ");
					System.out.println(resultado);
					
					pathname = "/";
					System.out.println("\n Vamos tentar com o pathname <" + pathname + ">");
					resultado = drive.getFileByPathname(pathname);
					System.out.println("Resultado : ");
					System.out.println(resultado);
					
					pathname = "/home3/plainfile1";
					System.out.println("\n Vamos tentar com o pathname <" + pathname + ">");
					resultado = drive.getFileByPathname(pathname);
					System.out.println("Resultado : ");
					System.out.println(resultado);
					
				}catch( MyDriveException mde){
					System.out.println("Nossa : ");
					System.out.println(mde.getMessage());
				}catch( Exception e){
					System.out.println("Generica :");
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
				//String a = app.getPath(drive, "");
				//System.out.println("\n" + a + "\n\n");
				//System.out.println("Users da drive : \n" + drive.getUserSet() + "----------------------");
				//System.out.println("Directorios da drive : \n" + drive.getFileSet().toString());
			}
			else{
				System.out.println("\n\n MyDrive is not empty! \n\n");
				return;
			}
		}catch(Exception e){
			log.error("\n---<MAIN>--- - " + e.getClass().toString()+"\n");
			e.printStackTrace();
			log.error("\n---</MAIN>---\n");
		}
	}

	public static void applicationCodeGoesHere(){
		someTransaction();
	}

	@Atomic
	public static void someTransaction(){
		System.out.println("FenixFramework's root object is: " + FenixFramework.getDomainRoot());
	}

	@Atomic
	public static void xmlScan(Document file){
		MyDrive md = FenixFramework.getDomainRoot().getMydrive().getInstance();
		md.importXML(file.getRootElement());
	}

}
