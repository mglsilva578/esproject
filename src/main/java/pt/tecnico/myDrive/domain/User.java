package pt.tecnico.myDrive.domain;

public class User extends User_Base {

	protected User(){super();}
	
    public User(String username, String password, String name, String mask) {
    	if(isValidUsername(username)){
    		setUsername(username);    		
    	}else{
    		//TODO lancar aqui a excepcao adequada
    	}
        setPassword(password);
        setName(name);
        setMask(mask);
    }

	private boolean isValidUsername(String username) {
		if(username == null) return false;
		return !(username.isEmpty() || isComposedOnlyLettersDigits(username));
	}

	private boolean isComposedOnlyLettersDigits(String username) {
		String patternNotToMatch = "[a-zA-Z0-9]*[^(a-zA-Z0-9)]+[a-zA-Z0-9]*";
		return !username.matches(patternNotToMatch);
	}
    
}
