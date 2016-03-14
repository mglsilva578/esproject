package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;

public class File extends File_Base {
	private static final int SLASH_DIR_1 = 1;

	protected File(){
		super();
	}

	public File(MyDrive drive, User owner, String name, String permissions){
		this.init(drive, owner, name, permissions);
	}

	public File(MyDrive drive, User owner, String name, String permissions, Dir dir){
		this.init(drive, owner, name, permissions, dir);
	}
	
	public File(MyDrive drive, Element xml){
		this.importXML(drive, xml);
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

	@Override
	public void setName(String n){
		if(!(this.getId() == SLASH_DIR_1 )){
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
		String description = "\n";
		description += "\tid: " + this.getId() + "\n";
		description += "\tname: " + this.getName() + "\n";
		description += "\towner: " + this.getOwner().getUsername() + "\n";
		description += "\tlast modified: " + this.getLast_modification() + "\n";
		description += "\tpermissions: " + this.getPermissions() + "\n";
		description += "\tfull path: " + this.getFullyQualifiedPath() + "\n";
		return description;
	}

	public Element exportXML() {
		Element element = new Element("File");
		element.setAttribute("id", this.getId().toString());
		element.setAttribute("path", this.getFullyQualifiedPath());
		element.setAttribute("name", this.getName());
		element.setAttribute("owner", this.getOwner().getName());
		element.setAttribute("perm", this.getPermissions());
		return element;
	}

	public void importXML(MyDrive drive, Element elm){
	//	this.setName(elm.getAttributeValue("name"));
	//	User user = FenixFramework.getDomainRoot().getMydrive().getUserByUsername(elm.getAttributeValue("owner"));
	//	this.setOwner(user);
	//	this.setPermissions(elm.getAttributeValue("perm"));
	
	
	
	
		try{
			int id = elm.getAttribute("id").getIntValue();
			String path = new String(elm.getChild("path").getValue());//.getBytes("UTF-8")));
			String name = new String(elm.getChild("name").getValue());//.getBytes("UTF-8")));
			String owner = new String(elm.getChild("owner").getValue());
			User u = drive.getUserByUsername(owner);
			String perm = new String(elm.getChild("perm").getValue());//.getBytes("UTF-8")));
			init(drive, u, name, perm);
		}catch(Exception e){
			e.printStackTrace();
			throw new ImportDocumentException("In File");
		}
	}

	private String getFullyQualifiedPath(){
		if(this.getName().equals(Dir.SLASH_NAME)) return "/";
		String path = "";
		path = this.getFather().getFullyQualifiedPath();
		path = path + "/" + this.getName();
		return path;
	}

	public String getPath(){
		if (!this.getName().equals("home"))
			return getFather().getPath()+"/"+this.getName();
		return "/"+this.getName();
	}

}
