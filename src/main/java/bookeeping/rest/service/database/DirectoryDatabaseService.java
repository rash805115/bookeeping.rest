package bookeeping.rest.service.database;

import java.util.Map;

import bookeeping.backend.database.service.DirectoryService;
import bookeeping.backend.database.service.neo4jrest.impl.DirectoryServiceImpl;
import bookeeping.backend.exception.DirectoryNotFound;
import bookeeping.backend.exception.DuplicateDirectory;
import bookeeping.backend.exception.FilesystemNotFound;
import bookeeping.backend.exception.NodeNotFound;
import bookeeping.backend.exception.NodeUnavailable;
import bookeeping.backend.exception.UserNotFound;
import bookeeping.backend.exception.VersionNotFound;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;

public class DirectoryDatabaseService
{
	private DirectoryService directoryService;
	
	public DirectoryDatabaseService()
	{
		this.directoryService = new DirectoryServiceImpl();
	}
	
	public Response createNewDirectory(String commitId, String userId, String filesystemId, int filesystemVersion, String directoryPath, String directoryName, Map<String, Object> directoryProperties)
	{
		Response response = new Response();
		try
		{
			this.directoryService.createNewDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, directoryProperties);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: directory created - \"" + (directoryPath.equals("/") ? "" : directoryPath) + "/" + directoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
		catch (DuplicateDirectory duplicateFilesystem)
		{
			response.addStatusAndOperation(HttpCodes.CONFLICT, "failure", duplicateFilesystem.getMessage());
			return response;
		}
	}
	
	public Response restoreDirectory(String commitId, String userId, String filesystemId, int filesystemVersion, String directoryPath, String directoryName, String nodeIdToBeRestored)
	{
		Response response = new Response();
		try
		{
			this.directoryService.restoreDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, nodeIdToBeRestored);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: directory restored - \"" + (directoryPath.equals("/") ? "" : directoryPath) + "/" + directoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | VersionNotFound | NodeNotFound | NodeUnavailable exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
		catch (DuplicateDirectory duplicateFilesystem)
		{
			response.addStatusAndOperation(HttpCodes.CONFLICT, "failure", duplicateFilesystem.getMessage());
			return response;
		}
	}
	
	public Response moveDirectory(String commitId, String userId, String filesystemId, int filesystemVersion, String oldDirectoryPath, String oldDirectoryName, String newDirectoryPath, String newDirectoryName)
	{
		Response response = new Response();
		try
		{
			this.directoryService.moveDirectory(commitId, userId, filesystemId, filesystemVersion, oldDirectoryPath, oldDirectoryName, newDirectoryPath, newDirectoryName);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: directory moved to - \"" + (newDirectoryPath.equals("/") ? "" : newDirectoryPath) + "/" + newDirectoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
		catch (DuplicateDirectory duplicateFilesystem)
		{
			response.addStatusAndOperation(HttpCodes.CONFLICT, "failure", duplicateFilesystem.getMessage());
			return response;
		}
	}
	
	public Response getDirectory(String userId, String filesystemId, int filesystemVersion, String directoryPath, String directoryName)
	{
		Response response = new Response();
		try
		{
			Map<String, Object> retrievedProperties = this.directoryService.getDirectory(userId, filesystemId, filesystemVersion, directoryPath, directoryName);
			response.addData(retrievedProperties);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for directory - \"" + (directoryPath.equals("/") ? "" : directoryPath) + "/" + directoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
}
