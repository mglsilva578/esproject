package pt.tecnico.myDrive.exception;

public class InvalidFileNameException extends MyDriveException {

	private static final long serialVersionUID = 1L;

    private String name;

    public InvalidFileNameException(String n) {
        name = n;
    }

    public String getInvalidFileName() {
        return name;
    }

    @Override
    public String getMessage() {
        return "Invalid file name format: " + name;
    }
}
