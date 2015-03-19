package bookeeping.rest.exception;

@SuppressWarnings("serial")
public class MandatoryPropertyNotFound extends Exception
{
	public MandatoryPropertyNotFound()
	{
		super();
	}
	
	public MandatoryPropertyNotFound(String message)
	{
		super(message);
	}
	
	public MandatoryPropertyNotFound(String message, Throwable throwable)
	{
		super(message, throwable);
	}
	
	public MandatoryPropertyNotFound(Throwable throwable)
	{
		super(throwable);
	}
}