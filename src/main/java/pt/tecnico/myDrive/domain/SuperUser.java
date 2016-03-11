package pt.tecnico.myDrive.domain;

public class SuperUser extends SuperUser_Base {
	
    public SuperUser(MyDrive md) {
        super();
        setUsername("root");
        setName("Super User");
        setMask("rwxdr-x-");
        setPassword("***");
        setMydrive(md);
    }
    
}
