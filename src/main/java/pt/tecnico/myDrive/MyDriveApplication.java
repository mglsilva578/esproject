package pt.tecnico.myDrive;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.SuperUser;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;

public class MyDriveApplication {
    // FenixFramework will try automatic initialization when first accessed
    public static void main(String [] args) {
    	System.out.println("Welcome to MyDrive Application");
        try {
            MyDriveApplication.setup();
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
			drive.addUser(new SuperUser());
			drive.addUser(new User("zttr", "76534", "Diogo", "rwxdr-x-"));
			drive.addUser(new User("mglsilva578", "68230", "Miguel", "rwxdr-x-"));
			drive.addUser(new User("R3Moura", "74005", "Ricardo", "rwxdr-x-"));
			drive.addUser(new User("joseluisvf", "55816", "JoseLuis", "rwxdr-x-"));
			drive.addUser(new User("dddd", "11111", "Daniel", "rwxdr-x-"));
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
