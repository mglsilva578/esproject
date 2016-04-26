package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.LoginUserService;

public class Login extends MdCommand{
	private static final String DEFAULT_HELP = "Logs the user with username / password pair provided as arguments.";
	private Long token;
	
	public Login(Shell shell, String name) {
		super(shell, name, DEFAULT_HELP);
	}

	public Login(Shell shell, String name, String help) {
		super(shell, name, help);
	}

	@Override
	void execute(String[] args) {
		this.validateArguments(args);
		this.token = this.executeService(args);
	}

	private void validateArguments(String[] args) {
		if (!(args.length == 2)) {
			throw new RuntimeException("Login usage: " + name() + " <username> <password>");
		}
		
		if (args[1] == null) {
			throw new RuntimeException("Login username cannot be null");
		}
		
		if (args[2] == null) {
			throw new RuntimeException("Login password cannot be null");
		}
	}
	
	private Long executeService(String[] args) {
		String username = args[1];
		String password = args[2];
		
		LoginUserService service = new LoginUserService(username, password);
		service.execute();
		return service.getResult();
	}
	
	public Long getToken() {
		return this.token;
	}
}
