package pt.tecnico.myDrive.presentation;

public class Execute extends MdCommand{
	private static final String DEFAULT_HELP = "Executes the file in the path given with the arguments provided.";
	private static final String DEFAULT_NAME = "do";
	private static final int MAX_ARGUMENTS = 2; //TODO confirmar que nao pode ser uma lsita de argumentos
	
	private Long token;
	
	public Execute(Shell shell) {
		super(shell, DEFAULT_NAME, DEFAULT_HELP, MAX_ARGUMENTS);
	}

	public Execute(Shell shell, String name, String help) {
		super(shell, name, help, MAX_ARGUMENTS);
	}
	
	public Long getToken() {
		return this.token;
	}

	@Override
	void execute(String[] args) {
		this.checkArgumentsAreValid(args);
		this.executeService(args);
	}

	@Override
	protected void checkArgumentsAreValid(String[] args) {
		if ((args.length != MAX_ARGUMENTS)) {
			throw new RuntimeException(this.name() + " usage: " + this.name() + " <path> <arguments>.");
		}
	}
	
	@Override
	protected void executeService(String[] args) {
		String username = args[1];
		String password = args[2];
		
		//TODO run service
	}
}
