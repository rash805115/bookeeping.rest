package bookeeping.rest.service;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import bookeeping.backend.database.service.UserService;
import bookeeping.backend.database.service.neo4jrest.impl.UserServiceImpl;
import bookeeping.backend.exception.DuplicateUser;
import bookeeping.rest.exception.MandatoryPropertyNotFound;
import bookeeping.rest.service.property.UserProperty;
import bookeeping.rest.utilities.ExpectProperty;
import bookeeping.rest.utilities.ProduceJson;

@Path("/user")
public class User
{
	private UserService userService;
	
	public User()
	{
		userService = new UserServiceImpl();
	}
	
	@POST
	@Path("/create")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createUser(InputStream inputStream)
	{
		JSONObject response = new JSONObject();
		try
		{
			JSONObject jsonObject = ProduceJson.inputStreamToJson(inputStream);
			
			String[] expectedProperties = {UserProperty.username.name()};
			String[] optionalProperties = {
				UserProperty.firstname.name(), UserProperty.lastname.name(), UserProperty.primaryemail.name(),
				UserProperty.secondaryemail.name(), UserProperty.phone.name()
			};
			
			Map<String, Object> userProperties = null;
			try
			{
				userProperties = ExpectProperty.expectProperty(jsonObject, expectedProperties, optionalProperties);
			}
			catch(MandatoryPropertyNotFound mandatoryPropertyNotFound)
			{
				response.put("status", "failure");
				response.put("code", 400);
				response.put("code_string", "Bad Request");
				response.put("message", mandatoryPropertyNotFound.getMessage());
				return response.toString();
			}
			
			String username = (String) userProperties.remove(UserProperty.username.name());			
			try
			{
				userService.createNewUser(username, userProperties);
				
				response.put("status", "success");
				response.put("code", 200);
				response.put("code_string", "Success");
				response.put("message", "User - \"" + username + "\" created successfully.");
				return response.toString();
			}
			catch (DuplicateUser duplicateUser)
			{
				response.put("status", "failure");
				response.put("code", 400);
				response.put("code_string", "Bad Request");
				response.put("message", duplicateUser.getMessage());
				return response.toString();
			}
		}
		catch(JSONException jsonException)
		{
			try
			{
				response.put("status", "failure");
				response.put("code", 400);
				response.put("code_string", "Bad Request");
				response.put("message", "ERROR: Malformed Json");
				return response.toString();
			}
			catch(JSONException jsonExceptionUnderCatch)
			{
				jsonExceptionUnderCatch.printStackTrace();
				return new JSONObject().toString();
			}
		}
	}
}
