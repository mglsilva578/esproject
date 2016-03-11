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

	protected void init( MyDrive myDrive, User owner, String name, String permissions ){
		setId(myDrive.getNewFileId());
		setName( name );
		setLast_modification( new DateTime() );
		setPermissions( permissions );
		this.setMydrive( myDrive );
		this.setOwner( owner );
	}

	public File(MyDrive myDrive, User owner, String name, String permissions) {
		this.init( myDrive, owner, name, permissions );
	}
	
	@Override
    public void setMydrive(MyDrive drive) {
        if (drive == null)
            super.setMydrive(null);
        else
        	drive.addFile(this);
    }
	
	@Override
    public void setOwner(User owner) {
        if (owner == null)
            super.setOwner(null);
        else
            owner.addFile(this);
    }

	@Override
	public void setName(String n) {
		if(!( this.getId() == SLASH_DIR_1 )){
			if(n.contains("/") || n.contains("\0")) {
				throw new InvalidFileNameException(n);
			}			
		}
		super.setName(n);
	}

	@Override
	public String toString(){
		String description = "";
		description += "File with id " + this.getId() + "\n";
		description += "\twith name " + this.getName() + "\n";
		description += "\tbelonging to " + this.getOwner().getUsername() + "\n";
		description += "\tlast modified at " + this.getLast_modification() + "\n";
		description += "\twith permissions " + this.getPermissions() + "\n";
		return description;
	}

	public void remove(){
		setMydrive(null);
		setOwner(null);
		deleteDomainObject();
	}
	
	public void importXML(Element elm){
		this.setName(elm.getAttributeValue("name"));
		User user = FenixFramework.getDomainRoot().getMydrive().getUserByUsername(elm.getAttributeValue("owner"));
		this.setOwner(user);
		this.setPermissions(elm.getAttributeValue("permissions"));
		this.setPath(elm.getAttributeValue("path"));
		
	}
}
