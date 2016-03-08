package pt.tecnico.myDrive.domain;

public class SuperUser extends SuperUser_Base {
	
    public SuperUser() {
        super();
        setUsername("root");
        setName("Super User");
        setMask("rwxdr-x-");
        setPassword("***");
    }
    
}
