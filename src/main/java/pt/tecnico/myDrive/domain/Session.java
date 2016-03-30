package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import pt.tecnico.myDrive.domain.Session_Base;
import pt.tecnico.myDrive.exception.CannotSetLastActiveDateOfSession;

public class Session extends Session_Base {
    private static final int MAX_INACTIVITY_TIME_IN_MINUTES = 120;

	public Session(User user, Long token, Dir currentDir) {
        super();
        this.setOwner(user);
        this.setToken(token);
        this.setCurrentDir(currentDir);
        super.setLastActiveAt(new DateTime());
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
    
    protected void resetLastActive(){
    	super.setLastActiveAt(new DateTime());
    }
    
    @Override
    public void setLastActiveAt(DateTime newDate){
    	throw new CannotSetLastActiveDateOfSession();
    }
}
