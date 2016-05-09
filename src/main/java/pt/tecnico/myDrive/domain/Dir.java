package pt.tecnico.myDrive.domain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.CannotDeleteDirInUseBySessionException;
import pt.tecnico.myDrive.exception.CannotDeleteDotOrDotDotException;
import pt.tecnico.myDrive.exception.CannotDeleteSlashDirException;
import pt.tecnico.myDrive.exception.CannotExecuteDirectoryException;
import pt.tecnico.myDrive.exception.ExceedsLimitPathException;
import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.FileDoesNotExistException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.NoDirException;
import pt.tecnico.myDrive.exception.NoPlainFileException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TypeDoesNotExistException;
import pt.tecnico.myDrive.exception.WrongContentException;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;
import pt.tecnico.myDrive.service.dto.FileDto;
import pt.tecnico.myDrive.util.SetOrderingHelper;

public class Dir extends Dir_Base {

	private static final String CONTENT_SEPARATOR = " | ";
	static final Logger log = LogManager.getRootLogger();
	public static final String SLASH_NAME = "/";
	public static final String DOT_NAME = ".";
	public static final String DOTDOT_NAME = "..";

	public Dir(){
		super();
	}

	public Dir(MyDrive drive, User owner, String name, String permissions){
		super.init(drive, owner, name, permissions);
		if(name.equals(SLASH_NAME)){
			drive.setRootDir(this);
		}
	}

	public void execute(String[] args){
		throw new CannotExecuteDirectoryException();
	}


	public Dir(MyDrive drive, User owner, String name, String permissions, Dir dir){
		super.init(drive, owner, name, permissions, dir);
	}

	public Dir(MyDrive drive, String name, String permissions, Dir dir){
		User owner = drive.getUserByUsername(SuperUser.USERNAME);
		super.init(drive, owner, name, permissions, dir);
	}

	public Dir(MyDrive drive, Element node){
		this.importXML(drive, node);
	}

	public File getFileByName(String nameToLook){
		if(nameToLook.equals(Dir.DOT_NAME))
			return this;
		if(nameToLook.equals(Dir.DOTDOT_NAME))
			return this.getFather();

		for (File file : this.getFileSet()){
			if(file.getName().equals(nameToLook)){
				if (file instanceof Link){
					log.trace(file.getName() + "==" + nameToLook);
					return ((Link) file).getLinkedFile();
				} else {
					return file;
				}
			}
		}
		throw new NoDirException(nameToLook, this.getName());
	}

	//This method is equal to getFileByName(String nameToLook) but if nameToLook is Link, it return link, 
	//unlike the other who returned the file pointed to by link.
	public File getFileByName2(String nameToLook){
		if(nameToLook.equals(Dir.DOT_NAME))
			return this;
		if(nameToLook.equals(Dir.DOTDOT_NAME))
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

	public void createFile(User owner, String fileName, String type, String content){
		switch(type){
		case "link":
			if(!this.isPath(content)) throw new WrongContentException();
			new Link(MyDrive.getInstance(),owner, fileName, owner.getMask(), content, this);
			break;
		case "dir":
			if(content != null)throw new WrongContentException();
			new Dir(MyDrive.getInstance(), owner, fileName, owner.getMask(), this);
			break;
		case "plainFile":
			new PlainFile(MyDrive.getInstance(), owner, fileName, owner.getMask(), content, this);
			break;
		case "app":
			new App(MyDrive.getInstance(), owner,fileName, owner.getMask(), content, this);
			break;
		default:
			throw new TypeDoesNotExistException();
		}
	}

	public void deleteFile(String fileName, User whoWantsToDelete){
		if(fileName.equals(Dir.SLASH_NAME)) throw new CannotDeleteSlashDirException();
		if(fileName.equals(Dir.DOT_NAME) || fileName.equals(Dir.DOTDOT_NAME)) throw new CannotDeleteDotOrDotDotException();
		File fileToDelete = this.getFileByName(fileName);
		Dir dirToDelete = null;

		if(!fileToDelete.hasPermissionsForDelete(whoWantsToDelete)){
			throw new PermissionDeniedException(
					whoWantsToDelete,
					PermissionDeniedException.DELETE,
					fileToDelete);
		}

		if(fileToDelete instanceof Dir){
			dirToDelete = (Dir)fileToDelete;
			this.removeDir(dirToDelete);
		}else{
			fileToDelete.getFather().removeFile(fileToDelete);
			fileToDelete.remove();
		}
	}

	private void removeDir(Dir dirToDelete) {
		checkDirNotInUseByAnotherSession(dirToDelete);

		if(!isEmpty(dirToDelete)){
			CopyOnWriteArrayList<File> files = new CopyOnWriteArrayList<File>(dirToDelete.getFileSet());
			Iterator<File> it = files.iterator();
			File fileToDelete = null;
			while(it.hasNext()){
				fileToDelete = it.next();

				if (fileToDelete instanceof Dir) {
					((Dir) fileToDelete).removeAllFiles();
				}
				dirToDelete.removeFile(fileToDelete);
				fileToDelete.remove();
			}
		}

		dirToDelete.getFather().removeFile(dirToDelete);
		dirToDelete.remove();
	}

	private void removeAllFiles() {
		CopyOnWriteArrayList<File> files = new CopyOnWriteArrayList<File>(this.getFileSet());
		Iterator<File> it = files.iterator();
		File fileToDelete = null;
		while(it.hasNext()){
			fileToDelete = it.next();

			if(fileToDelete instanceof Dir) {
				((Dir)fileToDelete).removeAllFiles();
			}

			this.removeFile(fileToDelete);
			fileToDelete.remove();
		}
	}

	private void checkDirNotInUseByAnotherSession(Dir dirToDelete) {
		if (isDirInUseByAnotherSession(dirToDelete)) {
			throw new CannotDeleteDirInUseBySessionException(dirToDelete);
		}
	}

	private boolean isDirInUseByAnotherSession(Dir dirToDelete) {
		LoginManager loginManager = this.getMydrive().getLoginManager();
		return loginManager.isDirInUseByAnySession(dirToDelete);
	}
	
	private boolean isPath(String newContent) {
		return Dir.SLASH_NAME.equals(""+newContent.charAt(0));
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

		maybeString = Optional.ofNullable(elm.getChildText("path"));
		String path = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir - path is not optional and must be supplied.")));
		Dir father = (Dir)drive.getFileByPathname(path, true, null);

		maybeString = Optional.ofNullable(elm.getChildText("name"));
		String name = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir - name is not optional and must be supplied.")));

		maybeString = Optional.ofNullable(elm.getChildText("owner"));
		String ownerName = (maybeString.orElse(SuperUser.USERNAME));
		User owner = drive.getUserByUsername(ownerName);

		maybeString = Optional.ofNullable(elm.getChildText("perm"));
		String perm = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir - permission is not optional and must be supplied.")));

		super.init(drive, owner, name, perm, father);
	}

	public boolean isTheSameAs(Object anotherObject) {
		if (!(anotherObject instanceof Dir)) {
			return false;
		} else {
			Dir anotherDir = (Dir)anotherObject;
			return this.getName().equals(anotherDir.getName()) &&
					this.getPath().equals(anotherDir.getPath());
		}
	}

	public List<File> listDir(User user){
		List<File> files = new ArrayList<File>();

		if(this.hasPermissionsForRead(user)){
			for(File file : this.getFileSet()){
				files.add(file);
			}

			return files;
		}
		else{
			throw new PermissionDeniedException(user, PermissionDeniedException.READ, this);
		}
	}
	
	public String readPlainFileContent(Long token, String nameFile, User userToRead){
		PlainFile fileToRead;
		
		try{
			File file = this.getFileByName2(nameFile);
			if(file instanceof Link){
				if(file.hasPermissionsForRead(userToRead)){
					file = this.getFileByName(nameFile);
					readPlainFileContent(token, file.getName(), userToRead);
				}
				else{
					throw new PermissionDeniedException(userToRead,
							PermissionDeniedException.READ,
							file);
				}
			}
			if(file instanceof PlainFile){
				if(file.hasPermissionsForRead(userToRead)){
					fileToRead = (PlainFile) file;
				}
				else{
					throw new PermissionDeniedException(userToRead,
							PermissionDeniedException.READ,
							file);
				}
			}
			else{
				throw new NoPlainFileException(nameFile);
			}
		}
		catch(NoDirException nde){
			throw new FileDoesNotExistException(nameFile);
		}
		
		return fileToRead.getContent();
	}
	
}