package pt.tecnico.myDrive.domain;


import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;

public class PlainFile extends PlainFile_Base {
	static final Logger log = LogManager.getRootLogger();

	protected PlainFile(){
		super();
	}

	public PlainFile(MyDrive drive, User owner, String name, String permissions, String content){
		this.init(drive, owner, name, permissions, content);
	}

	public PlainFile(MyDrive drive, User owner, String name, String permissions, String content, Dir dir){
		this.init(drive, owner, name, permissions, content, dir);
	}

	public PlainFile(MyDrive drive, Element xml){
		this.importXML(drive, xml);
	}

	protected void init(MyDrive drive, User owner, String name, String permissions, String content){
		super.init(drive, owner, name, permissions);
		super.setContent(content);
	}

	protected void init(MyDrive drive, User owner, String name, String permissions, String content, Dir dir){
		super.init(drive, owner, name, permissions, dir);
		super.setContent(content);
	}
	
	protected void init(MyDrive drive, String id, User owner, String name, String permissions, String content, Dir dir, String lastModifiedAt){
		super.init(drive, id, owner, name, permissions, dir, lastModifiedAt);
		super.setContent(content);
	}
	

	public String readContent(String path){
		File file = getMydrive().getFileByPathname(path, false, null);
		if(file instanceof PlainFile){
			return ((PlainFile)file).getContent();			
		}else{
			throw new WrongTypeOfFileFoundException(file.getName(), "PlainFile");
		}
	}

	@Override
	public String toString(){
		return this.toString("plain");
	}

	public String toString(String type){
		String description = type;
		description += " " + super.toString();
		if(type == "link"){
			description += " ->" + this.getContent();
		}
		else{ 
			description += " " + this.getContent();
		}
		return description;
	}


	public Element exportXML() {
		Element element;		
		element = super.exportXML();
		element.setName("plain");
		Element elementContent = new Element("contents");
		elementContent.setText(this.getContent());
		element.addContent(elementContent);
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
    	if(name == null) throw new ImportDocumentException("PlainFile - name is not optional and must be supplied.");
    	
    	String _name = name;
    	maybeString = Optional.ofNullable(elm.getAttributeValue("id"));
		id = (maybeString.orElseThrow(() -> new ImportDocumentException("PlainFile <"+ _name +"> ID is not optional and must be supplied." + elm.toString())));
		
		if(path == null) throw new ImportDocumentException("PlainFile <"+ name +"> \n path is not optional and must be supplied.");
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);
		
		if(owner == null) owner = SuperUser.USERNAME;
		_owner = drive.getUserByUsername(owner);
		
		if(contents == null) throw new ImportDocumentException("PlainFile <"+ name +"> contents are not optional and must be supplied.");
		String perm = _owner.getMask();
		
		if(last_modification == null) throw new ImportDocumentException("PlainFile <"+ name +"> date of last modification is not optional and must be supplied.");
		this.init(drive, id, _owner, name, perm, contents, father, last_modification);


	}

}
