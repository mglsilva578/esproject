package pt.tecnico.myDrive.domain;

import java.util.Optional;

import org.jdom2.Element;

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
    
    @Override
    public void setContent (String newContent){
    	MyDrive myDrive = this.getMydrive ();
    	System.out.println("\n\nLINK - Attempting to change contents of a file supposedly in <" + super.getContent() + ">\n\n");
    	PlainFile fileToChange = (PlainFile)myDrive.getFileByPathname (super.getContent (), false, null);
    	fileToChange.setContent (newContent);
    }
    
    @Override
    public String getContent (){
    	MyDrive myDrive = this.getMydrive ();
    	PlainFile fileToRead = (PlainFile)myDrive.getFileByPathname (super.getContent (), false, null);
    	return fileToRead.getContent ();
    }
    
    public String toString(){
		String description = super.toString("link");
		return description;
	}
    
    public Element exportXML() {
    	Element element = super.exportXML(); 
    	element.setName("link");
    	return element;
    }

    public void importXML(MyDrive drive, Element elm){
    	Optional<String> maybeString = null;

    	maybeString = Optional.ofNullable(elm.getAttributeValue("name"));
    	String name = (maybeString.orElseThrow(() -> new ImportDocumentException("Link \n name is not optional and must be supplied.")));
    	
    	maybeString = Optional.ofNullable(elm.getAttributeValue("id"));
		String id = (maybeString.orElseThrow(() -> new ImportDocumentException("Link <"+ name +"> ID is not optional and must be supplied." + elm.toString())));
    	

    	maybeString = Optional.ofNullable(elm.getAttributeValue("path"));
		String path = (maybeString.orElseThrow(() -> new ImportDocumentException("Link <"+ name +"> \n path is not optional and must be supplied.")));
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);

		maybeString = Optional.ofNullable(elm.getAttributeValue("owner"));
		String ownerName = (maybeString.orElse(SuperUser.USERNAME));
		User owner = drive.getUserByUsername(ownerName);

		maybeString = Optional.ofNullable(elm.getAttributeValue("contents"));
		String contents = (maybeString.orElseThrow(() -> new ImportDocumentException("Link <"+ name +"> \n contents are not optional and must be supplied.")));
		
		String perm = owner.getMask();
		
		maybeString = Optional.ofNullable(elm.getAttributeValue("last_modification"));
		String lastModifiedAt = (maybeString.orElseThrow(() -> new ImportDocumentException("Link <"+ name +"> date of last modification is not optional and must be supplied.")));
		
		this.init(drive, id, owner, name, perm, contents, father, lastModifiedAt);
    }
}
