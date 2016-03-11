package pt.tecnico.myDrive.domain;

public class App extends App_Base {
    
    public App( MyDrive myDrive, User owner, int id, String name, String permissions,String content ) {
        super.init( myDrive, owner, name, permissions );
    }
    
}
