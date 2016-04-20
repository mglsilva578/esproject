package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.CannotReadPasswordException;
import pt.tecnico.myDrive.exception.CannotSetNullDriveForSuperUserException;
import pt.tecnico.myDrive.exception.CannotWritePasswordException;

public class Nobody extends Nobody_Base {
    
	public static final String USERNAME = "nobody";
	public static final String NAME = "Guest";

	public Nobody() {
        super();
    }

   	public Nobody(MyDrive drive) {
       	super.init(drive, USERNAME, null, NAME, "rxwdr-x-", null);
       }
   	
   	public Nobody(MyDrive drive, Element xml){
   		super.importXML(drive, xml);
   	}
   	
   	public Element exportXML() {
   		Element element = super.exportXML(); 
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
    
}
