package pt.tecnico.myDrive.system;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;

import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.tecnico.myDrive.service.AddEnvironmentVariableService;
import pt.tecnico.myDrive.service.ChangeDirectoryService;
import pt.tecnico.myDrive.service.CreateFileService;
import pt.tecnico.myDrive.service.DeleteFileService;
import pt.tecnico.myDrive.service.ImportXMLService;
import pt.tecnico.myDrive.service.ListDirectoryService;
import pt.tecnico.myDrive.service.LoginUserService;
import pt.tecnico.myDrive.service.ReadFileService;
import pt.tecnico.myDrive.service.WriteFileService;
import pt.tecnico.myDrive.service.dto.FileDto;

@RunWith(JMockit.class)
public class IntegrationTest extends AbstractServiceTest {
	
	private static final String username = "jtb";
	private static final String password = "fernandes";
	private static final String jtbPath = "/home/jtb";
	private static final String filename = "plain";
	
	@Override
	protected void populate() {
		// TODO Auto-generated method stub
		
	}
	
	@Test
	public void testeIntegracaoSucesso() throws JDOMException, IOException{
		int size;
		ClassLoader loader = getClass().getClassLoader();
		File file = new File(loader.getResource("drive.xml").getFile());
		Document doc = (Document)new SAXBuilder().build(file);
		ImportXMLService service = new ImportXMLService(doc);
		service.execute();
		
		LoginUserService login = new LoginUserService(username, password);
		login.execute();
		
		CreateFileService fileService = new CreateFileService(login.getResult(), filename, "plainFile", "palavra" );
		fileService.execute();
		
		ReadFileService readFile = new ReadFileService(login.getResult(), filename);
		readFile.execute();
		
		assertEquals(readFile.getResult(), "palavra");
		
		WriteFileService writeFile = new WriteFileService(login.getResult(), jtbPath + "/plain" ,"nova palavra");
		writeFile.execute();
		
		readFile.execute();
		
		assertEquals(readFile.getResult(), "nova palavra");
		
		ListDirectoryService dirService = new ListDirectoryService(login.getResult(), jtbPath);
		dirService.execute();
		
		List<FileDto> listaDirectorio = dirService.result();
		size = listaDirectorio.size();
		
		for(FileDto dto : listaDirectorio)
			System.out.println(dto.toString());
		
		DeleteFileService deleteService = new DeleteFileService(login.getResult(), filename);
		deleteService.execute();
		
		ListDirectoryService newDirService = new ListDirectoryService(login.getResult(), jtbPath);
		newDirService.execute();
		
		List<FileDto> fileRemoved = newDirService.result();
		assertEquals(listaDirectorio.size()-1, fileRemoved.size());
		
		ChangeDirectoryService changeService = new ChangeDirectoryService(login.getResult(), jtbPath);
		changeService.execute();
		
		assertEquals(jtbPath, changeService.Result());
		
		AddEnvironmentVariableService aevs = new AddEnvironmentVariableService(login.getResult());
		aevs.execute();
		
		
	}

}
