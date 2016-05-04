package pt.tecnico.myDrive.service.dto;

public class EnvironmentVariableDto {
	private String name;
	private String value;
	
	public EnvironmentVariableDto(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public String toString() {
		String description = "";
		//description += "Contents of Dto: \n";
		//description += "Name - " + this.name + "\n";
		//description += "Value - " + this.value;
		description +=this.name + "="+this.value+"\n";
		return description;
	}
	
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object toCompare) {
		if (!(toCompare instanceof EnvironmentVariableDto)) {
			return false;
		}
		
		EnvironmentVariableDto toCompareDto = (EnvironmentVariableDto) toCompare;
		String anotherName = toCompareDto.getName();
		String anotherValue = toCompareDto.getValue();
		
		return this.name.equals(anotherName) &&
				this.value.equals(anotherValue);
	}

	public void setValue(String environmentVariableValue) {
		this.value=environmentVariableValue;
		
	}
}
