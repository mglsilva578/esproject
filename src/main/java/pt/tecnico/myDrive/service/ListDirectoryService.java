package pt.tecnico.myDrive.service;

import java.util.List;



import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.FileDto;

public class ListDirectoryService extends MyDriveService {
	private Long token;
	private String type;
	private List<FileDto> filesInCurrentDir;
	
	public ListDirectoryService(Long token){
		this.token = token;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		MyDrive drive = getMyDrive();
		Session session = drive.getLoginManager().getSessionByToken(this.token);
		Dir currentDir = session.getCurrentDir();
		for(File file : currentDir.getFileSet()){
			if(file instanceof Dir){ 
				this.type = "dir";
				filesInCurrentDir.add(new FileDto(type, file.getPermissions(), ((Dir) file).getFileSet().size(), 
						file.getOwner().getName(), file.getLast_modification(), file.getName()));
			}
			if(file instanceof PlainFile){
				this.type = "plainFile";
				filesInCurrentDir.add(new FileDto(type, file.getPermissions(), ((PlainFile) file).getContent().length(), 
						file.getOwner().getName(), file.getLast_modification(), file.getName()));
			}
			if(file instanceof Link){ 
				this.type = "link";
				filesInCurrentDir.add(new FileDto(type, file.getPermissions(), ((Link) file).getContent().length(), 
						file.getOwner().getName(), file.getLast_modification(), file.getName()));
			}
			if(file instanceof App){
				this.type = "app";
				filesInCurrentDir.add(new FileDto(type, file.getPermissions(), ((App) file).getContent().length(), 
						file.getOwner().getName(), file.getLast_modification(), file.getName()));
			}
			
		}
	}
	public List<FileDto> result() {
		return filesInCurrentDir;
	}
	
}

