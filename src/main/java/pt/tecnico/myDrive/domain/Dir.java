package pt.tecnico.myDrive.domain;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.NoDirException;
import pt.tecnico.myDrive.util.SetOrderingHelper;

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
		for (File file : SetOrderingHelper.sortFileSetById(getFileSet())) {
			contentNames += file.getName() + CONTENT_SEPARATOR;
		}
		return contentNames;
	}

	public void removeFile2(String fileName){

		// encontrar ficheiro
		// se nao for uma directoria, apagar
		// caso seja uma dir, apagar recursivamente os conteudos
		File fileToDelete = this.getFileByName(fileName);
		Dir dirToDelete = null;
		if(fileToDelete instanceof Dir){
			dirToDelete = (Dir)fileToDelete;
			//fileToDelete.remove();
			removeDir(dirToDelete);
		}else{
			log.trace("Trying to remove file <" + fileName + ">");
			//fileToDelete.remove();

			this.removeFile(fileToDelete);
			log.trace("Removed file <" + fileName + ">");
		}
	}

	public void deleteFile(String fileName){
		// encontrar ficheiro
		// se nao for uma directoria, apagar
		// caso seja uma dir, apagar recursivamente os conteudos
		File fileToDelete = this.getFileByName(fileName);
		Dir dirToDelete = null;
		if(fileToDelete instanceof Dir){
			dirToDelete = (Dir)fileToDelete;
			//fileToDelete.remove();
			removeDir(dirToDelete);
		}else{
			log.trace("Trying to remove file <" + fileName + ">");
			//fileToDelete.remove();
			this.removeFile(fileToDelete);
			fileToDelete.remove();
			log.trace("Removed file <" + fileName + ">");
		}
	}

	private void removeDir(Dir dirToDelete) {
		log.trace("Dir - Trying to remove a dir with name <"+ dirToDelete.getName() +">");
		File fileToDelete = null;
		
		if(!isEmpty(dirToDelete)){
			CopyOnWriteArrayList<File> files = new CopyOnWriteArrayList<File>(dirToDelete.getFileSet());
			Iterator<File> it = files.iterator();
			while(it.hasNext()){
				fileToDelete = it.next();
				log.trace("Dir - Trying to delete a file with name <" + fileToDelete.getName() + ">");
				dirToDelete.removeFile(fileToDelete);
				fileToDelete.remove();
				log.trace("Dir - success!");
			}
		}
		
		log.trace("Dir - dir <"+ dirToDelete.getName() +"> is now empty and will be deleted.");
		this.removeFile(dirToDelete);
		dirToDelete.remove();
		log.trace("Dir - Removed dir");
	}

	private boolean isEmpty(Dir dirToDelete) {
		return dirToDelete.getFileSet().size() == 0;
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

		maybeString = Optional.ofNullable(elm.getAttributeValue("name"));
		String name = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir - name is not optional and must be supplied.")));

		maybeString = Optional.ofNullable(elm.getAttributeValue("id"));
		String id = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir <"+ name +"> ID is not optional and must be supplied." + elm.toString())));

		maybeString = Optional.ofNullable(elm.getAttributeValue("path"));
		String path = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir <"+ name +"> path is not optional and must be supplied." + elm.toString())));
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);

		maybeString = Optional.ofNullable(elm.getAttributeValue("owner"));
		String ownerName = (maybeString.orElse(SuperUser.NAME));
		User owner = drive.getUserByUsername(ownerName);

		maybeString = Optional.ofNullable(elm.getAttributeValue("perm"));
		String perm = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir <"+ name +"> permission is not optional and must be supplied.")));

		maybeString = Optional.ofNullable(elm.getAttributeValue("last_modification"));
		String lastModifiedAt = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir <"+ name +"> date of last modification is not optional and must be supplied.")));

		super.init(drive, id, owner, name, perm, father, lastModifiedAt);
	}
}
