package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.CannotSetNullDriveForSuperUserException;

public class SuperUser extends SuperUser_Base {
	
    public static final String USERNAME = "root";
    private final int MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION = 10;

	public SuperUser(MyDrive drive) {
    	super.init( drive, USERNAME, "***", "Super User", "rwxdr-x-", "/home/root");
    	super.setMaxInactivityTimeOfSession(this.MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION);
    }
	
	public SuperUser(MyDrive drive, Element xml){
		super.importXML(drive, xml);
		super.setMaxInactivityTimeOfSession(this.MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION);
	}
	
	public Element exportXML() {
		Element element = super.exportXML(); 
		element.setName("superUser");
		return element;
	}
	
	public void setMyDrive(MyDrive drive){
		
	}
	
	protected void importXML(MyDrive drive, Element elm){
		super.importXML(drive, elm);
	}
	
	@Override
	public void setMydrive(MyDrive drive) {
		if (drive == null){
			throw new CannotSetNullDriveForSuperUserException();        	
		}else{
			drive.addUser(this);        	
		}
	}
}
