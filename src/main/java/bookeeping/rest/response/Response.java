package bookeeping.rest.response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Response
{
	private JSONObject response;
	private HttpCodes httpCodes;
	
	public Response()
	{
		this.response = new JSONObject();
		this.httpCodes = HttpCodes.getInstance();
	}
	
	public String getResponseString()
	{
		return this.response.toString();
	}
	
	public JSONObject getResponseObject()
	{
		return this.response;
	}
	
	public void addStatusCode(int statusCode)
	{
		try
		{
			this.response.put("status_code", statusCode);
			this.response.put("status_message", this.httpCodes.codeResponse.get(statusCode));
		}
		catch (JSONException jsonException)
		{
			jsonException.printStackTrace();
		}
	}
	
	public void addOperationResult(String operationResult, String operationResultMessage)
	{
		try
		{
			this.response.put("operation_code", operationResult);
			this.response.put("operation_message", operationResultMessage);
		}
		catch (JSONException jsonException)
		{
			jsonException.printStackTrace();
		}
	}
	
	public void addData(Object data)
	{
		try
		{
			this.response.put("data", data);
		}
		catch (JSONException jsonException)
		{
			jsonException.printStackTrace();
		}
	}
}
