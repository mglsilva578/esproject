package pt.tecnico.myDrive.domain;
import org.jdom2.Element;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.CannotFindFileException;
import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.NoDirException;

public class Dir extends Dir_Base {
	private Dir self;
	private Dir parent;
	public Dir() {
		super();
	}

	public Dir(MyDrive myDrive, User owner, String name, String permissions){
		super.init(myDrive, owner, name, permissions);
		this.self = this;
	}

	public Dir getSelf(){
		return this.self;
	}
	
	public Dir getParent() {
		return parent;
	}

	public void setParent(Dir parent) {
		this.parent = parent;
	}

	public void addFile( File fileToAdd ){
		if( fileToAdd instanceof Dir ){
			((Dir)fileToAdd).setParent( this );
		}
		if(!hasFileWithName( fileToAdd.getName() )){
			super.addFile( fileToAdd );			
		}else{
			throw new FileAlreadyExistsException(fileToAdd.getName(), this);
		}
	}
	
	private boolean hasFileWithName( String name ){
		if( this.getFileSet().size() == 0 ) return false;
		
		for (File file : this.getFileSet()) {
			if( file.getName().equals(name) ){
				return true;
			}
		}
		return false;
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
	
	
	public void importXML(Element elm){
		super.importXML(elm);
		
	}
	
	public Dir getDirByName( String name ){
		for (File file : this.getFileSet()) {
			if( file instanceof Dir){
				if( file.getName().equals(name) ){
					return (Dir)file;
				}				
			}
		}
		throw new NoDirException( name );
	}
	
	@Override
	public String toString(){
		String description = super.toString();
		description += "\n";
		description += "With parent : " + this.getParent().getName() +"\n";
		description += "Dir contents : ------------------\n";
		description += this.getFileSet();
		description += "-----------------------------------\n";
		return description;
	}
}
