package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

public class PlainFile extends PlainFile_Base {

	protected PlainFile(){
		super();
	}
	
	protected void init( MyDrive myDrive, User owner, String name, String permissions, String content ){
		super.init( myDrive, owner, name, permissions );
		this.setContent(content);
	}
	
	public PlainFile(MyDrive myDrive, User owner, int id, String name, String permissions, String content) {
		this.init( myDrive, owner, name, permissions );
	}
	
	public String readContent(){
		return getContent();
	}
	public void importXML(Element elm){
		super.importXML(elm);
		this.setContent(elm.getAttributeValue("contents"));
	}
	
}
