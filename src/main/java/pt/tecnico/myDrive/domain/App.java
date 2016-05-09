package pt.tecnico.myDrive.domain;


import java.util.Optional;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidAppMethodException;
import pt.tecnico.myDrive.exception.WrongContentException;

public class App extends App_Base {

	private static final String DEFAULT_METHOD = "main";
	public App(){
		super();
	}

	public App(MyDrive drive, User owner, String name, String permissions,String content, Dir dir){
		if(isFullyQualifiedName(content)) {
			super.init(drive, owner, name, permissions, content, dir);
		}
		else{
			throw new WrongContentException(); 
		}
	}

	public App(MyDrive drive, Element xml){
		this.importXML(drive, xml);
	}

	private boolean isFullyQualifiedName(String newContent) {
		String regex = "([a-zA-Z]+\\.[a-zA-Z]+)+";
		if(newContent.matches(regex)){
			return true;
		}
		else{
			return false;
		}
	}
	public void execute(String... args){
    	switch(this.getContent()){
    		case "pt.tecnico.myDrive.domain":
    			AppMethods.main(args);
    			break;
    		case "pt.tecnico.myDrive.domain.pdf":
    			AppMethods.pdfViewer(args);
    			break;
    		case "pt.tecnico.myDrive.domain.txt":
    			AppMethods.notePad(args);
    			break;
    		default:
    			throw new InvalidAppMethodException(this.getContent());
    			
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

    	maybeString = Optional.ofNullable(elm.getChildText("name"));
    	String name = (maybeString.orElseThrow(() -> new ImportDocumentException("App \n name is not optional and must be supplied.")));

    	maybeString = Optional.ofNullable(elm.getChildText("path"));
		String path = (maybeString.orElseThrow(() -> new ImportDocumentException("App <"+ name +"> \n path is not optional and must be supplied.")));
		drive.getFileByPathname(path, true, null);
		Dir father = (Dir)drive.getFileByPathname(path, true, null);


		maybeString = Optional.ofNullable(elm.getChildText("owner"));
		String ownerName = (maybeString.orElse(SuperUser.USERNAME));
		User owner = drive.getUserByUsername(ownerName);

		maybeString = Optional.ofNullable(elm.getChildText("method"));
		String contents = (maybeString.orElse(DEFAULT_METHOD));
		
		String perm = owner.getMask();
		
		this.init(drive, owner, name, perm, contents, father);
	}

}
