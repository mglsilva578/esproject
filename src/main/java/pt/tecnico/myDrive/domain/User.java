package pt.tecnico.myDrive.domain;

public class User extends User_Base {

	protected User(){super();}
	
    public User(String username, String password, String name, String mask) {
        setUsername(username);
        setPassword(password);
        setName(name);
        setMask(mask);
    }
    
}
