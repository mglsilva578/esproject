package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import pt.tecnico.myDrive.domain.Session_Base;
import pt.tecnico.myDrive.exception.CannotSetLastActiveDateOfSession;
import pt.tecnico.myDrive.exception.CannotSetOwnerOfSession;

public class Session extends Session_Base {

	public Session(User user, Long token, Dir currentDir) {
        super();
        super.setOwner(user);
        this.setToken(token);
        this.setCurrentDir(currentDir);
        super.setLastActiveAt(new DateTime());
    }
    
    public boolean isExpired(){
    	int howManyMinutesHavePassed;
    	howManyMinutesHavePassed = Minutes.minutesBetween(this.getLastActiveAt(), new DateTime()).getMinutes();
    	return howManyMinutesHavePassed > this.getMaxInactivityTimeInMinutes();
    }
    
    private int getMaxInactivityTimeInMinutes() {
    	return super.getOwner().getMaxInactivityTimeOfSession();
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
    public void setOwner(User newOwner) {
    	throw new CannotSetOwnerOfSession();
    }
    
    @Override
    public void setLastActiveAt(DateTime newDate){
    	throw new CannotSetLastActiveDateOfSession();
    }
    
    protected void makeExpired(){
    	int maxInactivityTimeInMinutes = this.getMaxInactivityTimeInMinutes();
    	DateTime lateDate = super.getLastActiveAt().minusMinutes(maxInactivityTimeInMinutes * 2);
    	super.setLastActiveAt(lateDate);
    }
}
