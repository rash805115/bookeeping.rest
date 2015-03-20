package bookeeping.rest.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import bookeeping.backend.exception.UserNotFound;
import bookeeping.rest.exception.MandatoryPropertyNotFound;
import bookeeping.rest.request.Request;
import bookeeping.rest.request.ValidateRequest;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;
import bookeeping.rest.service.property.Expect;
import bookeeping.rest.service.property.UserProperty;

@Path("/user")
public class User
{
	private UserService userService;
	
	public User()
	{
		userService = new UserServiceImpl();
	}
	
	@POST
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getUser(InputStream inputStream)
	{
		try
		{
			Request request = new Request(inputStream);
			Response response = new Response();
			
			Map<String, Object> requestMap = request.getRequestMap();
			String[] expectedProperties = {UserProperty.username.name()};
			
			try
			{
				ValidateRequest.validateKeys(Expect.expectProperties(expectedProperties), requestMap.keySet());
			}
			catch (MandatoryPropertyNotFound mandatoryPropertyNotFound)
			{
				response.addStatusCode(HttpCodes.BADREQUEST);
				response.addOperationResult("failure", "ERROR: Missing properties. Expected - " + expectedProperties.toString());
				return response.getResponseString();
			}
			
			String username = (String) requestMap.get(UserProperty.username.name());
			
			try
			{
				JSONObject data = new JSONObject();
				Map<String, Object> retrievedUserProperties = userService.getUser(username);
				for(Entry<String, Object> entry : retrievedUserProperties.entrySet())
				{
					data.put(entry.getKey(), entry.getValue());
				}
				
				response.addStatusCode(HttpCodes.OK);
				response.addOperationResult("success", "INFO: 1 user found.");
				response.addData(data);
				return response.getResponseString();
			}
			catch(UserNotFound userNotFound)
			{
				response.addStatusCode(HttpCodes.NOTFOUND);
				response.addOperationResult("failure", userNotFound.getMessage());
				return response.getResponseString();
			}
		}
		catch(JSONException jsonException)
		{
			Response response = new Response();
			response.addStatusCode(HttpCodes.BADREQUEST);
			response.addOperationResult("failure", "ERROR: Malformed Json");
			return response.getResponseString();
		}
	}
	
	@POST
	@Path("/create")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createUser(InputStream inputStream)
	{
		try
		{
			Request request = new Request(inputStream);
			Response response = new Response();
			
			Map<String, Object> requestMap = request.getRequestMap();
			String[] expectedProperties = {UserProperty.username.name()};
			String[] optionalProperties = {
				UserProperty.firstname.name(), UserProperty.lastname.name(), UserProperty.primaryemail.name(),
				UserProperty.secondaryemail.name(), UserProperty.phone.name()
			};
			
			try
			{
				ValidateRequest.validateKeys(Expect.expectProperties(expectedProperties), requestMap.keySet());
			}
			catch (MandatoryPropertyNotFound mandatoryPropertyNotFound)
			{
				response.addStatusCode(HttpCodes.BADREQUEST);
				response.addOperationResult("failure", "ERROR: Missing properties. Expected - " + expectedProperties.toString());
				return response.getResponseString();
			}
			
			String username = (String) requestMap.get(UserProperty.username.name());
			Set<String> optionalKeys = ValidateRequest.validateOptionalKeys(Expect.expectProperties(optionalProperties), requestMap.keySet());
			
			Map<String, Object> userProperties = new HashMap<String, Object>();
			for(String key : optionalKeys)
			{
				userProperties.put(key, requestMap.get(key));
			}
			
			try
			{
				userService.createNewUser(username, userProperties);
				
				response.addStatusCode(HttpCodes.CREATED);
				response.addOperationResult("success", "INFO: user created - \"" + username + "\"");
				return response.getResponseString();
			}
			catch (DuplicateUser duplicateUser)
			{
				response.addStatusCode(HttpCodes.CONFLICT);
				response.addOperationResult("failure", duplicateUser.getMessage());
				return response.getResponseString();
			}
		}
		catch(JSONException jsonException)
		{
			Response response = new Response();
			response.addStatusCode(HttpCodes.BADREQUEST);
			response.addOperationResult("failure", "ERROR: Malformed Json");
			return response.getResponseString();
		}
	}
}
