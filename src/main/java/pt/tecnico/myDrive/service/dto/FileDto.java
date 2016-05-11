package pt.tecnico.myDrive.service.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;

public class FileDto implements Comparable<FileDto> {
	static final Logger log = LogManager.getRootLogger();
	private String type;
	private String permissions;
	private String size = "";
	private String owner;
	private int id;
	private DateTime dateMod;
	private String name;
	private String content = "";

	public FileDto(File file) {
		this.type = file.getClass().getSimpleName();
		this.permissions = file.getPermissions();
		if(file.getClass().getSimpleName().equals("Dir")){
			size = Integer.toString(((Dir) file).getSize());
		}
		this.owner = file.getOwner().getUsername();
		this.id = file.getId();
		this.dateMod = file.getLast_modification();
		this.name = file.getName();
		if(type.equals("Link")){
			content = "->" + ((Link) file).getContent();
		}
	}

	public final String getType(){
		return this.type;
	}

	public final String getPermissions(){
		return this.permissions;
	}

	public final String getSize(){
		return this.size;
	}

	public final String getOwner(){
		return this.owner;
	}
	
	public final int getId(){
		return this.id;
	}

	public final DateTime getDateMod(){
		return this.dateMod;
	}

	public final String getName(){
		return this.name;
	}

	public final String getContent(){
		return this.content;
	}

	@Override
	public int compareTo(FileDto other) {
		return getName().compareTo(other.getName());
	}
	
	
	@Override
	public String toString() {
		String description = "";
		description += this.getType() + " ";
		description += this.getPermissions() + " ";
		description += this.getSize() + " ";
		description += this.getOwner() + " ";
		description += this.getId() + " ";
		description += this.getDateMod() + " ";
		description += this.getName() + " ";
		description += this.getContent() + " ";
		
		return description;
	}
}	
