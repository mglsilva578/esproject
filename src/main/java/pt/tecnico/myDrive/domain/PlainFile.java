package pt.tecnico.myDrive.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

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
		//dir.addFile(this);
	}

	protected void init(MyDrive drive, User owner, String name, String permissions, String content){
		super.init(drive, owner, name, permissions);
		setContent(content);
	}

	protected void init(MyDrive drive, User owner, String name, String permissions, String content, Dir dir){
		super.init(drive, owner, name, permissions, dir);
		setContent(content);
	}

	public String readContent(String path){
		File file = getMydrive().getFileByPathname(path);
		if(file instanceof PlainFile){
			return ((PlainFile)file).getContent();			
		}else{
			throw new WrongTypeOfFileFoundException(file.getName(), "PlainFile");
		}
	}

	public String toString(){
		String description = super.toString();
		description += "\tcontent: " + this.getContent() + "\n";
		return description;
	}


	public Element exportXML() {
		Element element;
		element = super.exportXML();
		element.setName("PlainFile");
		element.setAttribute("content", this.getContent());
		return element;
	}

	public void importXML(Element elm){
		super.importXML(elm);
		this.setContent(elm.getAttributeValue("contents"));
	}

}
