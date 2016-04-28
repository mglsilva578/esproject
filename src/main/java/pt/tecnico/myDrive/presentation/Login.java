package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.LoginUserService;

public class Login extends MdCommand{
	private static final String DEFAULT_HELP = "Logs the user with username / password pair provided as arguments.";
	private static final String DEFAULT_NAME = "login";
	private static final int MAX_ARGUMENTS = 2;
	
	private String username;
	private Long token;
	
	
	public Login(Shell shell) {
		super(shell, DEFAULT_NAME, DEFAULT_HELP, MAX_ARGUMENTS);
	}

	public Login(Shell shell, String name, String help) {
		super(shell, name, help, MAX_ARGUMENTS);
	}

	@Override
	void execute(String[] args) {
		this.checkArgumentsAreValid(args);
		this.executeService(args);
		this.updateShellWithNewLogin(this.username, this.token);
	}

	@Override
	protected void checkArgumentsAreValid(String[] args) {
		if ((args.length != MAX_ARGUMENTS)) {
			throw new RuntimeException(this.name() + " usage: " + this.name() + " <username> <password>.");
		}
	}
	
	@Override
	protected void executeService(String[] args) {
		String username = args[0];
		String password = args[1];
		
		LoginUserService service = new LoginUserService(username, password);
		service.execute();
		
		this.username = username;
		this.token = service.getResult();
	}
	
	public void updateShellWithNewLogin(String username, Long token) {
		Shell shell = this.shell();
		shell.addNewToken(username, token);
		shell.setUsernameActiveSession(username);
		shell.setTokenActiveSession(token);
	}
}
