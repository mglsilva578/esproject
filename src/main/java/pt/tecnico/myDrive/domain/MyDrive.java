package pt.tecnico.myDrive.domain;

import java.util.HashSet;
import java.util.Set;

import pt.ist.fenixframework.FenixFramework;
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
	
	public void erasePlainFileOrEmptyDirectory( String pathname ){
		// TODO - para joseluis
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
	
	private boolean isEmptyOfFiles() {
		return super.getFileSet().size() == 0;
	}

	private boolean isEmptyOfUsers() {
		return super.getUserSet().size() == 0;
	}
	
}