package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.ExecuteFileService;

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
	public 	void execute(String[] args) {
		this.checkArgumentsAreValid(args);
		this.executeService(args);
	}

	@Override
	protected void checkArgumentsAreValid(String[] args) {
		/*if ((args.length != MAX_ARGUMENTS)) {
			throw new RuntimeException(this.name() + " usage: " + this.name() + " <path> <arguments>.");
		}*/
	}
	
	@Override
	protected void executeService(String[] args) {
		String path = args[0];
		int i = 1;
		String[] newArgs = new String[args.length - 1];
		token = super.shell().getTokenActiveSession();
	
		while(i <= args.length - 2){
			newArgs[i-1] = args[i];
			i++;
		}
		
		ExecuteFileService service = new ExecuteFileService(token, path, newArgs);
		service.execute();
	}
}
