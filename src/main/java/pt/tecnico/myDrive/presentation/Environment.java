package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.AddEnvironmentVariableService;
import pt.tecnico.myDrive.service.dto.EnvironmentVariableDto;

public class Environment extends MdCommand{
	private static final String DEFAULT_HELP = "Creates or changes the value of the environment variable with the given name.";
	private static final String DEFAULT_NAME = "env";
	private static final int MAX_ARGUMENTS = 2;
	

	public Environment(Shell shell){
		super(shell,DEFAULT_NAME,DEFAULT_HELP,MAX_ARGUMENTS);
	}

	public Environment(Shell shell, String name, String help){
		super(shell,name,help,MAX_ARGUMENTS);
	}

	@Override
	public void execute(String[] args) {
		this.checkArgumentsAreValid(args);
		this.executeService(args);
	}

	@Override
	protected void checkArgumentsAreValid(String[] args) {
		if ((args.length > MAX_ARGUMENTS)) {
			System.out.println("value "+args.length);
			throw new RuntimeException(this.name() + " usage: " + this.name() + " <path> <arguments>.");
		}
	}

	@Override
	protected void executeService(String[] args) {
		Long token = super.shell().getTokenActiveSession();
		if(args.length==2){
			String name = args[0];
			String value = args[1];			
			AddEnvironmentVariableService service = new AddEnvironmentVariableService(token, name, value);
			service.execute();
		}
		else{
			AddEnvironmentVariableService service = new AddEnvironmentVariableService(token);
			service.execute();
		}
		if(args.length==0){
			//imprime todas
			AddEnvironmentVariableService service = new AddEnvironmentVariableService(token);
			service.execute();
			for(EnvironmentVariableDto env :service.getResult()){
				println(env.toString());
			}
		}
		else if(args.length==1){
			//imprime a variavel com esse nome, fazer for do result e retornar a com nome
			String name = args[0];
			AddEnvironmentVariableService service = new AddEnvironmentVariableService(token);
			service.execute();
			for(EnvironmentVariableDto env:service.getResult()){
				if(env.getName()==name){
					println(env.toString());
				}
			}
		}
		
	}

}
