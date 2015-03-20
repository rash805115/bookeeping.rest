package bookeeping.rest.response;

import java.util.HashMap;
import java.util.Map;

public class HttpCodes
{
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int ACCEPTED = 202;
	public static final int NOCONTENT = 204;
	public static final int RESETCONTENT = 205;
	public static final int PARTIALCONTENT = 206;
	
	public static final int MOVEDPERMANENTLY = 301;
	public static final int NOTMODIFIED = 304;
	
	public static final int BADREQUEST = 400;
	public static final int UNAUTHORIZED = 401;
	public static final int FORBIDDEN = 403;
	public static final int NOTFOUND = 404;
	public static final int METHODNOTALLOWED = 405;
	public static final int REQUESTTIMEOUT = 408;
	public static final int CONFLICT = 409;
	public static final int REQUESTENTITYTOOLARGE = 413;
	public static final int REQUESTURITOOLONG = 414;
	public static final int UNSUPPORTEDMEDIATYPE = 415;
	
	public static final int INTERNALSERVERERROR = 500;
	public static final int NOTIMPLEMENTED = 501;
	public static final int BADGATEWAY = 502;
	public static final int SERVICEUNAVAILABLE = 503;
	public static final int GATEWAYTIMEOUT = 504;
	
	public Map<Integer, String> codeResponse = new HashMap<Integer, String>();
	private static HttpCodes httpCodes = null;
	
	private HttpCodes()
	{
		this.codeResponse.put(HttpCodes.OK, "Success.");
		this.codeResponse.put(HttpCodes.CREATED, "The request has been fulfilled and resulted in a new resource being created.");
		this.codeResponse.put(HttpCodes.ACCEPTED, "The request has been accepted for processing, but the processing has not been completed. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place.");
		this.codeResponse.put(HttpCodes.NOCONTENT, "The server successfully processed the request, but is not returning any content.");
		this.codeResponse.put(HttpCodes.RESETCONTENT, "The server successfully processed the request, but is not returning any content. Unlike a 204 response, this response requires that the requester reset the document view.");
		this.codeResponse.put(HttpCodes.PARTIALCONTENT, "The server is delivering only part of the resource (byte serving) due to a range header sent by the client.");
		
		this.codeResponse.put(HttpCodes.MOVEDPERMANENTLY, "This and all future requests should be directed to another URI.");
		this.codeResponse.put(HttpCodes.NOTMODIFIED, "Indicates that the resource has not been modified since the version specified by the request headers If-Modified-Since or If-None-Match.");
		
		this.codeResponse.put(HttpCodes.BADREQUEST, "The server cannot or will not process the request due to something that is perceived to be a client error.");
		this.codeResponse.put(HttpCodes.UNAUTHORIZED, "Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been provided.");
		this.codeResponse.put(HttpCodes.FORBIDDEN, "The request was a valid request, but the server is refusing to respond to it. Unlike a 401 Unauthorized response, authenticating will make no difference.");
		this.codeResponse.put(HttpCodes.NOTFOUND, "The requested resource could not be found but may be available again in the future. Subsequent requests by the client are permissible.");
		this.codeResponse.put(HttpCodes.METHODNOTALLOWED, "A request was made of a resource using a request method not supported by that resource; for example, using GET on a form which requires data to be presented via POST, or using PUT on a read-only resource.");
		this.codeResponse.put(HttpCodes.REQUESTTIMEOUT, "The server timed out waiting for the request. According to HTTP specifications: \"The client did not produce a request within the time that the server was prepared to wait. The client MAY repeat the request without modifications at any later time.\"");
		this.codeResponse.put(HttpCodes.CONFLICT, "Indicates that the request could not be processed because of conflict in the request, such as an edit conflict in the case of multiple updates.");
		this.codeResponse.put(HttpCodes.REQUESTENTITYTOOLARGE, "The request is larger than the server is willing or able to process.");
		this.codeResponse.put(HttpCodes.REQUESTURITOOLONG, "The URI provided was too long for the server to process. Often the result of too much data being encoded as a query-string of a GET request, in which case it should be converted to a POST request.");
		this.codeResponse.put(HttpCodes.UNSUPPORTEDMEDIATYPE, "The request entity has a media type which the server or resource does not support. For example, the client uploads an image as image/svg+xml, but the server requires that images use a different format.");
		
		this.codeResponse.put(HttpCodes.INTERNALSERVERERROR, "A generic error message, given when an unexpected condition was encountered and no more specific message is suitable.");
		this.codeResponse.put(HttpCodes.NOTIMPLEMENTED, "The server either does not recognize the request method, or it lacks the ability to fulfil the request. Usually this implies future availability.");
		this.codeResponse.put(HttpCodes.BADGATEWAY, "The server was acting as a gateway or proxy and received an invalid response from the upstream server.");
		this.codeResponse.put(HttpCodes.SERVICEUNAVAILABLE, "The server is currently unavailable (because it is overloaded or down for maintenance). Generally, this is a temporary state.");
		this.codeResponse.put(HttpCodes.GATEWAYTIMEOUT, "The server was acting as a gateway or proxy and did not receive a timely response from the upstream server.");
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
