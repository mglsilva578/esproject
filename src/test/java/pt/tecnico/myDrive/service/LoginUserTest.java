package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.math.BigInteger;
import java.util.Random;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Nobody;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.CannotListTokenException;
import pt.tecnico.myDrive.exception.InvalidPasswordException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.UsernameDoesNotExistException;

public class LoginUserTest extends AbstractServiceTest{

	private static final String EXISTING_USERNAME1 = "zttr";
	private static final String EXISTING_USERNAME1_PASSWORD = "password1";
	private static final String EXISTING_USERNAME2 = "mglsilva578";
	private static final String EXISTING_USERNAME2_PASSWORD = "passwork2";
	private static final String EXISTING_USERNAME3 = "R3Moura";
	private static final String EXISTING_USERNAME3_PASSWORD = "password3";
	private static final String EXISTING_USERNAME4 = "joseluisvf";
	private static final String EXISTING_USERNAME4_PASSWORD = "password4";
	private static final String NON_EXISTING_USERNAME = "Someone Who Clearly Does Not Exist";
	
	private MyDrive myDrive;
	private LoginManager loginManager;
	
	private Long tokenUser1;
	private Long tokenUser2;
	private Long tokenUser3;

	@Override
	protected void populate() {
		this.myDrive = MyDrive.getInstance();
		this.loginManager = myDrive.getLoginManager();
		new User(this.myDrive, EXISTING_USERNAME1, EXISTING_USERNAME1_PASSWORD, "Diogo", "rwxd----", null);
		new User(this.myDrive, EXISTING_USERNAME2, EXISTING_USERNAME2_PASSWORD, "Miguel", "rwxd----", null);
		new User(this.myDrive, EXISTING_USERNAME3, EXISTING_USERNAME3_PASSWORD, "Ricardo", "rwxd----", null);
		new User(this.myDrive, EXISTING_USERNAME4, EXISTING_USERNAME4_PASSWORD, "JoseLuis", "rwxd----", null);
		new Nobody(this.myDrive);
		
		this.resetTokens();
	}
	
	private void resetTokens() {
		this.tokenUser1 = null;
		this.tokenUser2 = null;
		this.tokenUser3 = null;
	}

	@Test
	public void confirmCreatedSessionHasCorrectOwner() {
		whenSessionCreatedForUser1();
		
		thenSessionOwnerShouldBeUser1();
	}

	private void whenSessionCreatedForUser1() {
		LoginUserService service = new LoginUserService(EXISTING_USERNAME1, EXISTING_USERNAME1_PASSWORD);
		service.execute();
		this.tokenUser1 = service.getResult();
	}

	private void thenSessionOwnerShouldBeUser1() {
		Session session = this.loginManager.getSessionByToken(this.tokenUser1);
		User user1 = this.myDrive.getUserByUsername(EXISTING_USERNAME1);
		assertEquals(user1, session.getOwner());
		
	}
	
	@Test
	public void confirmCreatedSessionsAffectsSessionCount() {
		int sessionCountBefore = givenCurrentSessionCount();
		
		whenSessionsCreatedForDifferentUsers();
		
		thenSessionCountShouldHaveChanged(sessionCountBefore);
	}
	
	private int givenCurrentSessionCount() {
		return this.loginManager.getSessionsCount();
	}
	
	private void whenSessionsCreatedForDifferentUsers() {
		LoginUserService service = new LoginUserService(EXISTING_USERNAME1, EXISTING_USERNAME1_PASSWORD);
		service.execute();
		service = new LoginUserService(EXISTING_USERNAME2, EXISTING_USERNAME2_PASSWORD);
		service.execute();
	}
	
	private void thenSessionCountShouldHaveChanged(int sessionCountBefore) {
		assertEquals(sessionCountBefore + 2, givenCurrentSessionCount());
		
	}
	
	@Test
	public void confirmLastActiveAtChangedAfterUsingSession(){
		DateTime lastActiveAtBefore = givenLastActiveDateOfCreatedSession();
		
		whenSomeTimeHasPassed();
		
		thenCurrentLastActiveDateShouldHaveChanged(lastActiveAtBefore);
	}
	
	private DateTime givenLastActiveDateOfCreatedSession() {
		givenSessionCreatedUser3();
		Session session = this.loginManager.getSessionByToken(this.tokenUser3);
		return session.getLastActiveAt();
	}
	
	private void givenSessionCreatedUser3() {
		LoginUserService service = new LoginUserService(EXISTING_USERNAME3, EXISTING_USERNAME3_PASSWORD);
		service.execute();
		this.tokenUser3 = service.getResult();
	}

	private void whenSomeTimeHasPassed() {
		long someTimeInMilliseconds = 3000;
		try {
			Thread.sleep(someTimeInMilliseconds);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
			log.warn("Thread was interrupted for which reason the failure of this test cannot be accounted for.");
		}
	}
	
	private void thenCurrentLastActiveDateShouldHaveChanged(DateTime lastActiveAtBefore) {
		Session session = this.loginManager.getSessionByToken(this.tokenUser3);
		DateTime lastActiveAtAfter = session.getLastActiveAt();
		assertNotEquals(lastActiveAtBefore, lastActiveAtAfter);
	}
	
	@Test
	public void confirmCanCreateMoreThanOneSessionsForSameUser() {
		whenSessionsCreatedForSameUser();
		
		thenSessionsShouldHaveSameOwner();
	}
	

	private void whenSessionsCreatedForSameUser() {
		LoginUserService service = new LoginUserService(EXISTING_USERNAME1, EXISTING_USERNAME1_PASSWORD);
		service.execute();
		this.tokenUser1 = service.getResult();
		
		service = new LoginUserService(EXISTING_USERNAME1, EXISTING_USERNAME1_PASSWORD);
		service.execute();
		this.tokenUser2 = service.getResult();
	}
	
	private void thenSessionsShouldHaveSameOwner() {
		Session session1 = this.loginManager.getSessionByToken(tokenUser1);
		Session session2 = this.loginManager.getSessionByToken(tokenUser2);
		assertEquals(session1.getOwner(), session2.getOwner());
	}
	
	@Test(expected = InvalidTokenException.class)
	public void shouldThrowOnUsingInvalidToken(){
		givenSessionCreatedUser3();
		Long invalidToken = givenInvalidToken();
		
		whenTryToUseInvalidToken(invalidToken);
	}
	

	private Long givenInvalidToken() {
		Long invalidToken = new Long(new BigInteger(64, new Random()).longValue());
		
		while (invalidTokenIsNotSameAsExistingTokens(invalidToken)) {
			invalidToken = new Long(new BigInteger(64, new Random()).longValue());
		}
		
		return invalidToken;
	}

	private boolean invalidTokenIsNotSameAsExistingTokens(Long invalidToken) {
		return (invalidToken == this.tokenUser1) ||
				(invalidToken == this.tokenUser2) ||
				(invalidToken == this.tokenUser3);
	}
	
	private void whenTryToUseInvalidToken(Long invalidToken) {
		this.loginManager.getSessionByToken(invalidToken);
	}

	@Test(expected = InvalidPasswordException.class)
	public void shouldThrowOnCreateSessionWithInvalidPassword(){
		whenTryToCreateSessionWithInvalidPassword();
	}
	
	private void whenTryToCreateSessionWithInvalidPassword() {
		LoginUserService service = new LoginUserService(EXISTING_USERNAME1, EXISTING_USERNAME3_PASSWORD);
		service.execute();
	}
	
	@Test(expected = UsernameDoesNotExistException.class)
	public void shouldThrowOnCreateSessionWithInvalidUsername(){
		whenTryToCreateSessionWithInvalidUsername();
	}
	
	private void whenTryToCreateSessionWithInvalidUsername() {
		LoginUserService service = new LoginUserService(NON_EXISTING_USERNAME, EXISTING_USERNAME3_PASSWORD);
		service.execute();
	}
	
	@Test(expected = CannotListTokenException.class)
	public void shouldThrowOnTryToListTokens(){
		givenSessionCreatedUser3();

		whenTryToListTokensOfUser3();
	}
	
	private void whenTryToListTokensOfUser3() {
		Session session = this.loginManager.getSessionByToken(this.tokenUser3);
		User owner = session.getOwner();
		owner.getSessionsSet();
	}
	
	@Test(expected = InvalidTokenException.class)
	public void shouldThrowOnTryToUseExpiredSession (){
		givenSessionCreatedUser3();
		
		whenTryUseSessionAfterExpired();
	}

	private void whenTryUseSessionAfterExpired() {
		this.loginManager.makeSessionExpired(tokenUser3);
		this.loginManager.getSessionByToken(tokenUser3);
	}
	
	@Test
	public void shouldNotThrowOnTryToUseExpiredSessionForNobody (){
		givenSessionCreatedForNobody();
		
		whenTryUseSessionAfterExpired();
	}
	
	private void givenSessionCreatedForNobody() {
		LoginUserService service = new LoginUserService(Nobody.USERNAME, null);
		service.execute();
		this.tokenUser3 = service.getResult();
	}
}
