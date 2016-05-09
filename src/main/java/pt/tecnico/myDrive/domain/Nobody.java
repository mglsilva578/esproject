package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.CannotReadPasswordException;
import pt.tecnico.myDrive.exception.CannotSetNullDriveForSuperUserException;
import pt.tecnico.myDrive.exception.CannotWritePasswordException;

public class Nobody extends Nobody_Base {
    
	public static final String USERNAME = "nobody";
	public static final String HOME_DIR = "nobody";
	public static final String NAME = "Guest";
	private final int MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION = Integer.MAX_VALUE;

   	public Nobody(MyDrive drive) {
       	super.init(drive, USERNAME, null, NAME, "rxwdr-x-", "/home/nobody");
       	super.setMaxInactivityTimeOfSession(this.MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION);
       }
   	
   	public Nobody(MyDrive drive, Element xml){
   		super.importXML(drive, xml);
   	}
   	
   	public Element exportXML() {
   		Element element = new Element("user");
		element.setAttribute("username", getUsername());
		Element elementPassword = new Element("password");
		elementPassword.setText("N/A");
		element.addContent(elementPassword);
		Element elementName = new Element("name");
		elementName.setText(this.getName());
		element.addContent(elementName);
		Element elementHomeDir = new Element("homeDir");
		elementHomeDir.setText("/home/" + this.getUsername());
		element.addContent(elementHomeDir);
		Element elementMask = new Element("mask");
		elementMask.setText(this.getMask());
		element.addContent(elementMask);
   		
   		element.setName(NAME);
   		return element;
   	}
   	
   	@Override
   	public String getPassword(){
   		throw new CannotReadPasswordException();
   	}
   	
   	@Override
   	public void setPassword(String password){
   		throw new CannotWritePasswordException();
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
   		}
   		else{
   			drive.addUser(this);        	
   		}
   	}
   	
   	@Override
	public String toString(){
		String description = "\n";
		description += "\tusername: " + this.getUsername() + "\n";
		description += "\tpassword: N/A" + "\n";
		description += "\tname: " + this.getName() + "\n";
		description += "\thomeDir: " + this.getHomeDir() + "\n";
		description += "\tmask: " + this.getMask() + "\n";
		return description;
	}
}
