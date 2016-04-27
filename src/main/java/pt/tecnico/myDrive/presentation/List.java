package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.ChangeDirectoryService;
import pt.tecnico.myDrive.service.ListDirectoryService;
import pt.tecnico.myDrive.service.dto.FileDto;

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
	public void execute(String[] args) {
		if (args.length == 0) {
			ListDirectoryService lds = new ListDirectoryService(null);
			lds.execute();
			for (FileDto s: lds.result())
				System.out.println(s);
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
}
