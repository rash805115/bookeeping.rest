package bookeeping.rest.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;

import bookeeping.rest.exception.MandatoryPropertyNotFound;
import bookeeping.rest.request.Request;
import bookeeping.rest.request.expect.UserProperty;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;
import bookeeping.rest.service.database.UserDatabaseService;

@Path("/user")
public class User
{
	@POST
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getUser(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			if(username == null) throw new MandatoryPropertyNotFound("ERROR: Missing property - \"username\"");
			
			return UserDatabaseService.getInstance().getUser(username).getResponseString();
		}
		catch(JSONException jsonException)
		{
			return response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", "ERROR: Malformed Json");
		}
		catch (MandatoryPropertyNotFound mandatoryPropertyNotFound)
		{
			return response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", mandatoryPropertyNotFound.getMessage());
		}
	}
	
	@POST
	@Path("/create")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createUser(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			if(username == null) throw new MandatoryPropertyNotFound("ERROR: Missing property - \"username\"");
			
			Map<String, Object> userProperties = new HashMap<String, Object>();
			String[] optionalProperties = {
				UserProperty.firstname.name(), UserProperty.lastname.name(), UserProperty.primaryemail.name(),
				UserProperty.secondaryemail.name(), UserProperty.phone.name()
			};
			for(String key : optionalProperties)
			{
				Object value = requestMap.get(key);
				if(value != null) userProperties.put(key, value);
			}
			
			return UserDatabaseService.getInstance().createUser(username, userProperties).getResponseString();
		}
		catch(JSONException jsonException)
		{
			return response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", "ERROR: Malformed Json");
		}
		catch (MandatoryPropertyNotFound mandatoryPropertyNotFound)
		{
			return response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", mandatoryPropertyNotFound.getMessage());
		}
	}
}
