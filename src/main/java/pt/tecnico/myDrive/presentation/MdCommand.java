package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;

public abstract class MdCommand extends Command {
	private static final int MAXIMUM_LENGTH_PATH = 1024;
	
	public MdCommand(Shell sh, String n){ 
		super(sh, n); 
	}

	public MdCommand(Shell sh, String n, String h){ 
		super(sh, n, h);
	}
	
	protected boolean isPathValid(String pathToCheck) {
		if (pathToCheck.length() < 2) {
			return false;
		} else {
			boolean startsWithSlash = Dir.SLASH_NAME.equals(""+pathToCheck.charAt(0));
			boolean hasValidLength = (pathToCheck.length() <= MAXIMUM_LENGTH_PATH);
			
			return startsWithSlash && hasValidLength;
		}
	}
	
	
	protected Session getSessionByToken() {
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		
		Long token = this.getTokenActiveSession();
		
		Session session = loginManager.getSessionByToken(token);
		return session;
	}
	
	protected Long getTokenActiveSession() {
		Shell shell = super.shell();
		return shell.getTokenActiveSession();
	}
}
