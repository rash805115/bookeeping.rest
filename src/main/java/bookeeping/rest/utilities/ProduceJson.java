package bookeeping.rest.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ProduceJson
{
	public static JSONObject inputStreamToJson(InputStream inputStream) throws JSONException
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
		JSONObject convertedJsonObject = new JSONObject();
		@SuppressWarnings("unchecked") Iterator<String> userJsonKeys = userJsonObject.keys();
		while(userJsonKeys.hasNext())
		{
			String key = userJsonKeys.next();
			convertedJsonObject.put(key, userJsonObject.get(key));
		}
		
		return convertedJsonObject;
	}
}
