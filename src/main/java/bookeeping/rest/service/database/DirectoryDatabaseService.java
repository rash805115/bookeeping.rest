package bookeeping.rest.service.database;

import java.util.Map;

import bookeeping.backend.database.service.DirectoryService;
import bookeeping.backend.database.service.neo4jrest.impl.DirectoryServiceImpl;
import bookeeping.backend.exception.DirectoryNotFound;
import bookeeping.backend.exception.DuplicateDirectory;
import bookeeping.backend.exception.FilesystemNotFound;
import bookeeping.backend.exception.UserNotFound;
import bookeeping.backend.exception.VersionNotFound;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;

public class DirectoryDatabaseService
{
	private DirectoryService directoryService;
	private static DirectoryDatabaseService directoryDatabaseService = null;
	
	private DirectoryDatabaseService()
	{
		this.directoryService = new DirectoryServiceImpl();
	}
	
	public static DirectoryDatabaseService getInstance()
	{
		if(DirectoryDatabaseService.directoryDatabaseService == null)
		{
			DirectoryDatabaseService.directoryDatabaseService = new DirectoryDatabaseService();
		}
		
		return DirectoryDatabaseService.directoryDatabaseService;
	}
	
	public Response createNewDirectory(String commitId, String directoryPath, String directoryName, String filesystemId, String userId, Map<String, Object> directoryProperties)
	{
		Response response = new Response();
		try
		{
			this.directoryService.createNewDirectory(commitId, directoryPath, directoryName, filesystemId, userId, directoryProperties);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: directory created - \"" + directoryPath + "/" + directoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound exception)
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
	
	public Response createNewVersion(String commitId, String userId, String filesystemId, String directoryPath, String directoryName, Map<String, Object> changeMetadata, Map<String, Object> changedProperties)
	{
		Response response = new Response();
		try
		{
			this.directoryService.createNewVersion(commitId, userId, filesystemId, directoryPath, directoryName, changeMetadata, changedProperties);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: directory version created for - \"" + directoryPath + "/" + directoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
	
	public Response deleteDirectoryTemporarily(String commitId, String userId, String filesystemId, String directoryPath, String directoryName)
	{
		Response response = new Response();
		try
		{
			this.directoryService.deleteDirectoryTemporarily(commitId, userId, filesystemId, directoryPath, directoryName);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: directory temporarily deleted - \"" + directoryPath + "/" + directoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
	
	public Response restoreTemporaryDeletedDirectory(String commitId, String userId, String filesystemId, String directoryPath, String directoryName, String previousCommitId)
	{
		Response response = new Response();
		try
		{
			this.directoryService.restoreTemporaryDeletedDirectory(commitId, userId, filesystemId, directoryPath, directoryName, previousCommitId);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: directory restored - \"" + directoryPath + "/" + directoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound exception)
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
	
	public Response moveDirectory(String commitId, String userId, String filesystemId, String oldDirectoryPath, String oldDirectoryName, String newDirectoryPath, String newDirectoryName)
	{
		Response response = new Response();
		try
		{
			this.directoryService.moveDirectory(commitId, userId, filesystemId, oldDirectoryPath, oldDirectoryName, newDirectoryPath, newDirectoryName);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: directory moved to - \"" + newDirectoryPath + "/" + newDirectoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound exception)
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
	
	public Response getDirectory(String userId, String filesystemId, String directoryPath, String directoryName, int version)
	{
		Response response = new Response();
		try
		{
			Map<String, Object> retrievedProperties = this.directoryService.getDirectory(userId, filesystemId, directoryPath, directoryName, version);
			response.addData(retrievedProperties);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for directory - \"" + directoryPath + "/" +  directoryName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
}
