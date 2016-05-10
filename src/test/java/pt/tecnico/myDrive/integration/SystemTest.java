package pt.tecnico.myDrive.integration;

import org.junit.Test;

import pt.tecnico.myDrive.presentation.ChangeWorkingDirectory;
import pt.tecnico.myDrive.presentation.Environment;
import pt.tecnico.myDrive.presentation.Execute;
import pt.tecnico.myDrive.presentation.Import;
import pt.tecnico.myDrive.presentation.Key;
import pt.tecnico.myDrive.presentation.List;
import pt.tecnico.myDrive.presentation.Login;
import pt.tecnico.myDrive.presentation.MdShell;
import pt.tecnico.myDrive.service.AbstractServiceTest;


public class SystemTest extends AbstractServiceTest{


    private MdShell sh;

    protected void populate() {
        sh = new MdShell();
    }
    
    @Test
    public void success() {
    	new Import(sh).execute(new String[] { "drive.xml" } );
    	new Login(sh).execute(new String[]{ "jtb","fernandes"});
    	new ChangeWorkingDirectory(sh).execute(new String[]{"/home/jtb/bin"});
    	new List(sh).execute(new String[]{});
		new Execute(sh).execute(new String[]{});
		new Key(sh).execute(new String[]{});
		new Environment(sh).execute(new String[]{"name1","value1"});
    }


}
