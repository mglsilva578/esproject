package pt.tecnico.myDrive.domain;

public class PlainFile extends PlainFile_Base {

	protected PlainFile(){
		super();
	}
	
	public PlainFile(int id, String name, String permissions,String content) {
		super();
		super.setId(id);
		super.setName(name);
		super.setPermissions(permissions);
		setContent(content);
	}
	
	public String readContent(){
		return getContent();
	}
	
}
