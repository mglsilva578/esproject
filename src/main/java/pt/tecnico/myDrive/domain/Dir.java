package pt.tecnico.myDrive.domain;
import org.jdom2.Element;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.CannotFindFileException;
import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.NoDirException;

public class Dir extends Dir_Base {

	public static final String SLASH_NAME = "/";
	private Dir self;
	private Dir parent;

	public Dir(){
		super();
	}

	/**
	 * Creates a  Dir into MyDrive, associated to a User.
	 * A dir's parent is not set upon creation. Instead, it is set whenever
	 * a dir is added to another. Special attention is given to when the dir 
	 * in question is the root dir, in which case its parent is immediatly set
	 * as itself.
	 * 
	 * @param drive instancia da MyDrive a qual nos estamos a adicionar.
	 * @param owner User who is associated to this Dir.
	 * @param name name by which this dir is known to MyDrive.
	 * @param permissions permissions associated to this Dir.
	 */
	public Dir(MyDrive drive, User owner, String name, String permissions){
		super.init(drive, owner, name, permissions);
		this.setSelf(this);
		if(name.equals(SLASH_NAME)){
			this.setParent(this);
		}
	}


	public Dir getSelf(){
		return this.self;
	}

	public Dir getParent(){
		return this.parent;
	}

	public Dir getDirByName(String nameToLook){
		for (File file : this.getFileSet()){
			if(file instanceof Dir){
				if(file.getName().equals(nameToLook)){
					return (Dir)file;
				}				
			}
		}
		throw new NoDirException(nameToLook, this.getName());
	}

	public void setSelf(Dir self){
		this.self = self;
	}

	public void setParent(Dir parent) {
		this.parent = parent;
	}

	private boolean hasFileWithName(String name){
		if(this.getFileSet().size() == 0){
			return false;
		}
		for (File file : this.getFileSet()){
			if(file.getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public void addFile(File fileToAdd){
		if(fileToAdd.equals(this)){
			super.addFile(fileToAdd);
		}else{
			if (canAddFile(fileToAdd)){
				if(fileToAdd instanceof Dir){
					if(!((Dir)fileToAdd).equals(this.parent))
						((Dir)fileToAdd).setParent(this);
				}
				super.addFile(fileToAdd);			
			} else {
				throw new FileAlreadyExistsException(fileToAdd.getName(), this);
			}
		}
	}

	private boolean canAddFile(File fileToAdd) {
		return !hasFileWithName(fileToAdd.getName());
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
		contentNames += ".[" + this.self.getName() + "] | ";
		contentNames += "..[" + this.parent.getName() + "] | ";
		for (File file : this.getFileSet()) {
			contentNames += file.getName() + " | ";
		}
		return contentNames;
	}
	

	@Override
	public String toString(){
		String description = super.toString();
		description += "\t With parent : " + this.getParent().getName() +"\n";
		description += "\t Dir size : " + this.getFileSet().size() + "\n";
		description += "\t Dir content names : " + this.getContentNames() + "\n";
		return description;
	}

	public void importXML(Element elm){
		super.importXML(elm);
	}

}
