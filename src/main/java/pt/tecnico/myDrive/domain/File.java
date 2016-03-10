package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;
import pt.tecnico.myDrive.exception.InvalidFileNameException;

public class File extends File_Base {

	protected File(){
		super();
	}

	public File(MyDrive myDrive, User owner, String name, String permissions) {
		setOwner(owner);
		setName(name);
		setId(myDrive.getNewFileId());
		setLast_modification(new DateTime());
		setPermissions(permissions);
	}
	
	@Override
    public void setName(String n) {
		if(n.contains("/") || n.contains("\0")) {
            throw new InvalidFileNameException(n);
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

}
