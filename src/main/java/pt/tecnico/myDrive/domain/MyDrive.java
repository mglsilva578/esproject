
package pt.tecnico.myDrive.domain;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.CannotEraseFileException;
import pt.tecnico.myDrive.exception.InvalidPathameException;
import pt.tecnico.myDrive.exception.NoDirException;
import pt.tecnico.myDrive.exception.NoPlainFileException;
import pt.tecnico.myDrive.exception.UsernameAlreadyExistsException;
import pt.tecnico.myDrive.exception.UsernameDoesNotExistException;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;

public class MyDrive extends MyDrive_Base {
	static final Logger log = LogManager.getRootLogger();
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

	public File getFileByPathname(String pathname, boolean createMissingDir){
		if(pathBeginsWithSlash(pathname)){
			return findAllDirInPathname(pathname, createMissingDir);
		}else{
			throw new InvalidPathameException(pathname);
		}
	}

	private File findAllDirInPathname(String pathname, boolean createMissingDir) {
		File content = null;
		int nextDirIndex = 1;
		Dir previousDir;
		String[] pathnameSplit;
		int howManyLinks;
		previousDir = getRootDir();
		User rootUser = this.getUserByUsername(SuperUser.NAME);
		if(pathname.length() == 1) return previousDir;
		pathnameSplit = pathname.split(Dir.SLASH_NAME);
		howManyLinks = pathnameSplit.length;
		while(nextDirIndex < howManyLinks){
			while(true){
				try{
					content = previousDir.getFileByName(pathnameSplit[nextDirIndex]);
					break;						
				}catch(NoDirException nde){
					if(createMissingDir){
						new Dir(this, rootUser, pathnameSplit[nextDirIndex], rootUser.getMask(), previousDir);						
					}
					else{
						throw nde;
					}
				}				
			}
			nextDirIndex++;
			if (content instanceof Dir){
				previousDir = (Dir)content;
			}
			else{
				break;
			}
		}
		if(content == null){
			throw new InvalidPathameException(pathname);
		}
		else{
			return content;				
		}
	}

	private boolean pathBeginsWithSlash(String pathname) {
		return new String("" + pathname.charAt(0)).equals(Dir.SLASH_NAME);
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
		int id = this.getFileIdCounter();
		this.setFileIdCounter(id + 1);
		return id;
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
		File fileToRemove = this.getFileByPathname( pathnameFileToFind, false );
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
		File file = getFileByPathname(path, false);
		if (file instanceof PlainFile){
			return ((PlainFile)file).getContent();
		}
		else {
			throw new NoPlainFileException(path);
		}
	}

	public String listDirContent(String pathname){
		File fileFound = this.getFileByPathname(pathname, false);
		if(fileFound instanceof Dir){
			return ((Dir) fileFound).getContentNames();
		}else{
			throw new WrongTypeOfFileFoundException(fileFound.getName(), "Dir");
		}
	}


	public Document exportXML() {
		Element element = new Element("myDrive");
		Document doc = new Document(element); //
		for (User user: getUserSet()){
			element.addContent(user.exportXML());
		}
		for (File f: getFileSet()){
			element.addContent(f.exportXML());
		}
		return doc;
	}

	public void importXML(Element toImport){
		//this.initBaseState();
		for(Element node : toImport.getChildren()){
			this.importElement(node);
		} 
	}

	private void importElement(Element node) {
		String typeOfNode = node.getName();
		switch(typeOfNode){
		case "superUser" : new SuperUser(this, node);break;
		case "user" : new User(this, node);break;
		case "dir" : new Dir(this, node); break;
		case "plain" : new PlainFile(this, node); break;
		case "app" : new App(this, node); break;
		case "link" : new Link(this, node); break;


		default: log.error("Ainda nao sei fazer isto " + typeOfNode); return;
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

	private void initBaseState() {
		SuperUser superUser = new SuperUser(this);
		Dir slash = new Dir(this, superUser, Dir.SLASH_NAME, superUser.getMask());
		Dir home = new Dir(this, superUser, "home", superUser.getMask(), slash);
		new Dir(this, superUser, "root", superUser.getMask(), home);
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