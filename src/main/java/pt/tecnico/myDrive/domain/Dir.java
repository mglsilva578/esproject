package pt.tecnico.myDrive.domain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.NoDirException;

public class Dir extends Dir_Base {

	private static final String CONTENT_SEPARATOR = " | ";
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

	public Dir(MyDrive drive, Element node){
		this.importXML(drive, node);
	}

	public File getFileByName(String nameToLook){
		if(nameToLook.equals("."))
			return this;
		if(nameToLook.equals(".."))
			return this.getFather();

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

	public int getSize(){
		return this.getFileSet().size() + 2;
	}


	public String getContentNames(){
		String contentNames = "";
		contentNames += " . " + CONTENT_SEPARATOR +" .. " + CONTENT_SEPARATOR;
		for (File file : this.sortFileSetById(getFileSet())) {
			contentNames += file.getName() + CONTENT_SEPARATOR;
		}
		return contentNames;
	}
	
	private ArrayList<File> sortFileSetById(Set<File> toSort){
		ArrayList<File> sorted = new ArrayList<File>(toSort);
		Collections.sort(sorted, (file1, file2) -> file1.getId().compareTo(file2.getId()) );
		return sorted;
	}

	@Override
	public String toString(){
		String description = "dir";
		description += " " + this.getPermissions();
		description += " " + this.getSize();
		description += " " + this.getOwner().getUsername();
		description += " " + this.getId();
		description += " " + this.getLast_modification();
		description += " " + this.getName();
		description += " " + this.getPath();
		description += "\n\tcontent: " + this.getContentNames() + "\n";
		return description;
	}

	public Element exportXML() {
		Element element = super.exportXML(); 
		element.setName("dir");
		return element;
	}

	public void importXML(MyDrive drive, Element elm){
		Optional<String> maybeString = null;
		
		maybeString = Optional.ofNullable(elm.getAttributeValue("id"));
		String id = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir - ID is not optional and must be supplied." + elm.toString())));
		
		maybeString = Optional.ofNullable(elm.getAttributeValue("path"));
		String path = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir - path is not optional and must be supplied." + elm.toString())));
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);

		maybeString = Optional.ofNullable(elm.getAttributeValue("name"));
		String name = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir - name is not optional and must be supplied.")));

		maybeString = Optional.ofNullable(elm.getAttributeValue("owner"));
		String ownerName = (maybeString.orElse(SuperUser.NAME));
		User owner = drive.getUserByUsername(ownerName);
		maybeString = Optional.ofNullable(elm.getAttributeValue("perm"));
		String perm = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir - permission is not optional and must be supplied.")));
		super.init(drive, id, owner, name, perm, father);
	}
}
