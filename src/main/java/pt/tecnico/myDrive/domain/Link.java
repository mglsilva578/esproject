package pt.tecnico.myDrive.domain;

public class Link extends Link_Base {
    
    public Link( MyDrive myDrive, User owner, int id, String name, String permissions,String content ) {
        super.init( myDrive, owner, name, permissions );
    }
    
    public Link(){}
    public void importXML(){}
}
