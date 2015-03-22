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
import bookeeping.rest.request.expect.DirectoryProperty;
import bookeeping.rest.request.expect.FilesystemProperty;
import bookeeping.rest.request.expect.UserProperty;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;
import bookeeping.rest.service.database.DirectoryDatabaseService;

@Path("/directory")
public class Directory
{
	@POST
	@Path("/create")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createDirectory(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String directoryPath = (String) requestMap.get(DirectoryProperty.directorypath.name());
			String directoryName = (String) requestMap.get(DirectoryProperty.directoryname.name());
			if(username == null || filesystemId == null || directoryPath == null || directoryName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | directoryPath | directoryName\"");
			
			Map<String, Object> directoryProperties = new HashMap<String, Object>();
			return DirectoryDatabaseService.getInstance().createNewDirectory(filesystemId, directoryPath, directoryName, filesystemId, username, directoryProperties).getResponseString();
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
	public String createDirectoryVersion(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String changeType = (String) requestMap.get(Change.versionchangetype.name());
			String directoryPath = (String) requestMap.get(DirectoryProperty.directorypath.name());
			String directoryName = (String) requestMap.get(DirectoryProperty.directoryname.name());
			if(username == null || filesystemId == null || changeType == null || directoryPath == null || directoryName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | versionChangeType | directoryPath | directoryName\"");
			
			Map<String, Object> changedProperties = new HashMap<String, Object>();
			Map<String, Object> changeMetadata = new HashMap<String, Object>();
			changeMetadata.put(Change.versionchangetype.name(), changeType);
			return DirectoryDatabaseService.getInstance().createNewVersion(filesystemId, username, filesystemId, directoryPath, directoryName, changeMetadata, changedProperties).getResponseString();
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
	public String deleteDirectoryTemporarily(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String directoryPath = (String) requestMap.get(DirectoryProperty.directorypath.name());
			String directoryName = (String) requestMap.get(DirectoryProperty.directoryname.name());
			if(username == null || filesystemId == null || directoryPath == null || directoryName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | directoryPath | directoryName\"");
			
			return DirectoryDatabaseService.getInstance().deleteDirectoryTemporarily(filesystemId, username, filesystemId, directoryPath, directoryName).getResponseString();
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
	public String restoreDirectory(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String directoryPath = (String) requestMap.get(DirectoryProperty.directorypath.name());
			String directoryName = (String) requestMap.get(DirectoryProperty.directoryname.name());
			String commitId = (String) requestMap.get(Change.commitid.name());
			if(username == null || filesystemId == null || commitId == null || directoryPath == null || directoryName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | directoryPath | directoryName | commitId\"");
			
			return DirectoryDatabaseService.getInstance().restoreTemporaryDeletedDirectory(filesystemId, username, filesystemId, directoryPath, directoryName, commitId).getResponseString();
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
	public String moveDirectory(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String oldDirectoryPath = (String) requestMap.get(DirectoryProperty.olddirectorypath.name());
			String oldDirectoryName = (String) requestMap.get(DirectoryProperty.olddirectoryname.name());
			String newDirectoryPath = (String) requestMap.get(DirectoryProperty.newdirectorypath.name());
			String newDirectoryName = (String) requestMap.get(DirectoryProperty.newdirectoryname.name());
			if(username == null || filesystemId == null || oldDirectoryPath == null || oldDirectoryName == null || newDirectoryPath == null || newDirectoryName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | oldDirectoryPath | oldDirectoryName | newDirectoryPath | newDirectoryName\"");
			
			return DirectoryDatabaseService.getInstance().moveDirectory(filesystemId, username, filesystemId, oldDirectoryPath, oldDirectoryName, newDirectoryPath, newDirectoryName).getResponseString();
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
	public String getDirectory(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String directoryPath = (String) requestMap.get(DirectoryProperty.directorypath.name());
			String directoryName = (String) requestMap.get(DirectoryProperty.directoryname.name());
			int version = (int) requestMap.get(Change.version.name());
			if(username == null || filesystemId == null || version < 0 || directoryPath == null || directoryName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | directoryPath | directoryName | version\"");
			
			return DirectoryDatabaseService.getInstance().getDirectory(username, filesystemId, directoryPath, directoryName, version).getResponseString();
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
