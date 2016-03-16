package pt.tecnico.myDrive.domain;

import java.util.Optional;

import org.jdom2.Element;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.CannotModifyLinkContentException;
import pt.tecnico.myDrive.exception.ImportDocumentException;

public class Link extends Link_Base {
    
	public Link(){
		super();
	}
	
    public Link(MyDrive drive, User owner, String name, String permissions,String content, Dir dir){
        super.init(drive, owner, name, permissions, content, dir);
    }
    
    public Link(MyDrive drive, Element xml){
		this.importXML(drive, xml);
	}
    
    public String toString(){
		String description = super.toString();
		return description;
	}
    
    public Element exportXML() {
    	Element element = super.exportXML(); 
    	element.setName("link");
    	return element;
    }

    public void importXML(MyDrive drive, Element elm){
    	Optional<String> maybeString = null;

    	maybeString = Optional.ofNullable(elm.getChildText("name"));
    	String name = (maybeString.orElseThrow(() -> new ImportDocumentException("Link \n name is not optional and must be supplied.")));

    	maybeString = Optional.ofNullable(elm.getChildText("path"));
		String path = (maybeString.orElseThrow(() -> new ImportDocumentException("Link <"+ name +"> \n path is not optional and must be supplied.")));
		drive.getFileByPathname(path, true);
		Dir father = (Dir)drive.getFileByPathname(path, true);


		maybeString = Optional.ofNullable(elm.getChildText("owner"));
		String ownerName = (maybeString.orElse(SuperUser.NAME));
		User owner = drive.getUserByUsername(ownerName);

		maybeString = Optional.ofNullable(elm.getChildText("contents"));
		String contents = (maybeString.orElseThrow(() -> new ImportDocumentException("Link <"+ name +"> \n contents are not optional and must be supplied.")));
		
		String perm = owner.getMask();
		
		this.init(drive, owner, name, perm, contents, father);
    
    }
}
