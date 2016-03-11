package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.ist.fenixframework.FenixFramework;

public class App extends App_Base {
    
    public App( MyDrive myDrive, User owner, int id, String name, String permissions,String content ) {
        super.init( myDrive, owner, name, permissions );
    }
    
    public App(){}
    
    public void importXML(Element elm){
    	this.setName(elm.getAttributeValue("name"));
		User user = FenixFramework.getDomainRoot().getMydrive().getUserByUsername(elm.getAttributeValue("owner"));
		this.setOwner(user);
		this.setPermissions(elm.getAttributeValue("permissions"));
    	this.setContent(elm.getAttributeValue("method"));
    }
    
}
