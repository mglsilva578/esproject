package pt.tecnico.myDrive.domain;

public class Extension extends Extension_Base {
    
    protected Extension() {
        super();
    }
    public Extension(String fileExtension, String fileName, User owner){
    	this.setFileExtension(fileExtension);
    	this.setFileName(fileName);
    	this.setOwner(owner);
    }
}
