package pt.tecnico.myDrive.presentation;

import java.util.Optional;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.service.ListDirectoryService;

public class List extends MdCommand{
	private static final String DEFAULT_HELP = 
			"Prints all entries in the dir corresponding to the given path"
			+ "If the path is ommitted, prints all entries in the current dir.";

	public List(Shell shell, String name) {
		super(shell, name, DEFAULT_HELP);
	}

	public List(Shell shell, String name, String help) {
		super(shell, name, help);
	}

	@Override
	void execute(String[] args) {
		String argumentPath = this.validateArguments(args);
		Optional<String> maybePath = Optional.ofNullable(argumentPath);
		Long token = this.getTokenActiveSession();
		String pathToList = maybePath.orElse(this.getPathToCurrentDirOfSession());
		//TODO como invocar o servico com o pathToList?
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		//TODO imprimir os resultados
	}


	private String validateArguments(String[] args) {
		if (args.length > 1) {
			throw new RuntimeException("ChangeWorkingDirectory usage: " + name() + " <path to new working dir>");
		}
		
		if (args.length == 2) {
			String path = args[1];
			if (!super.isPathValid(path)) {
				throw new RuntimeException("ChangeWorkingDirectory path is not valid <" + path + " >");
			} else {
				return path;
			}
		} else {
			return null;
		}
	}
	
	private String getPathToCurrentDirOfSession() {
		Session session = super.getSessionByToken();
		Dir currentDir = session.getCurrentDir();
		return currentDir.getPath() +Dir.SLASH_NAME + currentDir.getName();
	}
}
