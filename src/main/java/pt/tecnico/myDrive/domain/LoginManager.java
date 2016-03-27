package pt.tecnico.myDrive.domain;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.exception.CannotListSessionsException;
import pt.tecnico.myDrive.exception.InvalidPasswordException;
import pt.tecnico.myDrive.exception.TokenDoesNotExistException;

public class LoginManager extends LoginManager_Base {
	static final Logger log = LogManager.getRootLogger();
	
	public LoginManager() {
		super();
	}

	public void createSession(String username, String password){
		if(this.isPasswordCorrectForUsername(username, password)){
			Long token = this.generateUniqueToken();
			MyDrive myDrive = this.getMyDrive();
			User user = myDrive.getUserByUsername(username);
			Dir currentDir = (Dir)myDrive.getFileByPathname(user.getHomeDir() + Dir.SLASH_NAME + user.getUsername(), false, null);
			Session session = new Session(user, token, currentDir);
			
			this.addSessions(session);
			this.removeInactiveSessions();
		}else{
			throw new InvalidPasswordException(username, password);
		}
	}
	
	public void useSession(Long token){
		try{
			Session session = this.getSessionByToken(token);
			session.resetLastActive();
		}catch(TokenDoesNotExistException tdnee){
			log.warn("Warning : attempted to use non active token <" + token + ">");
		}
	}

	@Override
	public Set<Session> getSessionsSet(){
		throw new CannotListSessionsException();
	}

	@Override
	public String toString(){
		String description = "Sessions : \n";
		for (Session session : super.getSessionsSet()) {
			description += "\t" + session.toString();
		}
		return description;
	}
	
	private boolean isPasswordCorrectForUsername(String username, String password){
		MyDrive myDrive = this.getMyDrive();
		User user = myDrive.getUserByUsername(username);
		return user.getPassword().equals(password);
	}

	private Long generateUniqueToken(){
		Long token = generateToken();
		// while token is not unique
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
			if(token.equals(session.getToken())){
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

	private Session getSessionByToken(Long token){
		for (Session session : super.getSessionsSet()) {
			if(!session.isExpired()){
				if(session.hasToken(token)){
					return session;
				}
			}
		}
		throw new TokenDoesNotExistException(token);
	}
}
