package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.WriteFileService;

public class Write extends MdCommand{
	private static final String DEFAULT_HELP = "Write in PlainFiles";
	private static final String DEFAULT_NAME = "update";
	private static final int MAX_ARGUMENTS = 2;

	public Write(Shell shell) {
		super(shell, DEFAULT_NAME, DEFAULT_HELP, MAX_ARGUMENTS);
	}

	@Override
	public void execute(String[] args) {
		Long tokenActiveSession = super.shell().getTokenActiveSession();
		if(args.length == 2){
			WriteFileService wfs = new WriteFileService(tokenActiveSession, args[0] ,args[1]);
			wfs.execute();
		}
		else{
			throw new RuntimeException("USAGE: "+name()+" <path> <text>");
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
