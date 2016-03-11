package pt.tecnico.myDrive.domain;

public class SuperUser extends SuperUser_Base {
	
    public SuperUser(MyDrive drive) {
    	super.init( drive, "root", "***", "Super User", "rwxdr-x-");
    }
}
