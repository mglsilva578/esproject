package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.myDrive.domain.EnvironmentVariable;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.InvalidEnvironmentVariableNameException;
import pt.tecnico.myDrive.exception.InvalidEnvironmentVariableValueException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.EnvironmentVariableDto;

/* Add an environment variable or redefine an existing one with the same name. 
 * This service requires a token, the variable name and its value. 
 * The service returns a list of all defined environment variables.*/

public class AddEnvironmentVariableService extends MyDriveService{
	private Long token;
	private String environmentVariableName;
	private String environmentVariableValue;
	private List<EnvironmentVariableDto> existingEnvironmentVariables;
	private Session session;
	public static final String NO_VALUE_GIVEN = "no value";
	public static final String NO_NAME_GIVEN = "no name";
	
	public AddEnvironmentVariableService(
			Long token,
			String environmentVariableName,
			String environmentVariableValue){
		this.token = token;
		this.environmentVariableName = environmentVariableName;
		this.environmentVariableValue = environmentVariableValue;
		//this.session=getSession(token);
		this.existingEnvironmentVariables = new ArrayList<EnvironmentVariableDto>();

	}

	@Override
	protected void dispatch() throws MyDriveException {
		//TODO implement service logic.
		session=getSession(token);
		if(environmentVariableName.equals(NO_NAME_GIVEN)&&environmentVariableValue.equals(NO_VALUE_GIVEN)){
			for(EnvironmentVariableDto env:existingEnvironmentVariables){
				log.trace(env.toString());
			}
		}
		if(!environmentVariableName.equals(NO_NAME_GIVEN)&&environmentVariableValue.equals(NO_VALUE_GIVEN)){
			for(EnvironmentVariableDto env:existingEnvironmentVariables){
				if(env.getName().equals(environmentVariableName)){
					log.trace(env.toString());
				}
			}
		}
			
		if(checkArgs(token, environmentVariableName, environmentVariableValue)){
			EnvironmentVariable newenv = new EnvironmentVariable(session,environmentVariableName,environmentVariableValue);
			session.addEnvironmentVariables(newenv);
		}

		for(EnvironmentVariable env : session.getEnvironmentVariablesSet()){
			EnvironmentVariableDto newenvdto = new EnvironmentVariableDto(env.getName(), env.getValue());
			existingEnvironmentVariables.add(newenvdto);
		}
	}



	public List<EnvironmentVariableDto> getResult() {
		return existingEnvironmentVariables;
	}


	public Session getSession(Long token){
		Session session = getMyDrive().getLoginManager().getSessionByToken(this.token);
		return session;
	}

	public boolean checkArgs(Long token, String environmentVariableName,String environmentVariableValue){
		if(token==null){
			throw new InvalidTokenException(token);
		}
		if(environmentVariableName==null){
			throw new InvalidEnvironmentVariableNameException(environmentVariableName);
		}
		if(environmentVariableValue==null){
			throw new InvalidEnvironmentVariableValueException(environmentVariableValue);
		}
		else
			return true;
	}
}
