
package pt.tecnico.myDrive.domain;

import java.util.HashSet;
import java.util.Set;

import org.jdom2.Document;
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


	public Document exportXML() {
		Element element = new Element("myDrive");
		Document doc = new Document(element); //
		//Element users = new Element("myDriveUsers");
		//element.addContent(users);
		//Element files = new Element("myDriveFiles");
		//element.addContent(files);
		for (User user: getUserSet()){
			element.addContent(user.exportXML());
		}
		for (File f: getFileSet()){
			element.addContent(f.exportXML());
		}
		return doc;
	}
	/*public void xmlImport(Element element) {
		for (Element node: element.getChildren("person")) {
			String name = node.getAttribute("name").getValue();
			Person person = getPersonByName(name);

			if (person == null) // Does not exist
				person = new Person(this, name);

			person.xmlImport(node);
		}
	}*/

	public void importXML(Element toImport){
		this.initBaseState();
		for(Element node : toImport.getChildren()){
			this.importElement(node);
			//User user = new User(this, node);
			//System.out.println(user.toString());
			//			String password = node.getAttribute("password").getValue();
			//		String name = node.getAttribute("name").getValue();
			//	String mask = node.getAttribute("username").getValue();
			//if(user == null) user = new User(this, username, password, name, mask);
			//user.importXML(node);
		} /*
			String type = node.getName();
			switch(type){
			case "user":

				String username = toImport.getChildren("username").getValue();
				String password = toImport.getChildren("password");
				String name = toImport.getChildren("name");
				String mask = toImport.getChildren("username");
				User user = getUserByUsername(username);
				if(user == null) user = new User(this, username, password, name, mask);
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

		}*/
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

		
		default: System.out.println("Ainda nao sei fazer isto " + typeOfNode); return;
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