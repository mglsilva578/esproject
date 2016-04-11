package pt.tecnico.myDrive.service.dto;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Dir;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;

public class FileDto implements Comparable<FileDto> {
	private String type;
	private String permissions;
	private int size;
	private String owner;
	private DateTime dateMod;
	private String name;


	public FileDto(String type, String permissions, int size, String owner, DateTime dateMod, String name){
		this.type = type;
		this.permissions = permissions;
		this.size = size;
		this.owner = owner;
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
}



