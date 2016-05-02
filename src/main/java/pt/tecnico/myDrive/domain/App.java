package pt.tecnico.myDrive.domain;


import java.util.Optional;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.WrongContentException;

public class App extends App_Base {

	private static final String DEFAULT_METHOD = "main";
	public App(){
		super();
	}

	public App(MyDrive drive, User owner, String name, String permissions,String content, Dir dir){
		super.init(drive, owner, name, permissions, content, dir);
	}

	public App(MyDrive drive, Element xml){
		this.importXML(drive, xml);
	}

	@Override
	public void setContent (String newContent){
		if(isFullyQualifiedName(newContent)){
			setContent (newContent);
		}
		else
			throw new WrongContentException();  				
	}

	private boolean isFullyQualifiedName(String newContent) {
		String regex = "[a-zA-Z]+\\.[a-zA-Z]+(\\.[a-zA-Z]){0,1}+";
		if(newContent.matches(regex)){
			return true;
		}
		else{
			return false;
		}
	}

	public String toString(){
		String description = super.toString("app");
		return description;
	}

	public Element exportXML() {
		Element element = super.exportXML(); 
		element.setName("app");
		return element;
	}

	public void importXML(MyDrive drive, Element elm){
		Optional<String> maybeString = null;
		String name = null;
		String path = null;
		String owner = null;
		String contents = null;
		String last_modification = null;
		String id;
		User _owner;

		for(Element element : elm.getChildren()){
			switch(element.getName()){
			case "name":
				name = element.getText();
				break;
			case "path":
				path = element.getText();
				break;
			case "owner":
				owner = element.getText();
				break;
			case "contents":
				contents = element.getText();
				break;
			case "last_modification":
				last_modification = element.getText();
				break;
			}
		}
		if(name == null) throw new ImportDocumentException("App \n name is not optional and must be supplied");

		String _name = name;
		maybeString = Optional.ofNullable(elm.getAttributeValue("id"));
		id = (maybeString.orElseThrow(() -> new ImportDocumentException("APP <"+ _name +"> ID is not optional and must be supplied." + elm.toString())));

		if(path == null) throw new ImportDocumentException("App <"+ name +"> \n path is not optional and must be supplied.");
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);

		if(owner == null) owner = SuperUser.USERNAME;
		_owner = drive.getUserByUsername(owner);

		if(contents == null) contents = DEFAULT_METHOD;
		String perm = _owner.getMask();

		if(last_modification == null) throw new ImportDocumentException("App <"+ name +"> date of last modification is not optional and must be supplied.");
		this.init(drive, id, _owner, name, perm, contents, father, last_modification);


	}

}
