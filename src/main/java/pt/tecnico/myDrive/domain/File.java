package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;
import pt.tecnico.myDrive.exception.InvalidFileNameException;

public class File extends File_Base {

	protected File(){
		super();
	}

	public File(int id,String name, String permissions) {
		setId(id);
		setName(name);
		DateTime creationdate= new DateTime();
		setLast_modification(creationdate);
		setPermissions(permissions);
	}
	
	@Override
    public void setName(String n) {
		if(n.contains("/") || n.contains("\0")) {
            throw new InvalidFileNameException(n);
        }
        super.setName(n);
    }

}
