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
    public  boolean hasPermissionsForRead (User u){
    	return this.getLinkedFile ().hasPermissionsForRead (u);
    }
    
    @Override
    public  boolean hasPermissionsForWrite (User u){
    	return this.getLinkedFile ().hasPermissionsForWrite (u);
    }
    
    @Override
    public boolean hasPermissionsForExecute (User u){
    	return this.getLinkedFile ().hasPermissionsForExecute (u);
    }
    
    @Override
    public void setContent (String newContent){
    	PlainFile fileToChange = this.getLinkedFile();
    	fileToChange.setContent (newContent);
    }
    
    @Override
    public String getContent (){
    	PlainFile fileToRead = this.getLinkedFile();
    	return fileToRead.getContent ();
    }
    
    public PlainFile getLinkedFile (){
    	MyDrive myDrive = this.getMydrive ();
    	return (PlainFile)myDrive.getFileByPathname (super.getContent (), false, null);
    }
    
    public void execute(String[] args){
    	System.out.println(this.getContent());
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
		String name = null;
    	String path = null;
    	String owner = null;
    	String contents = null;
    	String last_modification = null;
    	String id;
    	User _owner;
    
    	for(Element element : elm.getChildren()){
    		switch(element.getName()){
    			case "name":
    				name = element.getText();
    				break;
    			case "path":
    				path = element.getText();
    				break;
    			case "owner":
    				owner = element.getText();
    				break;
    			case "contents":
    				contents = element.getText();
    				break;
    			case "last_modification":
    				last_modification = element.getText();
    				break;
    		}
    	}
    	if(name == null) throw new ImportDocumentException("Link \n name is not optional and must be supplied.");
    	
    	String _name = name;
    	maybeString = Optional.ofNullable(elm.getAttributeValue("id"));
		id = (maybeString.orElseThrow(() -> new ImportDocumentException("Link <"+ _name +"> ID is not optional and must be supplied." + elm.toString())));
		
		if(path == null) throw new ImportDocumentException("Link <"+ name +"> \n path is not optional and must be supplied.");
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);
		
		if(owner == null) owner = SuperUser.USERNAME;
		_owner = drive.getUserByUsername(owner);
		
		if(contents == null) throw new ImportDocumentException("Link <"+ name +"> contents are not optional and must be supplied.");
		String perm = _owner.getMask();
		
		if(last_modification == null) throw new ImportDocumentException("Link <"+ name +"> date of last modification is not optional and must be supplied.");
		this.init(drive, id, _owner, name, perm, contents, father, last_modification);
    }
}
