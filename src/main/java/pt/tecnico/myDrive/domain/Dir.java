package pt.tecnico.myDrive.domain;
import java.util.Iterator;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.CannotDeleteDotOrDotDotException;
import pt.tecnico.myDrive.exception.CannotDeleteSlashDirException;
import pt.tecnico.myDrive.exception.ExceedsLimitPathException;
import pt.tecnico.myDrive.exception.FileAlreadyExistsException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.NoDirException;
import pt.tecnico.myDrive.exception.NoPlainFileException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.TypeDoesNotExistException;
import pt.tecnico.myDrive.exception.WrongContentException;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;
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
					return ((Link) file).getLinkedFile();
				} else {
					return file;
				}
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
		int size = this.getPath().length() + this.getName().length() + PATH_SEPARATOR.length() + fileName.length();
		if(size <= 1024){

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
		else{
			throw new ExceedsLimitPathException();
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
		if(!isEmpty(dirToDelete)){
			CopyOnWriteArrayList<File> files = new CopyOnWriteArrayList<File>(dirToDelete.getFileSet());
			Iterator<File> it = files.iterator();
			File fileToDelete = null;
			while(it.hasNext()){
				fileToDelete = it.next();
				dirToDelete.removeFile(fileToDelete);
				fileToDelete.remove();
			}
		}

		dirToDelete.getFather().removeFile(dirToDelete);
		dirToDelete.remove();
	}

	/**
	 * @throws NoPlainFileException
	 * @throws PermissionDeniedException
	 * @throws WrongContentException
	 */
	public void changePlainFileContent (String fileName, String newContent, User whoWantsToChange){
		File fileToChange = null;
		
		fileToChange = this.confirmFileExists (fileName, fileToChange);
		this.confirmFileIsPlainFile (fileToChange, fileName);
		this.confirmUserHasPermissionToWrite (fileToChange, whoWantsToChange);
		this.confirmContentIsValid (newContent, fileToChange);
		
		this.changePlainFileContent ((PlainFile)fileToChange, newContent);
	}

	private File confirmFileExists(String fileName, File fileToChange) {
		try{
			 return this.getFileByName(fileName);
		} catch (NoDirException nde){
			throw new NoPlainFileException(fileName);
		}
	}

	private void confirmFileIsPlainFile (File fileToChange, String fileName){
		if (!(fileToChange instanceof PlainFile)){
			throw new WrongTypeOfFileFoundException(fileName, "PlainFile");
		}
	}

	private void confirmUserHasPermissionToWrite (File fileToChange, User whoWantsToChange){
		if(!fileToChange.hasPermissionsForWrite(whoWantsToChange)){
			throw new PermissionDeniedException(
					whoWantsToChange,
					PermissionDeniedException.WRITE,
					fileToChange);
		}
	}

	private void confirmContentIsValid (String newContent, File fileToChange){
		if ((newContent == null)){
			throw new WrongContentException (newContent, fileToChange);
		} 
	}

	private boolean isPath(String newContent) {
		return Dir.SLASH_NAME.equals(""+newContent.charAt(0));
	}

	private void changePlainFileContent (PlainFile fileToChange, String newContent){
		fileToChange.setContent (newContent);
		fileToChange.setLast_modification(new DateTime());
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
		String name = null;
    	String path = null;
    	String owner = null;
    	String last_modification = null;
    	String id;
    	User _owner;
    
    	for(Element element : elm.getChildren()){
    		switch(element.getName()){
    			case "name":
    				name = element.getText();
    				break;
    			case "path":
    				path = element.getText();
    				break;
    			case "owner":
    				owner = element.getText();
    				break;
    			case "last_modification":
    				last_modification = element.getText();
    				break;
    		}
    	}
    	if(name == null) throw new ImportDocumentException("Dir - name is not optional and must be supplied.");
    	
    	String _name = name;
    	maybeString = Optional.ofNullable(elm.getAttributeValue("id"));
		id = (maybeString.orElseThrow(() -> new ImportDocumentException("Dir <"+ _name +"> ID is not optional and must be supplied." + elm.toString())));
		
		if(path == null) throw new ImportDocumentException("Dir <"+ name +"> \n path is not optional and must be supplied.");
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);
		
		if(owner == null) owner = SuperUser.USERNAME;
		_owner = drive.getUserByUsername(owner);
		
		String perm = _owner.getMask();
		
		if(last_modification == null) throw new ImportDocumentException("Dir <"+ name +"> date of last modification is not optional and must be supplied.");
		this.init(drive, id, _owner, name, perm, father, last_modification);

	}
}
