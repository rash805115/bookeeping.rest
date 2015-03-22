package bookeeping.rest.service.database;

import java.util.Map;

import bookeeping.backend.database.service.UserService;
import bookeeping.backend.database.service.neo4jrest.impl.UserServiceImpl;
import bookeeping.backend.exception.DuplicateUser;
import bookeeping.backend.exception.UserNotFound;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;

public class UserDatabaseService
{
	private UserService userService;
	private static UserDatabaseService userDatabaseService = null;
	
	private UserDatabaseService()
	{
		this.userService = new UserServiceImpl();
	}
	
	public static UserDatabaseService getInstance()
	{
		if(UserDatabaseService.userDatabaseService == null)
		{
			UserDatabaseService.userDatabaseService = new UserDatabaseService();
		}
		
		return UserDatabaseService.userDatabaseService;
	}
	
	public Response createUser(String userId, Map<String, Object> userProperties)
	{
		Response response = new Response();
		try
		{
			userService.createNewUser(userId, userProperties);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: user created - \"" + userId + "\"");
			return response;
		}
		catch (DuplicateUser duplicateUser)
		{
			response.addStatusAndOperation(HttpCodes.CONFLICT, "failure", duplicateUser.getMessage());
			return response;
		}
	}
	
	public Response getUser(String userId)
	{
		Response response = new Response();
		try
		{
			Map<String, Object> retrievedProperties = this.userService.getUser(userId);
			response.addData(retrievedProperties);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for user - \"" + userId + "\"");
			return response;
		}
		catch(UserNotFound userNotFound)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", userNotFound.getMessage());
			return response;
		}
	}
}
