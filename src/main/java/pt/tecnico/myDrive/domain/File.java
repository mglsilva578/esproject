package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;
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

	public String getPath(MyDrive drive, String p){
		String path = null;
		if(!(this.getFather().equals(drive.getRootDir()))){
			path = this.getName() + p; 
			this.getFather().getPath(drive, path);
			return null;
		}
		else{
			return path;
		}
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
		return description;
	}

	public void importXML(Element elm){
		this.setName(elm.getAttributeValue("name"));
		User user = FenixFramework.getDomainRoot().getMydrive().getUserByUsername(elm.getAttributeValue("owner"));
		this.setOwner(user);
		this.setPermissions(elm.getAttributeValue("permissions"));
	}

}
