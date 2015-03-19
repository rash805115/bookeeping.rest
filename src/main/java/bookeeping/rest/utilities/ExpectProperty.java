package bookeeping.rest.utilities;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import bookeeping.rest.exception.MandatoryPropertyNotFound;

public class ExpectProperty
{
	public static Map<String, Object> expectProperty(JSONObject jsonObject, String[] expectedProperties, String[] optionalProperties) throws JSONException, MandatoryPropertyNotFound
	{
		Map<String, Object> mandatoryProperties = new HashMap<String, Object>();
		
		for(String requiredKey : expectedProperties)
		{
			if(jsonObject.has(requiredKey))
			{
				mandatoryProperties.put(requiredKey, jsonObject.get(requiredKey));
			}
			else
			{
				throw new MandatoryPropertyNotFound("ERROR: Property \"" + requiredKey + "\" was not found!");
			}
		}
		
		for(String optionalKey : optionalProperties)
		{
			if(jsonObject.has(optionalKey))
			{
				mandatoryProperties.put(optionalKey, jsonObject.get(optionalKey));
			}
		}
		
		return mandatoryProperties;
	}
}
