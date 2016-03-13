package pt.tecnico.myDrive.domain;

public class SuperUser extends SuperUser_Base {
	
    public static final String NAME = "root";

	public SuperUser(MyDrive drive) {
    	super.init( drive, NAME, "***", "Super User", "rwxdr-x-");
    }
}
