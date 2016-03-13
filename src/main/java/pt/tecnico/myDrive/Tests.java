package pt.tecnico.myDrive;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;

public class Tests {
	
	//Criar o ficheiro de texto /home/README com o conteudo lista de utilizadores
	public static void Teste01(String username,MyDrive md){
		String content = "utilizador1, utilizador2";
		String path = "/Home/README";
		String permissions = "rw";
		String name = "nome";
		User owner = md.getInstance().getUserByUsername(username);
		PlainFile pf = new PlainFile(md, owner, name, permissions, content);
		
	}
	//criar a directoria /usr/local/bin
	public static void Teste02(String username, MyDrive md){
		User owner = md.getInstance().getUserByUsername(username);
		String name = "nome";
		String permissions = "rw";
		Dir dir = new Dir(md, owner, name, permissions);
	}
	//imprimir o conteudo do ficheiro /home/README
	public static void Teste03(String filename,  MyDrive md){
		for(File file : md.getInstance().getFileSet()){
			if(filename.equals(file.getName()))
				System.out.println(file.toString());
		}
	}
	
	//Remover a diretoria /usr/local/bin
	public static void Teste04(){}
	//Imprimir a exportacao em XML do sistema de ficheiros
	public static void Teste05(){}
	//Remover o ficheiro /home/README
	public static void Teste06(){}
	//Imprimir a listagem simples da directoria /home
	public static void Teste07(){}
}
