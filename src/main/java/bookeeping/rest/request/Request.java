package bookeeping.rest.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Request
{
	private JSONObject request;
	
	public Request(InputStream inputStream) throws JSONException
	{
		StringBuilder stringBuilder = new StringBuilder();
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while((line = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(line);
			}
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
		
		JSONObject userJsonObject = new JSONObject(stringBuilder.toString());
		this.request = new JSONObject();
		@SuppressWarnings("unchecked") Iterator<String> userJsonKeys = userJsonObject.keys();
		while(userJsonKeys.hasNext())
		{
			String key = userJsonKeys.next();
			this.request.put(key.toLowerCase(), userJsonObject.get(key));
		}
	}
	
	public String getRequestString()
	{
		return this.request.toString();
	}
	
	public Map<String, Object> getRequestMap()
	{
		Map<String, Object> requestMap = new HashMap<String, Object>();
		
		try
		{
			@SuppressWarnings("unchecked") Iterator<String> jsonKeys = this.request.keys();
			while(jsonKeys.hasNext())
			{
				String key = jsonKeys.next();
				requestMap.put(key, this.request.get(key));
			}
		}
		catch(JSONException jsonException)
		{
			jsonException.printStackTrace();
		}
		
		return requestMap;
		
	}
	
	public JSONObject getRequestObject()
	{
		return this.request;
	}
}
