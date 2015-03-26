package bookeeping.rest.service.database;

import java.util.Map;

import bookeeping.backend.database.service.FileService;
import bookeeping.backend.database.service.neo4jrest.impl.FileServiceImpl;
import bookeeping.backend.exception.DirectoryNotFound;
import bookeeping.backend.exception.DuplicateFile;
import bookeeping.backend.exception.FileNotFound;
import bookeeping.backend.exception.FilesystemNotFound;
import bookeeping.backend.exception.NodeNotFound;
import bookeeping.backend.exception.NodeUnavailable;
import bookeeping.backend.exception.UserNotFound;
import bookeeping.backend.exception.VersionNotFound;
import bookeeping.rest.exception.InvalidFilePermission;
import bookeeping.rest.request.expect.FilePermission;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;

public class FileDatabaseService
{
	private FileService fileService;
	private static FileDatabaseService fileDatabaseService = null;
	
	private FileDatabaseService()
	{
		this.fileService = new FileServiceImpl();
	}
	
	public static FileDatabaseService getInstance()
	{
		if(FileDatabaseService.fileDatabaseService == null)
		{
			FileDatabaseService.fileDatabaseService = new FileDatabaseService();
		}
		
		return FileDatabaseService.fileDatabaseService;
	}
	
	public Response createNewFile(String commitId, String userId, String filesystemId, int filesystemVersion, String filePath, String fileName, Map<String, Object> fileProperties)
	{
		Response response = new Response();
		try
		{
			this.fileService.createNewFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, fileProperties);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: file created - \"" + (filePath.equals("/") ? "" : filePath) + "/" + fileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
		catch (DuplicateFile duplicateFile)
		{
			response.addStatusAndOperation(HttpCodes.CONFLICT, "failure", duplicateFile.getMessage());
			return response;
		}
	}
	
	public Response shareFile(String commitId, String userId, String filesystemId, int filesystemVersion, String filePath, String fileName, String shareWithUserId, String filePermission)
	{
		Response response = new Response();
		try
		{
			FilePermission permission;
			if(filePermission.equalsIgnoreCase(FilePermission.read.name()))
			{
				permission = FilePermission.read;
			}
			else if(filePermission.equalsIgnoreCase(FilePermission.write.name()))
			{
				permission = FilePermission.write;
			}
			else
			{
				throw new InvalidFilePermission("ERROR: File permission is not valid - \"" + filePermission + "\"");
			}
			
			this.fileService.shareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, shareWithUserId, permission.name());
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: file shared - \"" + (filePath.equals("/") ? "" : filePath) + "/" + fileName + "\" with user - \"" + shareWithUserId + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
		catch (InvalidFilePermission invalidFilePermission)
		{
			response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", invalidFilePermission.getMessage());
			return response;
		}
	}
	
	public Response unshareFile(String commitId, String userId, String filesystemId, int filesystemVersion, String filePath, String fileName, String unshareWithUserId)
	{
		Response response = new Response();
		try
		{
			this.fileService.unshareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, unshareWithUserId);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: file un-shared - \"" + (filePath.equals("/") ? "" : filePath) + "/" + fileName + "\" with user - \"" + unshareWithUserId + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
	
	public Response restoreFile(String commitId, String userId, String filesystemId, int filesystemVersion, String filePath, String fileName, String nodeIdToBeRestored)
	{
		Response response = new Response();
		try
		{
			this.fileService.restoreFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, nodeIdToBeRestored);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: file restored - \"" + (filePath.equals("/") ? "" : filePath) + "/" + fileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound | VersionNotFound | NodeNotFound | NodeUnavailable exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
		catch (DuplicateFile duplicateFile)
		{
			response.addStatusAndOperation(HttpCodes.CONFLICT, "failure", duplicateFile.getMessage());
			return response;
		}
	}
	
	public Response moveFile(String commitId, String userId, String filesystemId, int filesystemVersion, String oldFilePath, String oldFileName, String newFilePath, String newFileName)
	{
		Response response = new Response();
		try
		{
			this.fileService.moveFile(commitId, userId, filesystemId, filesystemVersion, oldFilePath, oldFileName, newFilePath, newFileName);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: file moved to - \"" + (newFilePath.equals("/") ? "" : newFilePath) + "/" + newFileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
		catch (DuplicateFile duplicateFile)
		{
			response.addStatusAndOperation(HttpCodes.CONFLICT, "failure", duplicateFile.getMessage());
			return response;
		}
	}
	
	public Response getFile(String userId, String filesystemId, int filesystemVersion, String filePath, String fileName)
	{
		Response response = new Response();
		try
		{
			Map<String, Object> retrievedProperties = this.fileService.getFile(userId, filesystemId, filesystemVersion, filePath, fileName);
			response.addData(retrievedProperties);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for file - \"" + (filePath.equals("/") ? "" : filePath) + "/" + fileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
}
