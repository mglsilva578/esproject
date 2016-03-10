package pt.tecnico.myDrive.domain;
import pt.tecnico.myDrive.exception.NoDirException;

public class Dir extends Dir_Base {

	public Dir() {
		super();
	}

	public Dir(User owner, String name, String permissions){
		super();
		super.setOwner(owner);
		super.setName(name);
		super.setPermissions(permissions);
	}
	
	public String listDirContent(String path){
		String array[];
		array = path.split("/",2);
		String arg1 = array[0];
		String arg2 = array[1];
		if (arg2==null){
			for(File file: getFileSet())
				return file.getName();
		}
		for(File file: getFileSet()){
			if(file.getName().equals(arg1))
				((Dir) file).listDirContent(arg2);
			else
				throw new NoDirException(path);
		}
		return null;
	}
	
	@Override
	public String toString(){
		String description = super.toString();
		description += "\n";
		description += "Dir contents : \n";
		description += this.getFileSet();
		return description;
	}
}
