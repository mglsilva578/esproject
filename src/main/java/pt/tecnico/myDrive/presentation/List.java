package pt.tecnico.myDrive.presentation;

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
	public void execute(String[] args) {
		if (args.length == 0) {
			//TODO acrescentei esta linha abaixo e tirei o null da invocação do serviço
			Long tokenActiveSession = super.shell().getTokenActiveSession();
			ListDirectoryService lds = new ListDirectoryService(tokenActiveSession);
			lds.execute();
			for (FileDto s: lds.result())
				log.trace(s.toString());
		} 
		else if(args.length == 1){
			//String actualCurrentDir = ... 
			//ChangeDirectoryService cds1 = new ChangeDirectoryService(TOKEN, args[0]);
			//ListDirectoryService lds = new ListDirectoryService(null);
			//ChangeDirectoryService cds2 = new ChangeDirectoryService(TOKEN, actualCurrentDir);
			//cds1.execute();
			//lds.execute();
			//for (FileDto s: lds.result()){
			//	System.out.println(s);
			//}
			//cds2.execute();
		} 
		else {
			throw new RuntimeException("USAGE: "+name()+" [<path>]");
		}
	}

	@Override
	protected void checkArgumentsAreValid(String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void executeService(String[] args) {
		// TODO Auto-generated method stub
		
	}	
}