package bookeeping.rest.exception;

@SuppressWarnings("serial")
public class InvalidFilePermission extends Exception
{
	public InvalidFilePermission()
	{
		super();
	}
	
	public InvalidFilePermission(String message)
	{
		super(message);
	}
	
	public InvalidFilePermission(String message, Throwable throwable)
	{
		super(message, throwable);
	}
	
	public InvalidFilePermission(Throwable throwable)
	{
		super(throwable);
	}
}