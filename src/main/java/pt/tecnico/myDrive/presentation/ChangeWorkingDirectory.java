package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.ChangeDirectoryService;

public class ChangeWorkingDirectory extends MdCommand{
	private static final String DEFAULT_HELP = "Changes the current working dir to the path given and prints the path of the new working dir.";

	public ChangeWorkingDirectory(Shell shell, String name) {
		super(shell, name, DEFAULT_HELP);
	}

	public ChangeWorkingDirectory(Shell shell, String name, String help) {
		super(shell, name, help);
	}

	@Override
	void execute(String[] args) {
		String pathNewCurrentDir = this.validateArguments(args);
		String newPath = this.changeWorkingDirectory(pathNewCurrentDir);
		printPathToNewWorkingDir(newPath);
	}

	private String validateArguments(String[] args) {
		if (args.length < 1) {
			throw new RuntimeException("ChangeWorkingDirectory usage: " + name() + " <path to new working dir>");
		}
		
		String path = args[0];
		if (!super.isPathValid(path)) {
			throw new RuntimeException("ChangeWorkingDirectory path is not valid <" + path + " >");
		} else {
			return path;
		}
	}

	private String changeWorkingDirectory(String pathNewCurrentDir) {
		Long tokenActiveSession = super.shell().getTokenActiveSession();
		ChangeDirectoryService service = new ChangeDirectoryService(tokenActiveSession, pathNewCurrentDir);
		service.execute();
		return service.Result();
	}
	
	private void printPathToNewWorkingDir(String pathNewCurrentDir) {
		log.info("ChangeWorkingDirectory - changed working dir to path <" + pathNewCurrentDir + ">");
	}
}
