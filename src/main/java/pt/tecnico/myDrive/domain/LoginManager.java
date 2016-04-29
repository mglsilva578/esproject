package pt.tecnico.myDrive.domain;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.LoginManager_Base;
import pt.tecnico.myDrive.exception.CannotListTokenException;
import pt.tecnico.myDrive.exception.InvalidPasswordException;
import pt.tecnico.myDrive.exception.InvalidTokenException;

public class LoginManager extends LoginManager_Base {
	static final Logger log = LogManager.getRootLogger();
	
	private LoginManager() {
		super();
	}
	
	public static LoginManager getInstance(){
		MyDrive myDrive = FenixFramework.getDomainRoot().getMydrive();
		LoginManager loginManager = myDrive.getLoginManager();
		if( loginManager != null ){
			return loginManager;
		}else{
			return new LoginManager();
		}
	}
	

	public Long createSession(String username, String password){
		if(this.isPasswordCorrectForUsername(username, password)){
			Long token = this.generateUniqueToken();
			MyDrive myDrive = this.getMyDrive();
			User user = myDrive.getUserByUsername(username);
			Dir currentDir = (Dir)myDrive.getFileByPathname(user.getHomeDir(), false, null);
			//TODO a dar erro aqui quando vem do comando; não consegue criar a session
			Session session = new Session(user, token, currentDir);
			this.addSessions(session);
			this.removeInactiveSessions();
			return token;
		}else{
			throw new InvalidPasswordException(username, password);
		}
	}
	
	public void makeSessionExpired (Long token){
		Session sessionToExpire = this.getSessionByToken (token);
		sessionToExpire.makeExpired();
	}
	public Session getSessionByToken(Long token){
		for (Session session : super.getSessionsSet()) {
			if(!session.isExpired()){
				if(session.hasToken(token)){
					session.resetLastActive();
					return session;
				}
			}
		}
		log.warn("Warning : attempted to use non active token <" + token + ">");
		throw new InvalidTokenException(token);
	}
	
	@Override
	public int getSessionsCount(){
		return super.getSessionsSet().size();
	}
	
    @Override
    public Set<Session> getSessionsSet(){
    	throw new CannotListTokenException();
    }
	
    public boolean isDirInUseByAnySession(Dir dirToCheck) {
    	for (Session session : super.getSessionsSet()) {
    		Dir currentDir = session.getCurrentDir();
    		if (dirToCheck.equals(currentDir)) {
    			return true;
    		}
		}
    	return false;
    }
    
	private boolean isPasswordCorrectForUsername(String username, String password){
		if(username.equals(Nobody.USERNAME)) {
			return true;
		}
		MyDrive myDrive = this.getMyDrive();
		User user = myDrive.getUserByUsername(username);
		return user.getPassword().equals(password);
	}

	private Long generateUniqueToken(){
		Long token = generateToken();
		while(!this.isTokenUniqueAmongSessions(token)){
			token = generateToken();
		}
		return token;
	}

	private Long generateToken(){
		return new Long(new BigInteger(64, new Random()).longValue());
	}

	private boolean isTokenUniqueAmongSessions(Long token){
		for (Session session : super.getSessionsSet()) {
			if(session.hasToken(token)){
				return false;
			}
		}
		return true;
	}

	private void removeInactiveSessions(){
		CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<Session>(super.getSessionsSet());
		Iterator<Session> it = sessions.iterator();
		Session sessionToCheck = null;
		while(it.hasNext()){
			sessionToCheck = it.next();
			if(sessionToCheck.isExpired()){
				sessions.remove(sessionToCheck);
			}
		}
	}
}
