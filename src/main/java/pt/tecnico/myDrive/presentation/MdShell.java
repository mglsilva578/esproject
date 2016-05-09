package pt.tecnico.myDrive.presentation;

public class MdShell extends Shell{

	public static void main(String[] args) throws Exception{
		MdShell sh = new MdShell();
		sh.execute();
	}

	public MdShell(){
		super("MyDrive");
		new ChangeWorkingDirectory(this);
		new Execute(this);
		new Key(this);
		new List(this);
		new Login(this);
		new Import(this);
	}
}
