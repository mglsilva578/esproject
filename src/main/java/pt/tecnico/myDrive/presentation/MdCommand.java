package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;

public abstract class MdCommand extends Command {
	private int maxNumberOfArguments;
	public MdCommand(Shell sh, String n, int maxNumberOfArguments){ 
		super(sh, n);
		this.maxNumberOfArguments = maxNumberOfArguments;
	}

	public MdCommand(Shell sh, String n, String h, int maxNumberOfArguments){ 
		super(sh, n, h);
		this.maxNumberOfArguments = maxNumberOfArguments;
	}
	
	public int getMaxNumberOfArguments() {
		return this.maxNumberOfArguments;
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

	protected abstract void checkArgumentsAreValid(String[] args);
	protected abstract void executeService(String[] args);
}
