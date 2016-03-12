package pt.tecnico.myDrive.domain;
import org.jdom2.Element;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.CannotFindFileException;
import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.NoDirException;

public class Dir extends Dir_Base {

	private Dir self;
	private Dir parent;

	public Dir(){
		super();
	}

	public Dir(MyDrive drive, User owner, String name, String permissions){
		super.init(drive, owner, name, permissions);
		if(!(this.getName().equals(".")) && !(this.getName().equals(".."))){ 
			self = new Dir(drive, owner, ".", permissions); 
			this.addFile(self); 
		}  
		if(name.equals("/")){  
			if(!(this.getName().equals(".")) && !(this.getName().equals(".."))){ 
				parent = new Dir(drive, owner, "..", permissions);
				this.addFile(parent); 
			}  
		}  
		else { 
			if(!(this.getName().equals(".")) && !(this.getName().equals(".."))){ 
				parent = new Dir(drive, owner, "..", permissions); 
				this.addFile(parent); 
			} 
		}  
	}

	public Dir getSelf(){
		return this.self;
	}

	public Dir getParent(){
		return this.parent;
	}

	public Dir getDirByName(String name){
		for (File file : this.getFileSet()){
			if(file instanceof Dir){
				if(file.getName().equals(name)){
					return (Dir)file;
				}				
			}
		}
		throw new NoDirException(name);
	}

	public void setParent(Dir parent) {
		self.setName("..");
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
		if(!hasFileWithName(fileToAdd.getName())){
			super.addFile(fileToAdd);			
			if(fileToAdd instanceof Dir){
			//	((Dir)fileToAdd).setParent(this);
			}
			if(this.getName().equals("/")){
				//	fileToAdd.setPath(this.getPath() +  fileToAdd.getName());
			}
			else{
				//	fileToAdd.setPath(this.getPath() + "/" + fileToAdd.getName());
			}
		}
		else{
			throw new FileAlreadyExistsException(fileToAdd.getName(), this);
		}
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
			if(file.getName().equals(arg1)){
				((Dir) file).listDirContent(arg2);
			}
			else{
				throw new NoDirException(path);
			}
		}
		return null;
	}

	@Override
	public String toString(){
		String description = super.toString();
		description += "\t With parent : " + this.getParent().getName() +"\n";
		description += "\t Dir size : " + this.getFileSet().size() + "\n\n";
		return description;
	}

	public void importXML(Element elm){
		super.importXML(elm);
	}

}
