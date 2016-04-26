package pt.tecnico.myDrive.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import pt.tecnico.myDrive.exception.CannotListTokenException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidPasswordException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class User extends User_Base {
	private final int MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION = 120;
	static final Logger log = LogManager.getRootLogger();
	protected User(){
		super();
		super.setMaxInactivityTimeOfSession(this.MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION);
	}

	public User(MyDrive drive, String username, String password, String name, String mask, String homeDir){
		this.init(drive, username, password, name, mask, homeDir);
		super.setMaxInactivityTimeOfSession(this.MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION);
	}

	public User(MyDrive drive, Element xml){
		this.importXML(drive, xml);
		super.setMaxInactivityTimeOfSession(this.MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION);
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

		if(!(drive.isDefaultUser(username))){
			if(password.length() >= 8){
				setPassword(password);
			}
			else{
				throw new InvalidPasswordException(username, password);
			}
		}

		omission = Optional.ofNullable(name);
		name = omission.orElse(username);
		setName(name);

		omission = Optional.ofNullable(mask);
		mask = omission.orElse("rwxd----");
		setMask(mask);

		setMydrive(drive);
		if(!(drive.isDefaultUser(username))){
			if(homeDir == null){
				Dir dir = drive.createUserDir(this);
				homeDir = dir.getPath();
				return;
			}
			else{
				this.getMydrive().getFileByPathname(homeDir, true, this);
			}
		}
		setHomeDir(homeDir);
		super.setMaxInactivityTimeOfSession(this.MAX_INACTIVITY_TIME_IN_MINUTES_OF_SESSION);
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
		Element elementPassword = new Element("password");
		elementPassword.setText(this.getPassword());
		element.addContent(elementPassword);
		Element elementName = new Element("name");
		elementName.setText(this.getName());
		element.addContent(elementName);
		Element elementHomeDir = new Element("homeDir");
		elementHomeDir.setText("/home/" + this.getUsername());
		element.addContent(elementHomeDir);
		Element elementMask = new Element("mask");
		elementMask.setText(this.getMask());
		element.addContent(elementMask);
		return element;
	}

	protected void importXML(MyDrive drive, Element elm){
		try{
			List<Element> children = elm.getChildren();
			String username = elm.getAttributeValue("username");
			String name = children.get(1).getText();
			String password = children.get(0).getText();
			String mask = children.get(3).getText();
			String homeDir = children.get(2).getText();
			init(drive, username, password, name, mask, homeDir);
		}catch(Exception e){
			e.printStackTrace();
			throw new ImportDocumentException("In User : " + e.getMessage());
		}
	}
}