package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import pt.tecnico.myDrive.exception.CannotListTokenException;

public class Session extends Session_Base {
    private static final int MAX_INACTIVITY_TIME_IN_MINUTES = 120;

	public Session(User user, Long token, Dir currentDir) {
        super();
        this.setOwner(user);
        this.setToken(token);
        this.setCurrentDir(currentDir);
        this.setLastActiveAt(new DateTime());
    }
    
    public boolean isExpired(){
    	int howManyMinutesHavePassed;
    	howManyMinutesHavePassed = Minutes.minutesBetween(this.getLastActiveAt(), new DateTime()).getMinutes();
    	return howManyMinutesHavePassed > MAX_INACTIVITY_TIME_IN_MINUTES;
    }
    
    
    public boolean equals(Session anotherSession){
    	boolean sameLastActive = this.getLastActiveAt().equals(anotherSession.getLastActiveAt());
    	boolean sameUser = this.getOwner().equals(anotherSession.getOwner());
    	boolean sameToken = anotherSession.hasToken(super.getToken());
    	
    	return sameLastActive && sameUser && sameToken;
    }
    
    public boolean hasToken(Long token){
    	return super.getToken().equals(token);
    }
    
    public void resetLastActive(){
    	this.setLastActiveAt(new DateTime());
    }
    
    @Override
    public String toString(){
    	String description = "\n\tSession Description: \n";
    	description += "\tOwner - " + this.getOwner().toString() + "\n";
    	description += "\tCurrentDir - " + this.getCurrentDir() + "\n";
    	description += "\tLast Active At - " + this.getLastActiveAt().toString() + "\n";
    	description += "\tIs expired - " + this.isExpired() + "\n";
    	return description;
    }
    
    @Override
    public Long getToken(){
    	throw new CannotListTokenException();
    }
}
