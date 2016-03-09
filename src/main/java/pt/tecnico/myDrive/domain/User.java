package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class User extends User_Base {

	protected User(){super();}
	
    public User(String username, String password, String name, String mask) {
    	if(this.isUsernameValid(username)){
    		setUsername(username);    		
    	}else{
    		throw new InvalidUsernameException(username);
    	}
        setPassword(password);
        setName(name);
        setMask(mask);
    }

    private boolean isUsernameValid(String username) {
		if(username == null) return false;
		return isComposedOnlyLettersDigits(username);
	}
	
	private boolean isComposedOnlyLettersDigits(String username) {
		String patternToMatch = "[a-zA-Z0-9]+";
		return username.matches(patternToMatch);
	}
    
}
