package pt.tecnico.myDrive.domain;

import java.util.Optional;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.ImportDocumentException;

public class App extends App_Base {
    
	private static final String DEFAULT_METHOD = "main";
	public App(){
		super();
	}
	
    public App(MyDrive drive, User owner, String name, String permissions,String content, Dir dir){
        super.init(drive, owner, name, permissions, content, dir);
    }
    
    public App(MyDrive drive, Element xml){
		this.importXML(drive, xml);
	}
    
    public String toString(){
		String description = super.toString("app");
		return description;
	}
    
    public Element exportXML() {
    	Element element = super.exportXML(); 
    	element.setName("app");
    	return element;
    }

    public void importXML(MyDrive drive, Element elm){
    	Optional<String> maybeString = null;

    	maybeString = Optional.ofNullable(elm.getAttributeValue("name"));
    	String name = (maybeString.orElseThrow(() -> new ImportDocumentException("App \n name is not optional and must be supplied.")));
    	
    	maybeString = Optional.ofNullable(elm.getAttributeValue("id"));
		String id = (maybeString.orElseThrow(() -> new ImportDocumentException("APP <"+ name +"> ID is not optional and must be supplied." + elm.toString())));
		

    	maybeString = Optional.ofNullable(elm.getAttributeValue("path"));
		String path = (maybeString.orElseThrow(() -> new ImportDocumentException("App <"+ name +"> \n path is not optional and must be supplied.")));
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);


		maybeString = Optional.ofNullable(elm.getAttributeValue("owner"));
		String ownerName = (maybeString.orElse(SuperUser.NAME));
		User owner = drive.getUserByUsername(ownerName);

		maybeString = Optional.ofNullable(elm.getAttributeValue("contents"));
		String contents = (maybeString.orElse(DEFAULT_METHOD));
		
		String perm = owner.getMask();
		
		maybeString = Optional.ofNullable(elm.getAttributeValue("last_modification"));
		String lastModifiedAt = (maybeString.orElseThrow(() -> new ImportDocumentException("App <"+ name +"> date of last modification is not optional and must be supplied.")));
		
		this.init(drive, id, owner, name, perm, contents, father, lastModifiedAt);
    

    }
    
}
