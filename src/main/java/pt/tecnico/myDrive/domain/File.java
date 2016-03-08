package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

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

}
