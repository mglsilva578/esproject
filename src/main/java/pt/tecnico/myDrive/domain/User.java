package pt.tecnico.myDrive.domain;

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

	public void importXML(Element elm){
		try{
			this.setUsername(elm.getAttributeValue("username"));
			this.setPassword(elm.getAttributeValue("password"));
			this.setName(elm.getAttributeValue("name"));
			this.setHomeDir(elm.getAttributeValue("home"));
			this.setMask(elm.getAttributeValue("mask"));
		}catch(Exception e){
			throw new ImportDocumentException("user");
		}
	}

	/*public Element xmlExport() {
        Element element = new Element("user");
        element.setAttribute("username", getUsername());
        element.setAttribute("password", getPassword());
        element.setAttribute("name", getName());
        element.setAttribute("home", getHomeDir());
        element.setAttribute("mask", getMask());
        Element filesElement = new Element("files");
        element.addContent(filesElement);

        for (File f: getFileSet())
            filesElement.addContent(f.xmlExport());

        return element;
    }*/
}
