package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.CannotSetNullDriveForSuperUserException;

public class SuperUser extends SuperUser_Base {
	
    public static final String USERNAME = "root";

	public SuperUser(MyDrive drive) {
    	super.init( drive, USERNAME, "***", "Super User", "rwxdr-x-", "/home/root");
    }
	
	public SuperUser(MyDrive drive, Element xml){
		super.importXML(drive, xml);
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
