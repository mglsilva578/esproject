package pt.tecnico.myDrive.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;
import pt.tecnico.myDrive.domain.EnvironmentVariable;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidEnvironmentVariableNameException;
import pt.tecnico.myDrive.exception.InvalidEnvironmentVariableValueException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.service.dto.EnvironmentVariableDto;

public class AddEnvironmentVariableTest extends AbstractServiceTest{
	private MyDrive myDrive;
	private LoginManager loginManager;

	private final String existingUser1Name = "joseluisvf";
	private final String existingUser1Password = "some_password";
	private final String existingUser2Name = "mglsilva578";
	private final String existingUser2Password = "some_other_password";
	private Long existingUser1Token;
	private Long existingUser2Token;

	private final String existingUser1FirstVariableName = "$ES_PROJ";
	private final String existingUser1FirstVariableValue = "/home/projects/2016/es";
	private final String existingUser1SecondVariableName = "$TO_WATCH";
	private final String existingUser1SecondVariableValue = "/home/video/new_stuff";

	private final String firstNewEnvironmentVariableName = "$TO_LISTEN";
	private final String firstNewEnvironmentVariableValue = "/home/audio/new_stuff";
	private final String secondNewEnvironmentVariableName = "$TO_PROGRAM";
	private final String secondNewEnvironmentVariableValue = "/home/programming/new_stuff";

	private final String newValue = "/home/misc";

	@Override
	protected void populate() {
		this.myDrive = MyDrive.getInstance();
		this.loginManager = this.myDrive.getLoginManager();

		new User(this.myDrive, existingUser1Name, existingUser1Password, "jose luis", "rwxd----", null);
		new User(this.myDrive, existingUser2Name, existingUser2Password, "miguel silva", "rwxd----", null);

		this.loginUsers();
		this.addInitialEnvironmentVariablesToUser1();
	}

	private void loginUsers() {
		this.existingUser1Token = this.loginManager.createSession(existingUser1Name, existingUser1Password);
		this.existingUser2Token = this.loginManager.createSession(existingUser2Name, existingUser2Password);
	}

	private void addInitialEnvironmentVariablesToUser1() {
		Session user1Session = this.loginManager.getSessionByToken(existingUser1Token);
		this.addEnvironmentVariableToUser1(existingUser1Token, existingUser1FirstVariableName, existingUser1FirstVariableValue);
		this.addEnvironmentVariableToUser1(existingUser1Token, existingUser1SecondVariableName, existingUser1SecondVariableValue);
	}

	private void addEnvironmentVariableToUser1(
			Long existingUser1Token,
			String environmentVariableName,
			String environmentVariableValue) {
		//EnvironmentVariable toAdd = new EnvironmentVariable();
		//toAdd.setName(environmentVariableName);
		//toAdd.setValue(environmentVariableValue);
		//user1Session.addEnvironmentVariables(toAdd);
		AddEnvironmentVariableService service = new AddEnvironmentVariableService(existingUser1Token, environmentVariableName, environmentVariableValue);
		service.execute();
		

	}

	@Test(expected = InvalidTokenException.class)
	public void shouldThrowOnInvalidToken() {
		Long invalidToken = givenInvalidToken();
		whenTryToAddEnvironmentVariableUsingInvalidToken(invalidToken);
	}

	private Long givenInvalidToken() {
		Long invalidToken = new Long(new BigInteger(64, new Random()).longValue());

		while (invalidTokenIsNotSameAsExistingTokens(invalidToken)) {
			invalidToken = new Long(new BigInteger(64, new Random()).longValue());
		}

		return invalidToken;
	}

	private boolean invalidTokenIsNotSameAsExistingTokens(Long invalidToken) {
		return (invalidToken == this.existingUser1Token) ||
				(invalidToken == this.existingUser2Token);
	}

	private void whenTryToAddEnvironmentVariableUsingInvalidToken(Long invalidToken) {
		AddEnvironmentVariableService service = new AddEnvironmentVariableService(invalidToken, "name", "value");
		service.execute();
	}

	@Test(expected = InvalidTokenException.class)
	public void shouldThrowOnExpiredToken() {
		whenUser1TokenIsExpired();

		thenCantAddEnvironmentVariableToUser1();
	}

	private void whenUser1TokenIsExpired() {
		this.loginManager.makeSessionExpired(existingUser1Token);
	}

	private void thenCantAddEnvironmentVariableToUser1() {
		this.whenAddFirstEnvironmentVariable(existingUser1Token);
	}

	private List<EnvironmentVariableDto> whenAddFirstEnvironmentVariable(Long existingUser1Token) {
		AddEnvironmentVariableService service = new AddEnvironmentVariableService(existingUser1Token, firstNewEnvironmentVariableName, firstNewEnvironmentVariableValue);
		service.execute();
		return service.getResult();
	}

	@Test(expected = InvalidEnvironmentVariableNameException.class)
	public void shouldThrowOnNullEnvironmentVariableName() {
		this.whenTryToAddEnvironmentVariableWithNullName();
	}

	private void whenTryToAddEnvironmentVariableWithNullName() {
		AddEnvironmentVariableService service = new AddEnvironmentVariableService(existingUser1Token, null,firstNewEnvironmentVariableValue);
		service.execute();
	}

	@Test(expected = InvalidEnvironmentVariableValueException.class)
	public void shouldThrowOnNullEnvironmentVariableValue() {
		this.whenTryToAddEnvironmentVariableWithNullValue();
	}

	private void whenTryToAddEnvironmentVariableWithNullValue() {
		AddEnvironmentVariableService service = new AddEnvironmentVariableService(existingUser1Token, firstNewEnvironmentVariableName, null);
		service.execute();
	}

	@Test
	public void confirmCanAddOneVariable() {
		int howManyEnvironmentVariablesBefore = this.givenEnvironmentVariableCountBefore(existingUser1Token);
		List<EnvironmentVariableDto> existingEnvironmentVariables = this.whenAddFirstEnvironmentVariable(existingUser1Token);
		List<EnvironmentVariableDto> expectedEnvironmentVariables = new ArrayList<EnvironmentVariableDto>();
		EnvironmentVariableDto expected = new EnvironmentVariableDto(firstNewEnvironmentVariableName, firstNewEnvironmentVariableValue);
		expectedEnvironmentVariables.add(expected);
		this.thenServiceResultShouldBeAsExpected(existingEnvironmentVariables, expectedEnvironmentVariables, howManyEnvironmentVariablesBefore, 1);
	}

	private int givenEnvironmentVariableCountBefore(Long token) {
		Session session = this.loginManager.getSessionByToken(token);
		return session.getEnvironmentVariablesSet().size();
	}

	private void thenServiceResultShouldBeAsExpected(
			List<EnvironmentVariableDto> existingEnvironmentVariables,
			List<EnvironmentVariableDto> expectedEnvironmentVariables,
			int howManyEnvironmentVariablesBefore,
			int howManyEnvironmentVariablesAdded) {
		this.confirmServiceResultHasExpectedCount(existingEnvironmentVariables, howManyEnvironmentVariablesBefore, howManyEnvironmentVariablesAdded);
		this.confirmServiceResultHasExpectedValues(existingEnvironmentVariables, expectedEnvironmentVariables);
	}

	private void confirmServiceResultHasExpectedCount(
			List<EnvironmentVariableDto> existingEnvironmentVariables,
			int howManyEnvironmentVariablesBefore,
			int howManyAdded) {
		int howManyEnvironmentVariablesAfter = existingEnvironmentVariables.size();
		System.out.println("AFTER /n/n/n"+howManyEnvironmentVariablesAfter);
		System.out.println("BEFORE /n/n/n"+howManyEnvironmentVariablesBefore);
		
		assertTrue(howManyEnvironmentVariablesAfter == howManyEnvironmentVariablesBefore + howManyAdded);
	}

	private void confirmServiceResultHasExpectedValues(
			List<EnvironmentVariableDto> existingEnvironmentVariables,
			List<EnvironmentVariableDto> expectedEnvironmentVariables) {
		boolean allExistInServiceResult = false;
		for (EnvironmentVariableDto expectedEnvironmentVariable : expectedEnvironmentVariables) {
			allExistInServiceResult = existingEnvironmentVariables.contains(expectedEnvironmentVariable);
		}

		assertTrue(allExistInServiceResult);
	}

	@Test
	public void confirmCanAddMultipleVariables() {
		int howManyEnvironmentVariablesBefore = this.givenEnvironmentVariableCountBefore(existingUser1Token);
		List<EnvironmentVariableDto> existingEnvironmentVariables = this.whenAddBothEnvironmentVariables(existingUser1Token);

		List<EnvironmentVariableDto> expectedEnvironmentVariables = new ArrayList<>();
		EnvironmentVariableDto expected = new EnvironmentVariableDto(firstNewEnvironmentVariableName, firstNewEnvironmentVariableValue);
		expectedEnvironmentVariables.add(expected);
		expected = new EnvironmentVariableDto(secondNewEnvironmentVariableName, secondNewEnvironmentVariableValue);
		expectedEnvironmentVariables.add(expected);

		this.thenServiceResultShouldBeAsExpected(existingEnvironmentVariables, expectedEnvironmentVariables, howManyEnvironmentVariablesBefore, 2);
	}

	private List<EnvironmentVariableDto> whenAddBothEnvironmentVariables(Long existingUser1Token2) {
		AddEnvironmentVariableService service = new AddEnvironmentVariableService(existingUser1Token, firstNewEnvironmentVariableName, firstNewEnvironmentVariableValue);
		service.execute();

		service = new AddEnvironmentVariableService(existingUser1Token, secondNewEnvironmentVariableName, secondNewEnvironmentVariableValue);
		service.execute();

		return service.getResult();
	}

	@Test
	public void confirmCanOverrideValueOfExistingEnvironmentVariable() {
		String valueBefore = this.existingUser1FirstVariableValue;
		List<EnvironmentVariableDto> existingEnvironmentVariables = this.whenOverrideValueOfExistingEnvironmentVariable(this.existingUser1FirstVariableName);
		this.thenValueShouldBeOverridden(existingEnvironmentVariables, valueBefore);
	}
	
	private List<EnvironmentVariableDto> whenOverrideValueOfExistingEnvironmentVariable(String existingUser1FirstVariableName2) {
		AddEnvironmentVariableService service = new AddEnvironmentVariableService(existingUser1Token, existingUser1FirstVariableName, newValue);
		service.execute();
		return service.getResult();
	}

	private void thenValueShouldBeOverridden(List<EnvironmentVariableDto> existingEnvironmentVariables, String valueBefore) {
		EnvironmentVariableDto expectedEnvironmentVariable = new EnvironmentVariableDto(existingUser1FirstVariableName, newValue);
		assertTrue(existingEnvironmentVariables.contains(expectedEnvironmentVariable));
	}


}
