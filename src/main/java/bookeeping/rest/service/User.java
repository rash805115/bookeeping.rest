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
import org.codehaus.jettison.json.JSONObject;

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
	@Path("/create")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createNewUser(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			JSONObject requestJson = request.getRequestObject();
			
			String userId = null;
			try
			{
				userId = (String) requestJson.get(UserProperty.userid.name());
			}
			catch(JSONException | ClassCastException e)
			{
				throw new MandatoryPropertyNotFound("ERROR: Required property - \"userId(String)\"");
			}
			
			Map<String, Object> userProperties = new HashMap<String, Object>();
			String[] optionalProperties = {
				UserProperty.firstname.name(), UserProperty.lastname.name(), UserProperty.primaryemail.name(),
				UserProperty.secondaryemail.name(), UserProperty.phone.name()
			};
			for(String key : optionalProperties)
			{
				try
				{
					String value = (String) requestJson.get(key);
					userProperties.put(key, value);
				}
				catch(JSONException jsonException) {}
				catch(ClassCastException e)
				{
					throw new ClassCastException("ERROR: Optional property must be string.");
				}
			}
			
			return new UserDatabaseService().createNewUser(userId, userProperties).getResponseString();
		}
		catch(JSONException jsonException)
		{
			return response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", "ERROR: Malformed Json");
		}
		catch (MandatoryPropertyNotFound mandatoryPropertyNotFound)
		{
			return response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", mandatoryPropertyNotFound.getMessage());
		}
		catch(ClassCastException classCastException)
		{
			return response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", classCastException.getMessage());
		}
	}
	
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
			JSONObject requestJson = request.getRequestObject();
			
			String userId = null;
			try
			{
				userId = (String) requestJson.get(UserProperty.userid.name());
			}
			catch(JSONException | ClassCastException e)
			{
				throw new MandatoryPropertyNotFound("ERROR: Required property - \"userId(String)\"");
			}
			
			return new UserDatabaseService().getUser(userId).getResponseString();
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
