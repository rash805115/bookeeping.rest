package bookeeping.rest.response;

import java.util.HashMap;
import java.util.Map;

public class HttpCodes
{
	public static final int OK = 200;
	public static final int CREATED = 201;
	
	public static final int NOTMODIFIED = 304;
	
	public static final int BADREQUEST = 400;
	public static final int UNAUTHORIZED = 401;
	public static final int FORBIDDEN = 403;
	public static final int NOTFOUND = 404;
	public static final int CONFLICT = 409;
	
	public static final int INTERNALSERVERERROR = 500;
	
	public Map<Integer, String> codeResponse = new HashMap<Integer, String>();
	private static HttpCodes httpCodes = null;
	
	private HttpCodes()
	{
		this.codeResponse.put(HttpCodes.OK, "Success.");
		this.codeResponse.put(HttpCodes.CREATED, "The request has been fulfilled and resulted in a new resource being created.");
		
		this.codeResponse.put(HttpCodes.NOTMODIFIED, "The resource has not been modified since the version specified by the request headers If-Modified-Since or If-None-Match.");
		
		this.codeResponse.put(HttpCodes.BADREQUEST, "The server cannot or will not process the request because of a client error.");
		this.codeResponse.put(HttpCodes.UNAUTHORIZED, "Authentication failure.");
		this.codeResponse.put(HttpCodes.FORBIDDEN, "The requested resource is forbidden.");
		this.codeResponse.put(HttpCodes.NOTFOUND, "The requested resource could not be found.");
		this.codeResponse.put(HttpCodes.CONFLICT, "Request could not be processed because of conflict in the request.");
		
		
		this.codeResponse.put(HttpCodes.INTERNALSERVERERROR, "Server internal error.");
	}
	
	public static HttpCodes getInstance()
	{
		if(HttpCodes.httpCodes == null)
		{
			HttpCodes.httpCodes = new HttpCodes();
		}
		
		return HttpCodes.httpCodes;
	}
}
