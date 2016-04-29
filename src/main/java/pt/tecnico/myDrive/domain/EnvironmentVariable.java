package pt.tecnico.myDrive.domain;

public class EnvironmentVariable extends EnvironmentVariable_Base {
    
    public EnvironmentVariable() {
        super();
    }
    
    public EnvironmentVariable(Session session, String name, String value ){
    	setName(name);
    	setSession(session);
    	setValue(value);
    }
}
