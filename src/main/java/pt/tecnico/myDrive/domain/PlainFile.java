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

	public String readContent(String path){
		File file = getMydrive().getFileByPathname(path, false);
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
		element.setName("plain");
		element.setAttribute("contents", this.getContent());
		return element;
	}

	public void importXML(MyDrive drive, Element elm){
		super.importXML(drive, elm);
		String Content = new String(elm.getChild("contents").getValue());//.getBytes("UTF-8")));
	}

}
