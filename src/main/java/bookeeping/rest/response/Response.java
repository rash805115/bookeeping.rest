package bookeeping.rest.response;

import java.util.Map;
import java.util.Map.Entry;

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
	
	public String addStatusCode(int statusCode)
	{
		try
		{
			this.response.put("status_code", statusCode);
			this.response.put("status_message", this.httpCodes.codeResponse.get(statusCode));
			return this.getResponseString();
		}
		catch (JSONException jsonException)
		{
			jsonException.printStackTrace();
			return null;
		}
	}
	
	public String addOperationResult(String operationResult, String operationResultMessage)
	{
		try
		{
			this.response.put("operation_code", operationResult);
			this.response.put("operation_message", operationResultMessage);
			return this.getResponseString();
		}
		catch (JSONException jsonException)
		{
			jsonException.printStackTrace();
			return null;
		}
	}
	
	public String addStatusAndOperation(int statusCode, String operationResult, String operationResultMessage)
	{
		this.addStatusCode(statusCode);
		this.addOperationResult(operationResult, operationResultMessage);
		return this.getResponseString();
	}
	
	public String addData(Map<String, Object> data)
	{
		try
		{
			JSONObject responseData = new JSONObject();
			for(Entry<String, Object> entry : data.entrySet())
			{
				responseData.put(entry.getKey(), entry.getValue());
			}
			
			this.response.put("data", responseData);
			return this.getResponseString();
		}
		catch (JSONException jsonException)
		{
			jsonException.printStackTrace();
			return null;
		}
	}
	
	public String addData(JSONObject data)
	{
		try
		{
			this.response.put("data", data);
			return this.getResponseString();
		}
		catch (JSONException jsonException)
		{
			jsonException.printStackTrace();
			return null;
		}
	}
}
