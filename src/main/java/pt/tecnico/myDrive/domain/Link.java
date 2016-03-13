package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.UsernameDoesNotExistException;

public class Link extends Link_Base {
    
	public Link(){
		super();
	}
	
    public Link(MyDrive drive, User owner, String name, String permissions,String content, Dir dir){
        super.init(drive, owner, name, permissions, content, dir);
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
    	this.setContent(elm.getAttributeValue("value"));
    	//this.setPath(elm.getAttributeValue("path"));
    }
}
