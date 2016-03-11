package pt.tecnico.myDrive.domain;

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
	
}
