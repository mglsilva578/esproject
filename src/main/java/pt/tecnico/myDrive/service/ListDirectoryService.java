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
	private String path;
	private List<FileDto> filesInCurrentDir = new ArrayList<FileDto>();
	private List<File> files = new ArrayList<File>();

	public ListDirectoryService(Long token, String path){
		this.token = token;
		this.path = path;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		MyDrive drive = getMyDrive();
		Session session = drive.getLoginManager().getSessionByToken(this.token);
		Dir currentDir = session.getCurrentDir();
		User user = session.getOwner();
		if(path.length() == 0){
			files = currentDir.listDir(user);
			for(File file : files){
				filesInCurrentDir.add(new FileDto(file));
			}
		}
		else{
			Dir dir2List = (Dir) drive.getFileByPathname(path, false, user);
			files = dir2List.listDir(user);
			for(File file : files){
				filesInCurrentDir.add(new FileDto(file));
			}
		}
		Collections.sort(filesInCurrentDir);
	}

	public List<FileDto> result() {
		return filesInCurrentDir;
	}

}

