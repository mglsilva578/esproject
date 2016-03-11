package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class User extends User_Base {

	protected User(){super();}
	
    public User(MyDrive drive, String username, String password, String name, String mask) {
        this.init( drive, username, password, name, mask );
    }

    private boolean isUsernameValid(String username) {
		if(username == null) return false;
		return isComposedOnlyLettersDigits(username);
	}
	
	private boolean isComposedOnlyLettersDigits(String username) {
		String patternToMatch = "[a-zA-Z0-9]+";
		return username.matches(patternToMatch);
	}
	
	protected void init( MyDrive md, String username, String password, String name, String mask ){
		if(this.isUsernameValid(username)){
			setUsername(username);    		
		}else{
			throw new InvalidUsernameException(username);
		}
		setPassword(password);
		setName(name);
		setMask(mask);
		setMydrive(md);
	}
	
	@Override
    public void setMydrive(MyDrive md) {
        if (md == null)
            super.setMydrive(null);
        else
            md.addUser(this);
    }
	
	public void remove(){
		setMydrive(null);
		deleteDomainObject();
	}
	
	
	@Override
	public String toString(){
		String description = "";
		description += "User with username " + this.getUsername() + "\n";
		description += "\twith password " + this.getPassword() + "\n";
		description += "\twith name " + this.getName() + "\n";
		description += "\twith mask " + this.getMask() + "\n";
		return description;
	}
    
}
