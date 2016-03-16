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

	public User(MyDrive drive, String username, String password, String name, String mask){
		this.init(drive, username, password, name, mask);
	}
	
	public User(MyDrive drive, Element xml){
		this.importXML(drive, xml);
	}

	private boolean isUsernameValid(String username){
		if(username == null) return false;
		return isComposedOnlyLettersDigits(username);
	}

	private boolean isComposedOnlyLettersDigits(String username){
		String patternToMatch = "[a-zA-Z0-9]+";
		return username.matches(patternToMatch);
	}

	protected void init(MyDrive drive, String username, String password, String name, String mask){
		if(this.isUsernameValid(username)){
			setUsername(username);    	
		}
		else{
			throw new InvalidUsernameException(username);
		}
		setPassword(password);
		setName(name);
		setMask(mask);
		setMydrive(drive);

		if(!(username == "root")){
			drive.createUserDir(this);		
		}
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
			
			init(drive, username, password, name, mask);
		}catch(Exception e){
			throw new ImportDocumentException("In User : " + e.getMessage());
		}
	}

}