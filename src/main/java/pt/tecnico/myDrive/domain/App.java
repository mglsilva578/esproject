package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.UsernameDoesNotExistException;

public class App extends App_Base {
    
	public App(){
		super();
	}
	
    public App(MyDrive drive, User owner, String name, String permissions,String content, Dir dir){
        super.init(drive, owner, name, permissions, content, dir);
        dir.addFile(this);
    }
    
    public String toString(){
		String description = super.toString();
		return description;
	}
    
    public void importXML(Element elm){
    	this.setName(elm.getAttributeValue("name"));
		User user = FenixFramework.getDomainRoot().getMydrive().getUserByUsername(elm.getAttributeValue("owner"));
		this.setOwner(user);
		this.setPermissions(elm.getAttributeValue("permissions"));
    	this.setContent(elm.getAttributeValue("method"));
    	//this.setPath(elm.getAttributeValue("path"));
    }
    
}
