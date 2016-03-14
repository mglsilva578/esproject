package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

public class SuperUser extends SuperUser_Base {
	
    public static final String NAME = "root";

	public SuperUser(MyDrive drive) {
    	super.init( drive, NAME, "***", "Super User", "rwxdr-x-");
    }
	
	public Element exportXML() {
		Element element = super.exportXML(); 
		element.setName("superUser");
		return element;
	}
	
	public void importXML(MyDrive drive, Element elm){
		super.importXML(drive, elm);
	}
}
