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

import bookeeping.rest.exception.MandatoryPropertyNotFound;
import bookeeping.rest.request.Request;
import bookeeping.rest.request.expect.Change;
import bookeeping.rest.request.expect.FileProperty;
import bookeeping.rest.request.expect.FilesystemProperty;
import bookeeping.rest.request.expect.UserProperty;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;
import bookeeping.rest.service.database.FileDatabaseService;

@Path("/file")
public class File
{
	@POST
	@Path("/create")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createFile(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String filePath = (String) requestMap.get(FileProperty.filepath.name());
			String fileName = (String) requestMap.get(FileProperty.filename.name());
			if(username == null || filesystemId == null || filePath == null || fileName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | filePath | fileName\"");
			
			Map<String, Object> fileProperties = new HashMap<String, Object>();
			return FileDatabaseService.getInstance().createNewFile(filesystemId, filePath, fileName, filesystemId, username, fileProperties).getResponseString();
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
	
	@POST
	@Path("/create/version")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createFileVersion(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String changeType = (String) requestMap.get(Change.versionchangetype.name());
			String filePath = (String) requestMap.get(FileProperty.filepath.name());
			String fileName = (String) requestMap.get(FileProperty.filename.name());
			if(username == null || filesystemId == null || changeType == null || filePath == null || fileName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | versionChangeType | filePath | fileName\"");
			
			Map<String, Object> changedProperties = new HashMap<String, Object>();
			Map<String, Object> changeMetadata = new HashMap<String, Object>();
			changeMetadata.put(Change.versionchangetype.name(), changeType);
			return FileDatabaseService.getInstance().createNewVersion(filesystemId, username, filesystemId, filePath, fileName, changeMetadata, changedProperties).getResponseString();
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
	
	@POST
	@Path("/share")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String shareFile(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String filePath = (String) requestMap.get(FileProperty.filepath.name());
			String fileName = (String) requestMap.get(FileProperty.filename.name());
			String shareWithUserId = (String) requestMap.get(FileProperty.sharewithuser.name());
			String filePermission = (String) requestMap.get(FileProperty.filepermission.name());
			if(username == null || filesystemId == null || filePath == null || fileName == null || shareWithUserId == null || filePermission == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | filePath | fileName | shareWithUser | filePermission\"");
			
			return FileDatabaseService.getInstance().shareFile(filesystemId, username, filesystemId, filePath, fileName, shareWithUserId, filePermission).getResponseString();
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
	
	@POST
	@Path("/unshare")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String unshareFile(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String filePath = (String) requestMap.get(FileProperty.filepath.name());
			String fileName = (String) requestMap.get(FileProperty.filename.name());
			String unshareWithUserId = (String) requestMap.get(FileProperty.unsharewithuser.name());
			if(username == null || filesystemId == null || filePath == null || fileName == null || unshareWithUserId == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | filePath | fileName | unshareWithUser\"");
			
			return FileDatabaseService.getInstance().unshareFile(filesystemId, username, filesystemId, filePath, fileName, unshareWithUserId).getResponseString();
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
	
	@POST
	@Path("/delete/temporarily")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String deleteFileTemporarily(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String filePath = (String) requestMap.get(FileProperty.filepath.name());
			String fileName = (String) requestMap.get(FileProperty.filename.name());
			if(username == null || filesystemId == null || filePath == null || fileName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | filePath | fileName\"");
			
			return FileDatabaseService.getInstance().deleteFileTemporarily(filesystemId, username, filesystemId, filePath, fileName).getResponseString();
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
	
	@POST
	@Path("/restore")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String restoreFile(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String filePath = (String) requestMap.get(FileProperty.filepath.name());
			String fileName = (String) requestMap.get(FileProperty.filename.name());
			String commitId = (String) requestMap.get(Change.commitid.name());
			if(username == null || filesystemId == null || filePath == null || fileName == null || commitId == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | commitId | filePath | fileName\"");
			
			return FileDatabaseService.getInstance().restoreTemporaryDeletedFile(filesystemId, username, filesystemId, filePath, fileName, commitId).getResponseString();
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
	
	@POST
	@Path("/move")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String moveFile(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String oldFilePath = (String) requestMap.get(FileProperty.oldfilepath.name());
			String oldFileName = (String) requestMap.get(FileProperty.oldfilename.name());
			String newFilePath = (String) requestMap.get(FileProperty.newfilepath.name());
			String newFileName = (String) requestMap.get(FileProperty.newfilename.name());
			if(username == null || filesystemId == null || oldFilePath == null || oldFileName == null || newFilePath == null || newFileName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | oldFilePath | oldFileName | newFilePath | newFileName\"");
			
			return FileDatabaseService.getInstance().moveFile(filesystemId, username, filesystemId, oldFilePath, oldFileName, newFilePath, newFileName).getResponseString();
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
	
	@POST
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getFile(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String filePath = (String) requestMap.get(FileProperty.filepath.name());
			String fileName = (String) requestMap.get(FileProperty.filename.name());
			int version = (int) requestMap.get(Change.version.name());
			if(username == null || filesystemId == null || version < 0 || filePath == null || fileName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | filePath | fileName | version\"");
			
			return FileDatabaseService.getInstance().getFile(username, filesystemId, filePath, fileName, version).getResponseString();
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
