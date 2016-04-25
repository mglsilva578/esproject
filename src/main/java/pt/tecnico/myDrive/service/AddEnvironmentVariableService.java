package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.List;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.EnvironmentVariableDto;

public class AddEnvironmentVariableService extends MyDriveService{
	private Long token;
	private String environmentVariableName;
	private String environmentVariableValue;
	private List<EnvironmentVariableDto> existingEnvironmentVariables;
	
	public AddEnvironmentVariableService(
			Long token,
			String environmentVariableName,
			String environmentVariableValue){
		this.token = token;
		this.environmentVariableName = environmentVariableName;
		this.environmentVariableValue = environmentVariableValue;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		//TODO implement service logic.
		this.existingEnvironmentVariables = new ArrayList<EnvironmentVariableDto>();
	}
	
	public List<EnvironmentVariableDto> getResult() {
		return this.existingEnvironmentVariables;
	}
}
