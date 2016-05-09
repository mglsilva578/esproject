package pt.tecnico.myDrive.presentation;

import java.io.File;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import pt.tecnico.myDrive.service.ImportXMLService;


public class Import extends MdCommand {

	private static final String DEFAULT_HELP = "import phonebook contacts. (use ./locaFile or resourceFile)";
	private static final String DEFAULT_NAME = "import";
	private static final int MAX_ARGUMENTS = 1;

	public Import(Shell shell){ 
		super(shell, DEFAULT_NAME, DEFAULT_HELP, MAX_ARGUMENTS);
	}

	public void execute(String[] args) {
		if (args.length == 1){
			try {
				SAXBuilder builder = new SAXBuilder();
				File file;
				if(args[0].startsWith(".")){
					file = new File(args[0]);
				}
				else{
					file = resourceFile(args[0]);
				}
				Document doc = (Document)builder.build(file);
				new ImportXMLService(doc).execute();
			} 
			catch (Exception e){ 
				throw new RuntimeException(e);
			}
		}
		else {
			throw new RuntimeException("USAGE: "+name()+" filename");
		}
	}

	public File resourceFile(String filename) {
		log.trace("Resource: "+filename);
		ClassLoader classLoader = getClass().getClassLoader();
		if(classLoader.getResource(filename) == null) return null;
		return new java.io.File(classLoader.getResource(filename).getFile());
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
