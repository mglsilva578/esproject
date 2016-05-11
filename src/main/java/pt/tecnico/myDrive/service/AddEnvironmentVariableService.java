package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.List;

import pt.tecnico.myDrive.domain.EnvironmentVariable;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.InvalidEnvironmentVariableNameException;
import pt.tecnico.myDrive.exception.InvalidEnvironmentVariableValueException;
import pt.tecnico.myDrive.exception.InvalidTokenException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.EnvironmentVariableDto;

public class AddEnvironmentVariableService extends MyDriveService{
	private Long token;
	private String environmentVariableName;
	private String environmentVariableValue;
	private List<EnvironmentVariableDto> existingEnvironmentVariables;
	private Session session;
	public static final String NO_VALUE_GIVEN = "novalue";
	public static final String NO_NAME_GIVEN = "noname";

	public AddEnvironmentVariableService(
			Long token,
			String environmentVariableName,
			String environmentVariableValue){
		this.token = token;
		this.environmentVariableName = environmentVariableName;
		this.environmentVariableValue = environmentVariableValue;
		this.existingEnvironmentVariables = new ArrayList<EnvironmentVariableDto>();

	}
	
	public AddEnvironmentVariableService(Long token) {
		this.token = token;
		this.environmentVariableName="";
		this.environmentVariableValue="";
		this.existingEnvironmentVariables=new ArrayList<EnvironmentVariableDto>();
	}

	@Override
	protected void dispatch() throws MyDriveException {
		session=getSession(token);
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
		if(!(token==null)&&environmentVariableName==""&&environmentVariableValue==""){
			return false;
		}
		if(!(token==null)&&environmentVariableValue==""){
			return false;
		}
		
		else
			return true;
	}
}
