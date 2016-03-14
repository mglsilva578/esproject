package pt.tecnico.myDrive.domain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.NoDirException;

public class Dir extends Dir_Base {

	static final Logger log = LogManager.getRootLogger();
	public static final String SLASH_NAME = "/";

	public Dir(){
		super();
	}

	public Dir(MyDrive drive, User owner, String name, String permissions){
		super.init(drive, owner, name, permissions);
		if(name.equals(SLASH_NAME)){
			drive.setRootDir(this);
			this.setFather(this);
		}
	}

	public Dir(MyDrive drive, User owner, String name, String permissions, Dir dir){
		super.init(drive, owner, name, permissions, dir);
	}

	public Dir(MyDrive drive, String name, String permissions, Dir dir){
		User owner = drive.getUserByUsername(SuperUser.NAME);
		super.init(drive, owner, name, permissions, dir);
	}
	
	public Dir(MyDrive drive, Element node){
		this.importXML(drive, node);
	}

	public File getFileByName(String nameToLook){
		for (File file : this.getFileSet()){
			if(file.getName().equals(nameToLook)){
				return file;
			}
		}
		throw new NoDirException(nameToLook, this.getName());
	}

	private boolean hasFileWithName(String name){
		for (File file : this.getFileSet()){
			if(file.getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public void addFile(File fileToAdd){
		if(!hasFileWithName(fileToAdd.getName())){
			super.addFile(fileToAdd);			
		} 
		else{
			throw new FileAlreadyExistsException(fileToAdd.getName(), this);
		}
	}

	public String getContentNames(){
		String contentNames = "";
		for (File file : this.getFileSet()) {
			contentNames += file.getName() + " | ";
		}
		return contentNames;
	}

	@Override
	public String toString(){
		String description = "";
		if(!this.getName().equals(Dir.SLASH_NAME)){
			description = super.toString();
		}else{
			description = "\n";
			description += "\tid: " + this.getId() + "\n";
			description += "\tname: " + this.getName() + "\n";
			description += "\towner: " + this.getOwner().getUsername() + "\n";
			description += "\tlast modified: " + this.getLast_modification() + "\n";
			description += "\tpermissions: " + this.getPermissions() + "\n";
		}
		description += "\tsize: " + this.getFileSet().size() + "\n";
		description += "\tcontent: " + this.getContentNames() + "\n";
		return description;
	}

	public Element exportXML() {
		Element element = super.exportXML(); 
		element.setName("dir");
		return element;
	}

	public void importXML(MyDrive drive, Element elm){
		super.importXML(drive, elm);
	}

	public String getFullyQualifiedPath(){
		if(this.getName().equals(Dir.SLASH_NAME)) return "";

		Dir parent = this.getFather();
		String path = "" + this.getName();
		while(!parent.getName().equals(Dir.SLASH_NAME)){
			path = parent.getName() + "/" + path;
			parent = parent.getFather();
		}
		path = "/" + path;
		return path;
	}
}
