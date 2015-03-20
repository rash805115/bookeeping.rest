package bookeeping.rest.service.property;

import java.util.HashSet;
import java.util.Set;

public class Expect
{
	public static Set<String> expectProperties(String[] expectedProperties)
	{
		Set<String> expectProperties = new HashSet<String>();
		for(String expectedProperty : expectedProperties)
		{
			expectProperties.add(expectedProperty);
		}
		
		return expectProperties;
	}
}
