package pt.tecnico.myDrive.service.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;

public class FileDto implements Comparable<FileDto> {
	static final Logger log = LogManager.getRootLogger();
	private String type;
	private String permissions;
	private int size;
	private String owner;
	private int id;
	private DateTime dateMod;
	private String name;


	public FileDto(String type, String permissions, int size, String owner, int id, DateTime dateMod, String name){
		this.type = type;
		this.permissions = permissions;
		this.size = size;
		this.owner = owner;
		this.id = id;
		this.dateMod = dateMod;
		this.name = name;
	}
	
	public FileDto(String type, String permissions, String owner, int id, DateTime dateMod, String name){
		this.type = type;
		this.permissions = permissions;
		this.owner = owner;
		this.id = id;
		this.dateMod = dateMod;
		this.name = name;
	}

	public final String getType(){
		return this.type;
	}

	public final String getPermissions(){
		return this.permissions;
	}

	public final int getSize(){
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

	@Override
	public int compareTo(FileDto other) {
		return getName().compareTo(other.getName());
	}
	
	
	public void toStringforDir(){
		log.trace(this.getType() + " " + this.getPermissions() + " " + this.getSize() + " " + 
				this.getOwner() + " " + this.getId() + " " + this.getDateMod() + " " + this.getName());
	}
	
	public void toStringforPlainFile(){
		log.trace(this.getType() + " " + this.getPermissions() + " " + this.getOwner() + " " + 
				this.getId() + " " + this.getDateMod() + " " + this.getName());
	}
}


