package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Session;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.exception.PermissionDeniedException;
import pt.tecnico.myDrive.service.dto.FileDto;

public class ListDirectoryService extends MyDriveService {
	private Long token;
	private String type;
	private List<FileDto> filesInCurrentDir = new ArrayList<FileDto>();

	public ListDirectoryService(Long token){
		this.token = token;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		MyDrive drive = getMyDrive();
		Session session = drive.getLoginManager().getSessionByToken(this.token);
		Dir currentDir = session.getCurrentDir();
		if(currentDir.hasPermissionsForRead(session.getOwner())){
			//System.out.println(session.getOwner().getMask() + session.getOwner().getUsername() + "\n\n" + currentDir.getPermissions() + currentDir.getOwner().getUsername());
			for(File file : currentDir.getFileSet()){
				if(file instanceof Dir){ 
					this.type = "dir";
					filesInCurrentDir.add(new FileDto(type, file.getPermissions(), ((Dir) file).getSize(), 
							file.getOwner().getUsername(), file.getId(), file.getLast_modification(), file.getName()));
				}
				if(file instanceof PlainFile){
					if(file instanceof Link){ 
						this.type = "link";
						String content = "->" + ((Link) file).getContent();
						filesInCurrentDir.add(new FileDto(type, file.getPermissions(), file.getOwner().getUsername(), 
								file.getId(), file.getLast_modification(), file.getName(), content));
					}
					else if(file instanceof App){
						this.type = "app";
						filesInCurrentDir.add(new FileDto(type, file.getPermissions(), file.getOwner().getUsername(), 
								file.getId(), file.getLast_modification(), file.getName()));
					}
					else{
						this.type = "plainFile";
						filesInCurrentDir.add(new FileDto(type, file.getPermissions(), file.getOwner().getUsername(), 
								file.getId(), file.getLast_modification(), file.getName()));
					}
				}
			}
		}
		else{
			throw new PermissionDeniedException(session.getOwner(), PermissionDeniedException.READ, currentDir);
		}
		Collections.sort(filesInCurrentDir);
	}
	
	public List<FileDto> result() {
		return filesInCurrentDir;
	}

}

