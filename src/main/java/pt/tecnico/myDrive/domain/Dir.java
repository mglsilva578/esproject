package pt.tecnico.myDrive.domain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.CannotFindFileException;
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
		}
	}

	public Dir(MyDrive drive, User owner, String name, String permissions, Dir dir){
		super.init(drive, owner, name, permissions, dir);
	}
	
	public Dir(MyDrive drive, String name, String permissions, Dir dir){
		User owner = drive.getUserByUsername(SuperUser.NAME);
		super.init(drive, owner, name, permissions, dir);
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

	public String listDirContent(String path){
		String array[];
		array = path.split(SLASH_NAME,2);
		String arg1 = array[0];
		String arg2 = array[1];
		if (arg2==null){
			for(File file: getFileSet())
				return file.getName();
		}
		for(File file: getFileSet()){
			if(file.getName().equals(arg1)){
				((Dir) file).listDirContent(arg2);
			}
			else{
				throw new NoDirException(path);
			}
		}
		return null;
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
		String description = super.toString();
		description += "\tsize: " + this.getFileSet().size() + "\n";
		description += "\tcontent: " + this.getContentNames() + "\n";
		return description;
	}

	public void importXML(Element elm){
		super.importXML(elm);
	}

}
