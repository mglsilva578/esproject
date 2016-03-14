
package pt.tecnico.myDrive.domain;

import java.util.HashSet;
import java.util.Set;

import org.jdom2.Element;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.CannotEraseFileException;
import pt.tecnico.myDrive.exception.InvalidPathameException;
import pt.tecnico.myDrive.exception.NoPlainFileException;
import pt.tecnico.myDrive.exception.UsernameAlreadyExistsException;
import pt.tecnico.myDrive.exception.UsernameDoesNotExistException;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;

public class MyDrive extends MyDrive_Base {

	private MyDrive() {
		super();
		setRoot(FenixFramework.getDomainRoot());
		this.setFileIdCounter( new Integer(0) );
	}

	private boolean isEmptyDirectory( File fileToTest ){
		if( fileToTest instanceof Dir ){
			return ((Dir)fileToTest).getFileSet().size() == 0;
		}else{
			return false;			
		}
	}

	private boolean isPlainFile ( File fileToTest ){
		return fileToTest instanceof PlainFile;
	}

	private boolean isEmptyOfFiles() {
		return super.getFileSet().size() == 0;
	}

	private boolean isEmptyOfUsers() {
		return super.getUserSet().size() == 0;
	}

	public static MyDrive getInstance(){
		MyDrive drive = FenixFramework.getDomainRoot().getMydrive();
		if( drive != null ){
			return drive;
		}else{
			return new MyDrive();
		}
	}

	public File getFileByPathname(String pathname){
		Dir previousDir;
		File content = null;
		String[] pathnameSplit;
		int nextDirIndex = 1; 
		int howManyLinks;
		if(new String("" + pathname.charAt(0)).equals(Dir.SLASH_NAME)){
			previousDir = this.getRootDir();
			if(pathname.length() == 1) return previousDir;
			pathnameSplit = pathname.split(Dir.SLASH_NAME);
			howManyLinks = pathnameSplit.length;
			
			while(nextDirIndex < howManyLinks){
				content = previousDir.getFileByName(pathnameSplit[nextDirIndex++]);
				if (content instanceof Dir){
					previousDir = (Dir)content;
				}else{
					break;
				}
			}
			if(content == null){
				throw new InvalidPathameException(pathname);
			}else{
				return content;				
			}
		}else{
			throw new InvalidPathameException(pathname);
		}
	}
	
	public File getFileByName(String name) {
		for (File file : getFileSet()) {
			if (file.getName().equals(name)) {
				return file;
			}
		}
		return null;
	}

	public boolean hasFile(String Name) {
		return getFileByName(Name) != null;
	}

	public boolean isEmpty(){
		return isEmptyOfUsers() && isEmptyOfFiles();
	}

	public Integer getNewFileId(){
		this.setFileIdCounter(this.getFileIdCounter() + 1);
		return this.getFileIdCounter();
	}

	public void removeUserByUsername(String usernameToRemove) throws UsernameDoesNotExistException{
		User userToRemove = this.getUserByUsername(usernameToRemove);
		super.removeUser(userToRemove);
	}


	public Set<String> getAllUsernames(){
		Set<String> allUsernames = new HashSet<String>();
		for(User user : this.getUserSet()){
			allUsernames.add(user.getUsername());
		}
		return allUsernames;
	}

	public void removePlainFileOrEmptyDirectoryByPathname( String pathnameFileToFind ){
		File fileToRemove = this.getFileByPathname( pathnameFileToFind );
		if ((isEmptyDirectory( fileToRemove ) ||
				isPlainFile( fileToRemove ))){
			fileToRemove.getFather().removeFile(fileToRemove);
			fileToRemove.remove();
		}else{
			throw new CannotEraseFileException( fileToRemove.getName() );
		}
	}



	public void setRootDir( Dir rootDir ){
		super.setRootDir( rootDir );
	}

	public User getUserByUsername(String usernameToFind) throws UsernameDoesNotExistException{
		for(User user : this.getUserSet()){
			if(user.getUsername().equals(usernameToFind)){
				return user;
			}
		}
		throw new UsernameDoesNotExistException(usernameToFind);
	}



	private boolean hasUser(String username) {
		try{
			getUserByUsername(username);
			return true;
		}catch( UsernameDoesNotExistException udnee){
			return false;
		}
	}

	@Override
	public void addUser(User userToBeAdded) throws UsernameAlreadyExistsException{
		if (hasUser(userToBeAdded.getUsername())){
			throw new UsernameAlreadyExistsException(userToBeAdded.getUsername());
		}
		super.addUser(userToBeAdded);
	}

	@Override
	public void removeUser(User user){
		user.remove();
		super.removeUser(user);		
	}
	
	public String readContent(String path){
		File file = getFileByPathname(path);
		if (file instanceof PlainFile){
			return ((PlainFile)file).getContent();
		}
		else {
			throw new NoPlainFileException(path);
		}
	}
	
	public String listDirContent(String pathname){
		File fileFound = this.getFileByPathname(pathname);
		if(fileFound instanceof Dir){
			return ((Dir) fileFound).getContentNames();
		}else{
			throw new WrongTypeOfFileFoundException(fileFound.getName(), "Dir");
		}
	}

	
	public Element exportXML() {
        Element element = new Element("myDrive");
        Element users = new Element("myDriveUsers");
        element.addContent(users);
        Element files = new Element("myDriveFiles");
        element.addContent(files);
        
        for (User user: getUserSet()){
        	users.addContent(user.exportXML());
        }
        for (File file: getFileSet()){
        	files.addContent(file.exportXML());
        }
        return element;
    }

	
	public void importXML(Element toImport){

		for(Element node : toImport.getChildren()){
			String type = node.getName();
			switch(type){
			case "user":
				String name = node.getAttributeValue("username");
				User user = getUserByUsername(name);
				if(user == null) user = new User();
				user.importXML(node);
				break;
			case "dir":
				Dir d = new Dir();
				d.importXML(node);
				break;
			case "plain":
				PlainFile p = new PlainFile();
				p.importXML(node);
				break;
			case "link":
				Link l = new Link();
				l.importXML(node);
				break;
			case "app":
				App a = new App();
				a.importXML(node);
				break;
			default:
				break;
			}
			
		}
	}

	public Dir createUserDir( User user ){
		Dir slash = this.getRootDir();
		File fileFound = slash.getFileByName( "home" );
		if (fileFound instanceof Dir){
			Dir home = (Dir) fileFound;			
			Dir userDir = new Dir( this, user, user.getUsername(), user.getMask(), home);
			return userDir;
		}else{
			throw new WrongTypeOfFileFoundException(fileFound.getName(), "dir");
		}
	}
	
	@Override
	public String toString(){
		String description = "";
		description += "\n\t\tMyDrive files\n";
		for (File file : this.getFileSet()) {
			description += "\t" + file.toString() + "\n";
		}

		description += "\n\t\tMyDrive users\n";
		for (User user : this.getUserSet()) {
			description += "\t" + user.toString() + "\n";
		}
		return description;
	}
}