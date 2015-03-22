package bookeeping.rest.service.database;

import java.util.Map;

import bookeeping.backend.database.service.FileService;
import bookeeping.backend.database.service.neo4jrest.impl.FileServiceImpl;
import bookeeping.backend.exception.DirectoryNotFound;
import bookeeping.backend.exception.DuplicateFile;
import bookeeping.backend.exception.FileNotFound;
import bookeeping.backend.exception.FilesystemNotFound;
import bookeeping.backend.exception.UserNotFound;
import bookeeping.backend.exception.VersionNotFound;
import bookeeping.backend.file.FilePermission;
import bookeeping.rest.exception.InvalidFilePermission;
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
	
	public Response createNewFile(String commitId, String filePath, String fileName, String filesystemId, String userId, Map<String, Object> fileProperties)
	{
		Response response = new Response();
		try
		{
			this.fileService.createNewFile(commitId, filePath, fileName, filesystemId, userId, fileProperties);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: file created - \"" + filePath + "/" + fileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound exception)
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
	
	public Response createNewVersion(String commitId, String userId, String filesystemId, String filePath, String fileName, Map<String, Object> changeMetadata, Map<String, Object> changedProperties)
	{
		Response response = new Response();
		try
		{
			this.fileService.createNewVersion(commitId, userId, filesystemId, filePath, fileName, changeMetadata, changedProperties);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: file version created for - \"" + filePath + "/" + fileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
	
	public Response shareFile(String commitId, String userId, String filesystemId, String filePath, String fileName, String shareWithUserId, String filePermission)
	{
		Response response = new Response();
		try
		{
			FilePermission permission;
			if(filePermission.equalsIgnoreCase(FilePermission.READ.name()))
			{
				permission = FilePermission.READ;
			}
			else if(filePermission.equalsIgnoreCase(FilePermission.WRITE.name()))
			{
				permission = FilePermission.WRITE;
			}
			else
			{
				throw new InvalidFilePermission("ERROR: File permission is not valid - \"" + filePermission + "\"");
			}
			
			this.fileService.shareFile(commitId, userId, filesystemId, filePath, fileName, shareWithUserId, permission);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: file shared - \"" + filePath + "/" + fileName + "\" with user - \"" + shareWithUserId + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound exception)
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
	
	public Response unshareFile(String commitId, String userId, String filesystemId, String filePath, String fileName, String unshareWithUserId)
	{
		Response response = new Response();
		try
		{
			this.fileService.unshareFile(commitId, userId, filesystemId, filePath, fileName, unshareWithUserId);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: file un-shared - \"" + filePath + "/" + fileName + "\" with user - \"" + unshareWithUserId + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
	
	public Response deleteFileTemporarily(String commitId, String userId, String filesystemId, String filePath, String fileName)
	{
		Response response = new Response();
		try
		{
			this.fileService.deleteFileTemporarily(commitId, userId, filesystemId, filePath, fileName);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: file temporarily deleted - \"" + filePath + "/" + fileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
	
	public Response restoreTemporaryDeletedFile(String commitId, String userId, String filesystemId, String filePath, String fileName, String previousCommitId)
	{
		Response response = new Response();
		try
		{
			this.fileService.restoreTemporaryDeletedFile(commitId, userId, filesystemId, filePath, fileName, previousCommitId);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: file restored - \"" + filePath + "/" + fileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound exception)
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
	
	public Response moveFile(String commitId, String userId, String filesystemId, String oldFilePath, String oldFileName, String newFilePath, String newFileName)
	{
		Response response = new Response();
		try
		{
			this.fileService.moveFile(commitId, userId, filesystemId, oldFilePath, oldFileName, newFilePath, newFileName);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: file moved to - \"" + newFilePath + "/" + newFileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound exception)
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
	
	public Response getFile(String userId, String filesystemId, String filePath, String fileName, int version)
	{
		Response response = new Response();
		try
		{
			Map<String, Object> retrievedProperties = this.fileService.getFile(userId, filesystemId, filePath, fileName, version);
			response.addData(retrievedProperties);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for file - \"" + filePath + "/" +  fileName + "\"");
			return response;
		}
		catch (UserNotFound | FilesystemNotFound | DirectoryNotFound | FileNotFound | VersionNotFound exception)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", exception.getMessage());
			return response;
		}
	}
}
