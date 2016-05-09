package pt.tecnico.myDrive.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.CannotDeleteRootException;
import pt.tecnico.myDrive.exception.CannotEraseFileException;
import pt.tecnico.myDrive.exception.FileDoesNotExistException;
import pt.tecnico.myDrive.exception.InvalidPathnameException;
import pt.tecnico.myDrive.exception.NoDirException;
import pt.tecnico.myDrive.exception.NoPlainFileException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.exception.UsernameAlreadyExistsException;
import pt.tecnico.myDrive.exception.UsernameDoesNotExistException;
import pt.tecnico.myDrive.exception.WrongContentException;
import pt.tecnico.myDrive.exception.WrongTypeOfFileFoundException;
import pt.tecnico.myDrive.util.SetOrderingHelper;

public class MyDrive extends MyDrive_Base {
	static final Logger log = LogManager.getRootLogger();
	private MyDrive() {
		super();
		setRoot(FenixFramework.getDomainRoot());
		this.setLoginManager(LoginManager.getInstance());
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

	public File getFileByPathname(String pathname, boolean createMissingDir, User user){
		if(pathBeginsWithSlash(pathname)){
			return findAllDirInPathname(pathname, createMissingDir, user);
		}else{
			throw new InvalidPathnameException(pathname);
		}
	}

	private File findAllDirInPathname(String pathname, boolean createMissingDir, User user) {
		File content = null;
		int nextDirIndex = 1;
		Dir previousDir;
		String[] pathnameSplit;
		int howManyLinks;
		previousDir = getRootDir();
		if(user == null){
			user = this.getUserByUsername(SuperUser.USERNAME);
		}
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
						new Dir(this, user, pathnameSplit[nextDirIndex], user.getMask(), previousDir);						
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
			throw new InvalidPathnameException(pathname);
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
		File fileToRemove = this.getFileByPathname( pathnameFileToFind, false, null );
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



	public boolean hasUser(String username) {
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
		if(!user.getUsername().equals(SuperUser.USERNAME)){ 
			user.remove();
			super.removeUser(user);	
		}
		else{
			throw new CannotDeleteRootException();
		}	
		
	}

	public String listDirContent(String pathname){
		File fileFound = this.getFileByPathname(pathname, false, null);
		if(fileFound instanceof Dir){
			return ((Dir) fileFound).getContentNames();
		}else{
			throw new WrongTypeOfFileFoundException(fileFound.getName(), "Dir");
		}
	}


	public Document exportXML() {
		Element element = new Element("myDrive");
		Document doc = new Document(element);

		for (User user: getUserSet()){
			if(!(user instanceof SuperUser)){
				element.addContent(user.exportXML());
			}
		}

		for (File f: this.getFileSet()){
			if(!(f.getName().equals("/") 
					|| f.getName().equals("home")
					|| f.getName().equals(SuperUser.HOME_DIR)
					|| isUserHomeDir(f))){
				element.addContent(f.exportXML());
			}
		}
		return doc;
	}

	private boolean isUserHomeDir(File f) {
		if(f == null) return false;
		if(f.getPath().equals("/home")){
			String fileName = f.getName();
			for (User user : this.getUserSet()) {
				if (fileName.equals(user.getUsername())){
					return true;
				}
			}
		}
		return false;
	}

	public void importXML(Element toImport){
		//this.initBaseState();

		Optional<String> maybeString = null;
		int highestIdImported = -1;
		int currentId;
		for(Element node : toImport.getChildren()){
			log.trace("MyDrive importing " + node.getName());
			maybeString = Optional.ofNullable(node.getAttributeValue("id"));
			currentId = Integer.parseInt(maybeString.orElse("-1"));
			if(highestIdImported < currentId ){
				highestIdImported = currentId;
				this.setFileIdCounter(highestIdImported + 1);
			}
			this.importElement(node);
		} 
		log.trace("Finished importing elements. Current next id is " + this.getFileIdCounter());
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

		default: log.error("Impossivel importar tipo de elemento desconhecido <" + typeOfNode + ">"); return;
		}

	}

	public Dir createUserDir( User user ){
		Dir slash = this.getRootDir();
		File fileFound = slash.getFileByName( "home" );
		if (fileFound instanceof Dir){
			Dir home = (Dir) fileFound;			
			Dir userDir = new Dir( this, user, user.getUsername(), user.getMask(), home);
			user.setHomeDir(userDir.getPath() + Dir.SLASH_NAME + userDir.getName());
			return userDir;
		}else{
			throw new WrongTypeOfFileFoundException(fileFound.getName(), "dir");
		}
	}

	private void initBaseState() {
		SuperUser rootUser = new SuperUser(this);
		Dir slash = new Dir(this, rootUser, Dir.SLASH_NAME, rootUser.getMask());
		this.setRootDir(slash);
		Dir home = new Dir(this, rootUser, "home", rootUser.getMask(), slash);
		new Dir(this, rootUser, SuperUser.HOME_DIR, rootUser.getMask(), home);
		Nobody nobody = new Nobody(this);
		new Dir(this, nobody, Nobody.HOME_DIR, nobody.getMask(), home);
	}

	@Override
	public String toString(){
		String description = "";

		description += "\n\t\tMyDrive files\n";
		for (File file : SetOrderingHelper.sortFileSetById(this.getFileSet())) {
			description += "\t" + file.toString() + "\n";
		}

		description += "\n\t\tMyDrive users\n";
		for (User user : this.getUserSet()) {
			log.trace(user.getName() + "   " + getUserSet().size());
			description += "\t" + user.toString() + "\n";
		}

		return description;
	}



	public void cleanup() {
		for (File file: getFileSet()){
			if(notIsDirInit(file)){
				file.remove();
			}
		}

		for (User user: getUserSet()){
			if(this.isDefaultUser(user.getUsername())) continue;
			user.remove();
		}
	}

	private boolean notIsDirInit(File file){
		if(file.getName().equals(Dir.SLASH_NAME) || 
				(file.getPath().equals("/") && file.getName().equals("home")) || 
				(file.getPath().equals("/home") && file.getName().equals(SuperUser.HOME_DIR)) ||
				(file.getPath().equals("/home") && file.getName().equals(Nobody.HOME_DIR))){
			return false;
		}
		else {
			return true;
		}
	}

	public boolean isDefaultUser(String username) {
		return username.equals(Nobody.USERNAME) ||
				username.equals(SuperUser.USERNAME);
	}
	
	public void changePlainFileContent (Long token, String path, String newContent){
		File fileToChange = null;
		LoginManager loginManager = this.getLoginManager();
		Session session = loginManager.getSessionByToken(token);
		User user = session.getOwner();
		
		fileToChange = this.getFileByPathname(path, false, user);
		fileToChange.confirmFileIsPlainFile(fileToChange.getName());
		fileToChange.confirmUserHasPermissionToWrite(user);
		fileToChange.confirmContentIsValid(newContent);

		((PlainFile) fileToChange).changePlainFileContent(newContent);
	}
}