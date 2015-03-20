package bookeeping.rest.request;

import java.util.HashSet;
import java.util.Set;

import bookeeping.rest.exception.MandatoryPropertyNotFound;

public class ValidateRequest
{
	public static Set<String> validateKeys(Set<String> expected, Set<String> received) throws MandatoryPropertyNotFound
	{
		for(String key : expected)
		{
			if(!received.contains(key))
			{
				throw new MandatoryPropertyNotFound("ERROR: Property \"" + key + "\" was not found!");
			}
		}
		
		return expected;
	}
	
	public static Set<String> validateOptionalKeys(Set<String> optional, Set<String> received)
	{
		Set<String> list = new HashSet<String>();
		for(String key : optional)
		{
			if(received.contains(key))
			{
				list.add(key);
			}
		}
		
		return list;
	}
}
