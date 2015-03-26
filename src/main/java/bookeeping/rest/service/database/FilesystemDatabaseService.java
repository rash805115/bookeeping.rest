package bookeeping.rest.service.database;

import java.util.Map;

import bookeeping.backend.database.service.FilesystemService;
import bookeeping.backend.database.service.neo4jrest.impl.FilesystemServiceImpl;
import bookeeping.backend.exception.DuplicateFilesystem;
import bookeeping.backend.exception.FilesystemNotFound;
import bookeeping.backend.exception.NodeNotFound;
import bookeeping.backend.exception.NodeUnavailable;
import bookeeping.backend.exception.UserNotFound;
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
	
	public Response createNewFilesystem(String commitId, String userId, String filesystemId, Map<String, Object> filesystemProperties)
	{
		Response response = new Response();
		try
		{
			this.filesystemService.createNewFilesystem(commitId, userId, filesystemId, filesystemProperties);
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
	
	public Response restoreFilesystem(String commitId, String userId, String filesystemId, String nodeIdToBeRestored)
	{
		Response response = new Response();
		try
		{
			this.filesystemService.restoreFilesystem(commitId, userId, filesystemId, nodeIdToBeRestored);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: filesystem restored - \"" + filesystemId + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | NodeNotFound | NodeUnavailable exception)
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
	
	public Response getFilesystem(String userId, String filesystemId)
	{
		Response response = new Response();
		try
		{
			Map<String, Object> retrievedProperties = this.filesystemService.getFilesystem(userId, filesystemId);
			response.addData(retrievedProperties);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for filesystem - \"" + filesystemId + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
}
