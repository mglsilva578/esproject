package pt.tecnico.myDrive.domain;

import java.util.Optional;

import org.jdom2.Attribute;
import org.jdom2.Element;

import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class User extends User_Base {

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
		
		if(this.isUsernameValid(username)){
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
		
		if(homeDir == null && !(username == "root")){
			Dir dir = drive.createUserDir(this);
			homeDir = dir.getName();
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
			super.setMydrive(null);        	
		}else{
			drive.addUser(this);        	
		}
	}

	public void remove(){
		setMydrive(null);
		deleteDomainObject();
	}


	@Override
	public String toString(){
		String description = "\n";
		description += "\tusername: " + this.getUsername() + "\n";
		description += "\tpassword: " + this.getPassword() + "\n";
		description += "\tname: " + this.getName() + "\n";
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
			Optional<String> maybeString = null;
			
			maybeString = Optional.ofNullable(elm.getChildText("name"));
			String name = (maybeString.orElseThrow(() -> new ImportDocumentException("User \n name is not optional and must be supplied.")));

			Optional<Attribute> maybeAttribute = Optional.ofNullable(elm.getAttribute("username"));
			String username = new String((maybeAttribute.orElseThrow(() -> new ImportDocumentException("User <"+ name +"> \n username is not optional and must be supplied.")))
					.getValue().getBytes("UTF-8"));
			
			maybeString = Optional.ofNullable(elm.getChildText("password"));
			String password = (maybeString.orElseThrow(() -> new ImportDocumentException("User <"+ name +"> \n password is not optional and must be supplied.")));
			
			maybeString = Optional.ofNullable(elm.getChildText("mask"));
			String mask = (maybeString.orElse("rwxd----"));
			
			String homeDir = elm.getChildText("home");
			
			//TO DO if homeDir!=null don't work
			if(homeDir == null && !(username == "root")){
				Dir dir = drive.createUserDir(this);
				homeDir = dir.getName();
			}
						
			init(drive, username, password, name, mask, homeDir);
		}catch(Exception e){
			throw new ImportDocumentException("In User : " + e.getMessage());
		}
	}

}