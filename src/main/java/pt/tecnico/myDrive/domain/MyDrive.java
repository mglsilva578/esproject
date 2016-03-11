package pt.tecnico.myDrive.domain;

import java.util.HashSet;
import java.util.Set;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.CannotEraseFileException;
import pt.tecnico.myDrive.exception.UsernameAlreadyExistsException;
import pt.tecnico.myDrive.exception.UsernameDoesNotExistException;

public class MyDrive extends MyDrive_Base {

	private MyDrive() {
		super();
		setRoot(FenixFramework.getDomainRoot());
		this.setFileIdCounter(new Integer(0));
	}

	public static MyDrive getInstance(){
		MyDrive drive = FenixFramework.getDomainRoot().getMydrive();
		if( drive != null ){
			return drive;
		}else{
			return new MyDrive();
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

	public void erasePlainFileOrEmptyDirectoryByPathname( String pathnameFileToFind ){
		File fileToRemove = this.getFileByPathname( pathnameFileToFind );
		if ((isEmptyDirectory( fileToRemove ) ||
				isPlainFile( fileToRemove ))){
			// TODO cortar ligacao atraves do file como no PB
			this.removeFile(fileToRemove);
		}else{
			throw new CannotEraseFileException( pathnameFileToFind );
		}
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
	
	private File getFileByPathname( String pathnameFileToFind ){
		return null;
		// TODO implementar isto.
	}

	public void setRootDir( Dir rootDir ){
		rootDir.setId( this.getNewFileId() );
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

	

	boolean hasUser(String username) {
		return getUserByUsername(username) != null;
	}

	@Override
	public void addUser(User userToBeAdded) throws UsernameAlreadyExistsException{
		if (hasUser(userToBeAdded.getUsername()))
			throw new UsernameAlreadyExistsException(userToBeAdded.getUsername());
		super.addUser(userToBeAdded);
	}
	
	@Override
	public void removeUser(User user){
		user.remove();
		super.removeUser(user);		
	}

	private boolean isEmptyOfFiles() {
		return super.getFileSet().size() == 0;
	}

	private boolean isEmptyOfUsers() {
		return super.getUserSet().size() == 0;
	}
}