package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.ExceedsLimitPathException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidIdException;
import pt.tecnico.myDrive.exception.InvalidPermissionsException;

public class File extends File_Base {
	private static final int SLASH_DIR_0 = 0;
	protected static final String PATH_SEPARATOR = "/";
	private final int PERMISSION_LENGTH = 8;
	private static final String ACCEPTABLE_CHARACTERS_FOR_PERMISSION = "rwxd-";

	protected File(){
		super();
	}

	public File(MyDrive drive, User owner, String name, String permissions){
		this.init(drive, owner, name, permissions);
	}

	public File(MyDrive drive, User owner, String name, String permissions, Dir dir){
		this.init(drive, owner, name, permissions, dir);
	}

	protected void init(MyDrive drive, User owner, String name, String permissions){
		setId(drive.getNewFileId());
		setName(name);
		setLast_modification(new DateTime());
		setPermissions(this.filterPermissionsThroughUserMask(permissions, owner));
		this.setMydrive(drive);
		this.setOwner(owner);
	}

	protected void init(MyDrive drive, User owner, String name, String permissions, Dir dir){
		setId(drive.getNewFileId());
		setName(name);
		setLast_modification(new DateTime());
		setPermissions(this.filterPermissionsThroughUserMask(permissions, owner));
		this.setMydrive(drive);
		this.setOwner(owner);
		int size = dir.getPath().length() + dir.getName().length() + PATH_SEPARATOR.length() + name.length();
		if(size <= 1024){ 
			dir.addFile(this);
		}
		else{
			throw new ExceedsLimitPathException();
		}
	}

	protected void init(MyDrive drive, String id,  User owner, String name, String permissions, Dir dir, String lastModificationDate){
		int parsedId;
		try{
			parsedId = Integer.parseInt(id);
			setId(parsedId);
			setName(name);
			setLast_modification(new DateTime(lastModificationDate));
			setPermissions(this.filterPermissionsThroughUserMask(permissions, owner));
			this.setMydrive(drive);
			this.setOwner(owner);
			int size = dir.getPath().length() + dir.getName().length() + PATH_SEPARATOR.length() + name.length();
			if(size <= 1024){ 
				dir.addFile(this);
			}
			else{
				throw new ExceedsLimitPathException();
			}
		}catch(NumberFormatException nfe){
			throw new InvalidIdException(id);
		}
	}
	
	public  boolean hasPermissionsForRead(User u){
		String s = this.getPermissions();
		if (this.getOwner().equals(u))
			return s.charAt(0)=='r';
		else
			return s.charAt(4)=='r';
	}

	public boolean hasPermissionsForWrite(User u){
		String s = this.getPermissions();
		if (this.getOwner().equals(u))
			return s.charAt(1)=='w';
		else
			return s.charAt(5)=='w';
	}

	public boolean hasPermissionsForExecute(User u){
		String s = this.getPermissions();
		if (this.getOwner().equals(u))
			return s.charAt(2)=='x';
		else
			return s.charAt(6)=='x';
	}

	public boolean hasPermissionsForDelete(User u){
		String s = this.getPermissions();
		if (this.getOwner().equals(u))
			return s.charAt(3)=='d';
		else
			return s.charAt(7)=='d';
	}


	@Override
	public void setName(String n){
		if(!(this.getId() == SLASH_DIR_0 )){
			if(n.contains("/") || n.contains("\0")){
				throw new InvalidFileNameException(n);
			}
		}
		super.setName(n);
	}

	@Override
	public void setMydrive(MyDrive drive){
		if (drive == null){
			this.remove();
		}
		else{
			drive.addFile(this);
		}
	}

	@Override
	public void setOwner(User owner){
		if (owner == null){
			super.setOwner(null);
		}
		else{
			owner.addFile(this);
		}
	}

	public void remove(){
		super.setMydrive(null);
		this.getOwner().removeFile(this);
		setOwner(null);
		deleteDomainObject();
	}

	@Override
	public String toString(){
		String description = this.getPermissions();
		//To Do size
		description += " " + this.getOwner().getUsername();
		description += " " + this.getId();
		description += " " + this.getLast_modification();
		description += " " + this.getName();
		return description;
	}

	public Element exportXML() {
		Element element = new Element("File");
		element.setAttribute("id", this.getId().toString());
		Element elementPath = new Element("path");
		elementPath.setAttribute("path", this.getPath());
		element.addContent(elementPath);
		Element elementName = new Element("name");
		elementName.setAttribute("name", this.getName());
		element.addContent(elementName);
		Element elementOwner = new Element("owner");
		elementOwner.setAttribute("owner", this.getOwner().getUsername());
		element.addContent(elementOwner);
		Element elementPerm = new Element("perm");
		elementPerm.setAttribute("perm", this.getPermissions());
		element.addContent(elementPerm);
		Element elementLastModification = new Element("last_modification");
		elementLastModification.setAttribute("last_modification", this.getLast_modification().toString());
		element.addContent(elementLastModification);
		return element;
	}

	public String getPath(){
		if(this.getName().equals(Dir.SLASH_NAME)) return "";

		Dir parent = this.getFather();
		String path;
		if(directlyUnderSlashDir(parent)){
			path = PATH_SEPARATOR;
			return path;
		}else{
			path = "";
		}

		while(!directlyUnderSlashDir(parent)){
			path = PATH_SEPARATOR + parent.getName() + path;
			parent = parent.getFather();
		}
		return path;
	}

	private boolean directlyUnderSlashDir(Dir parent) {
		return parent.getName().equals(Dir.SLASH_NAME);
	}
	
	
	private String filterPermissionsThroughUserMask (String permissions, User owner){
		String ownerMask = owner.getMask ();
		
		this.checkValidityPermission (permissions);
		this.checkValidityPermission (ownerMask);
		
		char[] filteredPermissions = {'-', '-', '-', '-', '-', '-', '-', '-'};
		for (int index = 0; index < permissions.length (); index++){
			filteredPermissions [index] = filterPermission (permissions.charAt (index), ownerMask.charAt (index));
		}
		return new String(filteredPermissions);
	}
	
	private char filterPermission (char filePermission, char ownerPermission){
		return filePermission == ownerPermission ? filePermission : '-';
	}
	
	private void checkValidityPermission (String permission){
		if (!(this.isPermissionValid(permission))){
			throw new InvalidPermissionsException(permission);
		}
	}
	
	private boolean isPermissionValid (String permissionToValidate){
		boolean isNull = (permissionToValidate == null);
		boolean isComposedOfAcceptableCharacters = true;
		boolean hasRightLength = ((permissionToValidate.length () == this.PERMISSION_LENGTH));
		
		for (int i = 0; i < permissionToValidate.length (); i++){
			if (!(File.ACCEPTABLE_CHARACTERS_FOR_PERMISSION.contains("" + permissionToValidate.charAt (i)))){
				isComposedOfAcceptableCharacters = false;
			}
		}
		
		return !isNull && isComposedOfAcceptableCharacters && hasRightLength;
	}
}
