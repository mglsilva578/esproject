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
		setContent(content);
	}

	protected void init(MyDrive drive, User owner, String name, String permissions, String content, Dir dir){
		super.init(drive, owner, name, permissions, dir);
		setContent(content);
	}
	
	protected void init(MyDrive drive, String id, User owner, String name, String permissions, String content, Dir dir, String lastModifiedAt){
		super.init(drive, id, owner, name, permissions, dir, lastModifiedAt);
		setContent(content);
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
		element.setAttribute("contents", this.getContent());
		return element;
	}


	public void importXML(MyDrive drive, Element elm){
		Optional<String> maybeString = null;

		maybeString = Optional.ofNullable(elm.getAttributeValue("name"));
		String name = (maybeString.orElseThrow(() -> new ImportDocumentException("PlainFile - name is not optional and must be supplied.")));
		
		maybeString = Optional.ofNullable(elm.getAttributeValue("id"));
		String id = (maybeString.orElseThrow(() -> new ImportDocumentException("PlainFile <"+ name +"> ID is not optional and must be supplied." + elm.toString())));
		
		maybeString = Optional.ofNullable(elm.getAttributeValue("path"));
		String path = (maybeString.orElseThrow(() -> new ImportDocumentException("PlainFile <"+ name +"> path is not optional and must be supplied.")));
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);


		maybeString = Optional.ofNullable(elm.getAttributeValue("owner"));
		String ownerName = (maybeString.orElse(SuperUser.USERNAME));
		User owner = drive.getUserByUsername(ownerName);

		maybeString = Optional.ofNullable(elm.getAttributeValue("contents"));
		String contents = (maybeString.orElseThrow(() -> new ImportDocumentException("PlainFile <"+ name +"> contents are not optional and must be supplied.")));

		String perm = owner.getMask();

		maybeString = Optional.ofNullable(elm.getAttributeValue("last_modification"));
		String lastModifiedAt = (maybeString.orElseThrow(() -> new ImportDocumentException("PlainFile <"+ name +"> date of last modification is not optional and must be supplied.")));
		
		this.init(drive, id, owner, name, perm, contents, father, lastModifiedAt);
	}

}
