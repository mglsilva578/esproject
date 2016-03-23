package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidIdException;

public class File extends File_Base {
	private static final int SLASH_DIR_0 = 0;
	private static final String PATH_SEPARATOR = "/";
	
	protected File(){
		super();
	}

	public File(MyDrive drive, User owner, String name, String permissions){
		this.init(drive, owner, name, permissions);
	}

	public File(MyDrive drive, User owner, String name, String permissions, Dir dir){
		this.init(drive, owner, name, permissions, dir);
	}

	protected void init(MyDrive drive, User owner, String name, String permissions){
		setId(drive.getNewFileId());
		setName(name);
		setLast_modification(new DateTime());
		setPermissions(permissions);
		this.setMydrive(drive);
		this.setOwner(owner);
	}

	protected void init(MyDrive drive, User owner, String name, String permissions, Dir dir){
		setId(drive.getNewFileId());
		setName(name);
		setLast_modification(new DateTime());
		setPermissions(permissions);
		this.setMydrive(drive);
		this.setOwner(owner);
		dir.addFile(this);
	}
	
	protected void init(MyDrive drive, String id,  User owner, String name, String permissions, Dir dir){
		int parsedId;
		try{
			parsedId = Integer.parseInt(id);
			setId(parsedId);
			setName(name);
			setLast_modification(new DateTime());
			setPermissions(permissions);
			this.setMydrive(drive);
			this.setOwner(owner);
			dir.addFile(this);
		}catch(NumberFormatException nfe){
			throw new InvalidIdException(id);
		}
	}

	@Override
	public void setName(String n){
		if(!(this.getId() == SLASH_DIR_0 )){
			if(n.contains("/") || n.contains("\0")){
				throw new InvalidFileNameException(n);
			}
		}
		super.setName(n);
	}

	@Override
	public void setMydrive(MyDrive drive){
		if (drive == null){
			super.setMydrive(null);
		}
		else{
			drive.addFile(this);
		}
	}

	@Override
	public void setOwner(User owner){
		if (owner == null){
			super.setOwner(null);
		}
		else{
			owner.addFile(this);
		}
	}

	public void remove(){
		setMydrive(null);
		setOwner(null);
		deleteDomainObject();
	}

	@Override
	public String toString(){
		String description = this.getPermissions();
		//To Do size
		description += " " + this.getOwner().getUsername();
		description += " " + this.getId();
		description += " " + this.getLast_modification();
		description += " " + this.getName();
		return description;
	}

	public Element exportXML() {
		Element element = new Element("File");
		element.setAttribute("id", this.getId().toString());
		element.setAttribute("path", this.getPath());
		element.setAttribute("name", this.getName());
		element.setAttribute("owner", this.getOwner().getUsername());
		element.setAttribute("perm", this.getPermissions());
		return element;
	}

	public String getPath(){
		if(this.getName().equals(Dir.SLASH_NAME)) return "";

		Dir parent = this.getFather();
		String path;
		if(directlyUnderSlashDir(parent)){
			path = PATH_SEPARATOR;
			return path;
		}else{
			path = "";
		}
		
		while(!directlyUnderSlashDir(parent)){
			path = PATH_SEPARATOR + parent.getName() + path;
			parent = parent.getFather();
		}
		return path;
	}

	private boolean directlyUnderSlashDir(Dir parent) {
		return parent.getName().equals(Dir.SLASH_NAME);
	}
}
