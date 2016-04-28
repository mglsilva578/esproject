package pt.tecnico.myDrive.presentation;

public class Key extends MdCommand{
	private static final String DEFAULT_HELP = 
			"Changes the active user to the user associated with the username given and prints his token."
			+ "\nIf username is ommitted, the active username / token are printed instead.";
	private static final String DEFAULT_NAME = "token";
	private static final int MAX_ARGUMENTS = 1;
	
	public Key(Shell shell) {
		super(shell, DEFAULT_NAME, DEFAULT_HELP, MAX_ARGUMENTS);
	}

	public Key(Shell shell, String name, String help) {
		super(shell, name, help, MAX_ARGUMENTS);
	}

	@Override
	void execute(String[] args) {
		this.checkArgumentsAreValid(args);
		this.executeService(args);
	}

	@Override
	protected void checkArgumentsAreValid(String[] args) {
		if ((args.length > MAX_ARGUMENTS)) {
			throw new RuntimeException(this.name() + " usage: " + this.name() + " <username>.");
		}
	}
	
	@Override
	protected void executeService(String[] args) {
		if (args.length == 1) {
			String username = args[0];
			Long activeToken = this.updateShellChangeActiveUser(username);
			this.printActiveToken(activeToken);
		} else {
			this.printActiveUsernameAndToken();
		}
	}

	private Long updateShellChangeActiveUser(String newActiveUsername) {
		Shell shell = this.shell();
		shell.changeActiveUserTo(newActiveUsername);
		return shell.getTokenActiveSession();
	}
	
	private void printActiveToken(Long activeToken) {
		super.print(this.name() + " - current active token: \n" + activeToken);
	}
	
	private void printActiveUsernameAndToken() {
		Shell shell = this.shell();
		String activeUsername = shell.getUsernameActiveSession();
		Long activeToken = shell.getTokenActiveSession();
		
		String result = 
				this.name() +
				" - current active username / token: \n" + 
				activeUsername 
				+
				" / " +
				activeToken;
		
		super.print(result);
	}
}
