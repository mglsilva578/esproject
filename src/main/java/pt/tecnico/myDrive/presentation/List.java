package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.service.ListDirectoryService;
import pt.tecnico.myDrive.service.dto.FileDto;

public class List extends MdCommand{
	private static final String DEFAULT_HELP = 
			"Prints all entries in the dir corresponding to the given path"
			+ "If the path is ommitted, prints all entries in the current dir.";
	private static final String DEFAULT_NAME = "ls";
	private static final int MAX_ARGUMENTS = 1;

	public List(Shell shell) {
		super(shell, DEFAULT_NAME, DEFAULT_HELP, MAX_ARGUMENTS);
	}

	public List(Shell shell, String name, String help) {
		super(shell, name, help, MAX_ARGUMENTS);
	}

	@Override
	void execute(String[] args) {
		this.checkArgumentsAreValid(args);
		
		this.executeService(args);
	}

	@Override
	protected void checkArgumentsAreValid(String[] args) {
		if (args.length > MAX_ARGUMENTS) {
			throw new RuntimeException(this.name() + " usage: " + this.name() + " <optional - path of dir to list>.");
		}
	}
	
	@Override
	protected void executeService(String[] args) {
		String pathToList;
		if (args.length == 0){
			pathToList = this.getPathToCurrentDirOfSession();
		} else {
			pathToList = args[0];
		}

		Long token = this.getTokenActiveSession();
		//TODO como invocar o servico com o pathToList?
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		//TODO imprimir os resultados
		java.util.List<FileDto> filesInDir = service.result();
		this.printResults(filesInDir);
	}
	
	private String getPathToCurrentDirOfSession() {
		Session session = super.getSessionByToken();
		Dir currentDir = session.getCurrentDir();
		//TODO confirmar se o path deverá ser assim como descrito abaixo.
		return currentDir.getPath() + Dir.SLASH_NAME + currentDir.getName();
	}
	
	private void printResults(java.util.List<FileDto> filesInDir) {
		String results = "";
		for (FileDto fileDto : filesInDir) {
			results += fileDto.toString();
		}
		log.trace(this.name() + "results : \n" + results);
	}
}
