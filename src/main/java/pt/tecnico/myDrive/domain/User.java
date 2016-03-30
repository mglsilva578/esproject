package pt.tecnico.myDrive.domain;

import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import pt.tecnico.myDrive.exception.CannotListTokenException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class User extends User_Base {
	static final Logger log = LogManager.getRootLogger();
	protected User(){
		super();
	}

	public User(MyDrive drive, String username, String password, String name, String mask, String homeDir){
		this.init(drive, username, password, name, mask, homeDir);
	}

	public User(MyDrive drive, Element xml){
		this.importXML(drive, xml);
	}

	protected void init(MyDrive drive, String username, String password, String name, String mask, String homeDir){
		Optional<String> omission = null;
		if(this.isUsernameValid(username) && username.length() >= 3){
			setUsername(username);    	
		}
		else{
			throw new InvalidUsernameException(username);
		}
		omission = Optional.ofNullable(password);
		password = omission.orElse(username);
		setPassword(password);

		omission = Optional.ofNullable(name);
		name = omission.orElse(username);
		setName(name);

		omission = Optional.ofNullable(mask);
		mask = omission.orElse("rwxd----");
		setMask(mask);

		setMydrive(drive);
		if(!(username == "root")){
			if(homeDir == null){
				Dir dir = drive.createUserDir(this);
				homeDir = dir.getPath();
			}
			else{
				this.getMydrive().getFileByPathname(homeDir, true, this);
			}
		}
		setHomeDir(homeDir);
	}

	private boolean isUsernameValid(String username){
		if(username == null) return false;
		return isComposedOnlyLettersDigits(username);
	}

	private boolean isComposedOnlyLettersDigits(String username){
		String patternToMatch = "[a-zA-Z0-9]+";
		return username.matches(patternToMatch);
	}

	@Override
	public void setMydrive(MyDrive drive) {
		if (drive == null){
			this.remove();        	
		}else{
			drive.addUser(this);        	
		}
	}

	public void remove(){
		super.setMydrive(null);
		deleteDomainObject();
	}

	@Override
	public Set<Session> getSessionsSet(){
		throw new CannotListTokenException();
	}

	@Override
	public String toString(){
		String description = "\n";
		description += "\tusername: " + this.getUsername() + "\n";
		description += "\tpassword: " + this.getPassword() + "\n";
		description += "\tname: " + this.getName() + "\n";
		description += "\thomeDir: " + this.getHomeDir() + "\n";
		description += "\tmask: " + this.getMask() + "\n";
		return description;
	}

	public Element exportXML() {
		Element element = new Element("user");
		element.setAttribute("username", getUsername());
		element.setAttribute("password", getPassword());
		element.setAttribute("name", getName());
		element.setAttribute("homeDir", "/home/" + getUsername());
		element.setAttribute("mask", getMask());
		return element;
	}

	protected void importXML(MyDrive drive, Element elm){
		try{
			String username = elm.getAttributeValue("username");
			String name = elm.getAttributeValue("name");
			String password = elm.getAttributeValue("password");
			String mask = elm.getAttributeValue("mask");
			String homeDir = elm.getAttributeValue("homeDir");
			init(drive, username, password, name, mask, homeDir);
		}catch(Exception e){
			throw new ImportDocumentException("In User : " + e.getMessage());
		}
	}
}