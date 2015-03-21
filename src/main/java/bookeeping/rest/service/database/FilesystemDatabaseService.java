package bookeeping.rest.service.database;

import java.util.Map;

import bookeeping.backend.database.service.FilesystemService;
import bookeeping.backend.database.service.neo4jrest.impl.FilesystemServiceImpl;
import bookeeping.backend.exception.DuplicateFilesystem;
import bookeeping.backend.exception.FilesystemNotFound;
import bookeeping.backend.exception.UserNotFound;
import bookeeping.backend.exception.VersionNotFound;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;

public class FilesystemDatabaseService
{
	private FilesystemService filesystemService;
	private static FilesystemDatabaseService filesystemDatabaseService = null;
	
	private FilesystemDatabaseService()
	{
		this.filesystemService = new FilesystemServiceImpl();
	}
	
	public static FilesystemDatabaseService getInstance()
	{
		if(FilesystemDatabaseService.filesystemDatabaseService == null)
		{
			FilesystemDatabaseService.filesystemDatabaseService = new FilesystemDatabaseService();
		}
		
		return FilesystemDatabaseService.filesystemDatabaseService;
	}
	
	public Response createFilesystem(String userId, String filesystemId, Map<String, Object> filesystemProperties)
	{
		Response response = new Response();
		try
		{
			this.filesystemService.createNewFilesystem(filesystemId, userId, filesystemProperties);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: filesystem created - \"" + filesystemId + "\"");
			return response;
		}
		catch (UserNotFound userNotFound)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", userNotFound.getMessage());
			return response;
		}
		catch (DuplicateFilesystem duplicateFilesystem)
		{
			response.addStatusAndOperation(HttpCodes.CONFLICT, "failure", duplicateFilesystem.getMessage());
			return response;
		}
	}
	
	public Response createNewVersion(String userId, String filesystemId, Map<String, Object> changeMetadata, Map<String, Object> changedProperties)
	{
		Response response = new Response();
		try
		{
			this.filesystemService.createNewVersion(userId, filesystemId, changeMetadata, changedProperties);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: filesystem version created - \"" + filesystemId + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
	
	public Response deleteFilesystemTemporarily(String userId, String filesystemId)
	{
		Response response = new Response();
		try
		{
			this.filesystemService.deleteFilesystemTemporarily(userId, filesystemId);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: filesystem temporarily deleted - \"" + filesystemId + "\"");
			return response;
			
		}
		catch (UserNotFound | FilesystemNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
	
	public Response restoreTemporaryDeletedFilesystem(String userId, String filesystemId)
	{
		Response response = new Response();
		try
		{
			this.filesystemService.restoreTemporaryDeletedFilesystem(userId, filesystemId);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: filesystem restored - \"" + filesystemId + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
		catch (DuplicateFilesystem duplicateFilesystem)
		{
			response.addStatusAndOperation(HttpCodes.CONFLICT, "failure", duplicateFilesystem.getMessage());
			return response;
		}
	}
	
	public Response getFilesystem(String userId, String filesystemId, int version)
	{
		Response response = new Response();
		try
		{
			Map<String, Object> retrievedProperties = this.filesystemService.getFilesystem(userId, filesystemId, version);
			response.addData(retrievedProperties);
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
}
