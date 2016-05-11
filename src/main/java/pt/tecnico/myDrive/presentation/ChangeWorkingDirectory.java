package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.ChangeDirectoryService;

public class ChangeWorkingDirectory extends MdCommand{
	private static final String DEFAULT_HELP = "Changes the current working dir to the path given and prints the path of the new working dir.";
	private static final String DEFAULT_NAME = "cwd";
	private static final int MAX_ARGUMENTS = 1;
	
	public ChangeWorkingDirectory(Shell shell) {
		super(shell, DEFAULT_NAME, DEFAULT_HELP, MAX_ARGUMENTS);
	}

	public ChangeWorkingDirectory(Shell shell, String name, String help) {
		super(shell, name, help, MAX_ARGUMENTS);
	}

	@Override
	public void execute(String[] args) {
		this.checkArgumentsAreValid(args);
		
		executeService(args);
	}
	
	@Override
	protected void executeService(String[] args) {
		Long token = super.shell().getTokenActiveSession();
		if(args.length==0){
			ChangeDirectoryService service = new ChangeDirectoryService(token);
			service.execute();
			println(service.Result());
		}
		else{
		String pathNewCurrentDir = args[0];
		String newPath = this.changeWorkingDirectory(pathNewCurrentDir);
		println(newPath);
		}
	}
	
	@Override
	protected void checkArgumentsAreValid(String[] args) {
		if (args.length > MAX_ARGUMENTS) {
			throw new RuntimeException(this.name() + " usage: " + this.name() + " <path to new working dir>.");
		}
	}

	private String changeWorkingDirectory(String pathNewCurrentDir) {
		Long tokenActiveSession = super.shell().getTokenActiveSession();
		ChangeDirectoryService service = new ChangeDirectoryService(tokenActiveSession, pathNewCurrentDir);
		service.execute();
		return service.Result();
	}
	
	
}
