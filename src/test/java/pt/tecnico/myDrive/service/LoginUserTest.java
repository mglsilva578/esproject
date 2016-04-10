package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Test;

import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.CannotListTokenException;
import pt.tecnico.myDrive.exception.InvalidPasswordException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.UsernameDoesNotExistException;

public class LoginUserTest extends AbstractServiceTest{

	@Override
	protected void populate() {
		MyDrive myDrive = MyDrive.getInstance();
		new User(myDrive, "zttr", "76534", "Diogo", "rwxd----", null);
		new User(myDrive, "mglsilva578", "68230", "Miguel", "rwxd----", null);
		new User(myDrive, "R3Moura", "74005", "Ricardo", "rwxd----", null);
		new User(myDrive, "joseluisvf", "55816", "JoseLuis", "rwxd----", null);
		new User(myDrive, "ist176544", "76544", "Daniel", "rwxd----", null);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void createSessionForOneUser() {
		String username;
		String password;
		LoginUserService service;
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		int howManySessionsBefore = loginManager.getSessionsCount();
		
		username = "zttr";
		password = "76534";
		service = new LoginUserService(username, password);
		service.execute();
		
		int howManySessionsAfter = loginManager.getSessionsCount();
		assertEquals("Number of sessions before and after creation is not consistent.", howManySessionsBefore, howManySessionsAfter - 1);
		
		User sessionOwner = myDrive.getUserByUsername(username);
		Long token = service.getResult();
		Session session = loginManager.getSessionByToken(token);
		assertTrue("Session created is not consistent with credentials.",
				this.isSessionConsistentWithCredentials(session, sessionOwner, token));
	}

	@Test
	public void createSessionsForDifferentUsers() {
		String username, password;
		LoginUserService service;
		Long token;
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		int howManySessionsCreated = 0;
		int howManySessionsBefore = loginManager.getSessionsCount();
		
		username = "zttr";
		password = "76534";
		service = new LoginUserService(username, password);
		service.execute();
		token = service.getResult();
		User user = myDrive.getUserByUsername(username);
		Session session = loginManager.getSessionByToken(token);
		assertTrue("Session created is not consistent with credentials.",
				this.isSessionConsistentWithCredentials(session, user, token));
		howManySessionsCreated++;
		
		username = "mglsilva578";
		password = "68230";
		service = new LoginUserService(username, password);
		service.execute();
		token = service.getResult();
		user = myDrive.getUserByUsername(username);
		session = loginManager.getSessionByToken(token);
		assertTrue("Session created is not consistent with credentials.",
				this.isSessionConsistentWithCredentials(session, user, token));
		howManySessionsCreated++;
		
		username = "R3Moura";
		password = "74005";
		service = new LoginUserService(username, password);
		service.execute();
		token = service.getResult();
		user = myDrive.getUserByUsername(username);
		session = loginManager.getSessionByToken(token);
		assertTrue("Session created is not consistent with credentials.",
				this.isSessionConsistentWithCredentials(session, user, token));
		howManySessionsCreated++;
		
		username = "joseluisvf";
		password = "55816";
		service = new LoginUserService(username, password);
		service.execute();
		token = service.getResult();
		user = myDrive.getUserByUsername(username);
		session = loginManager.getSessionByToken(token);
		assertTrue("Session created is not consistent with credentials.",
				this.isSessionConsistentWithCredentials(session, user, token));
		howManySessionsCreated++;
		
		username = "ist176544";
		password = "76544";
		service = new LoginUserService(username, password);
		service.execute();
		token = service.getResult();
		user = myDrive.getUserByUsername(username);
		session = loginManager.getSessionByToken(token);
		assertTrue("Session created is not consistent with credentials.",
				this.isSessionConsistentWithCredentials(session, user, token));
		howManySessionsCreated++;
		
		// Confirm everything went well
		int howManySessionsAfter = loginManager.getSessionsCount();
		assertEquals("Number of sessions before and after creation is not consistent.", howManySessionsBefore, howManySessionsAfter - howManySessionsCreated);
	}
	
	@Test
	public void confirmLastActiveAtChangedAfterUsingSession(){
		String username = "joseluisvf";
		String password = "55816";
		Long token = null;
		long waitDurationInMillis = 5000;
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		
		LoginUserService service = new LoginUserService(username, password);
		service.execute();
		token = service.getResult();
		Session session = loginManager.getSessionByToken(token);

		DateTime lastActiveAtCreation = session.getLastActiveAt();
		try {
			Thread.sleep(waitDurationInMillis);
			loginManager.getSessionByToken(token);
			DateTime lastActiveAfterCreation = session.getLastActiveAt();
			int howManySecondsHavePassed = Seconds.secondsBetween(lastActiveAtCreation, lastActiveAfterCreation).getSeconds();
			assertEquals("Inconsistent number of seconds have passed.", howManySecondsHavePassed, waitDurationInMillis);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
			log.warn("Thread was interrupted for which reason the failure of this test cannot be accounted for.");
		}
	}
	
	@Test
	public void createTwoSessionsForSameUser(){
		String username = "joseluisvf";
		String password = "55816";
		Long token = null;
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		
		LoginUserService service = new LoginUserService(username, password);
		service.execute();
		token = service.getResult();
		Session firstSession = loginManager.getSessionByToken(token);
		
		service = new LoginUserService(username, password);
		service.execute();
		token = service.getResult();
		Session secondSession = loginManager.getSessionByToken(token);
		
		assertEquals("Owners should be the same but aren't.",firstSession.getOwner(), secondSession.getOwner());
	}
	
	@Test(expected = InvalidTokenException.class)
	public void useInvalidToken(){
		String username = "joseluisvf";
		String password = "55816";
		Long token = null;
		Long unexistentToken = new Long(1);
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		
		LoginUserService service = new LoginUserService(username, password);
		service.execute();
		token = service.getResult();

		while(unexistentToken == token){
			unexistentToken = new Long(new BigInteger(64, new Random()).longValue());
		}
		loginManager.getSessionByToken(unexistentToken);
	}
	
	@Test(expected = InvalidPasswordException.class)
	public void createSessionWithInvalidPassword(){
		String username = "joseluisvf";
		String password = "00000";
		
		LoginUserService service = new LoginUserService(username, password);
		service.execute();
	}
	
	@Test(expected = UsernameDoesNotExistException.class)
	public void createSessionWithInvalidUsername(){
		String username = "vfjoseluis";
		String password = "55816";
		
		LoginUserService service = new LoginUserService(username, password);
		service.execute();
	}
	
	@Test(expected = CannotListTokenException.class)
	public void tryToListTokens(){
		String username = "joseluisvf";
		String password = "55816";
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		
		LoginUserService service = new LoginUserService(username, password);
		service.execute();
		Long token = service.getResult();
		Session session = loginManager.getSessionByToken(token);
		User owner = session.getOwner();
		owner.getSessionsSet();
	}
	
	private boolean isSessionConsistentWithCredentials(Session sessionToCheck, User expectedOwner, Long expectedToken){
		boolean isSameUser = sessionToCheck.getOwner().equals(expectedOwner);
		boolean isSameToken = sessionToCheck.getToken().equals(expectedToken);
		
		return isSameUser && isSameToken;
	}
}
