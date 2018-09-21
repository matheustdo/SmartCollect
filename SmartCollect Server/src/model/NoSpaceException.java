package model;

public class NoSpaceException extends Throwable {
	
	private static final long serialVersionUID = -8009439775398514498L;
	
	private final String code;
	
	public NoSpaceException() {
		super();
		this.code = "-1";
	}
	
	public NoSpaceException(String code) {
		super();
		this.code = code;
	}
	
	public NoSpaceException(String message, Throwable cause, String code) {
		super(message, cause);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
