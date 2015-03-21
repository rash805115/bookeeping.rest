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
import bookeeping.rest.request.expect.FilesystemProperty;
import bookeeping.rest.request.expect.UserProperty;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;
import bookeeping.rest.service.database.FilesystemDatabaseService;

@Path("/filesystem")
public class Filesystem
{
	@POST
	@Path("/create")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createFilesystem(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			if(username == null || filesystemId == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId\"");
			
			Map<String, Object> filesystemProperties = new HashMap<String, Object>();
			return FilesystemDatabaseService.getInstance().createFilesystem(username, filesystemId, filesystemProperties).getResponseString();
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
	public String createFilesystemVersion(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			String changeType = (String) requestMap.get(Change.versionchangetype.name());
			if(username == null || filesystemId == null || changeType == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | versionchangetype\"");
			
			Map<String, Object> changedProperties = new HashMap<String, Object>();
			Map<String, Object> changeMetadata = new HashMap<String, Object>();
			changeMetadata.put(Change.versionchangetype.name(), changeType);
			return FilesystemDatabaseService.getInstance().createNewVersion(username, filesystemId, changeMetadata, changedProperties).getResponseString();
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
	@Path("/delete/temporary")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String deleteFilesystemTemporarily(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			if(username == null || filesystemId == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId\"");
			
			return FilesystemDatabaseService.getInstance().deleteFilesystemTemporarily(username, filesystemId).getResponseString();
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
	public String restoreFilesystem(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			if(username == null || filesystemId == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId\"");
			
			return FilesystemDatabaseService.getInstance().restoreTemporaryDeletedFilesystem(username, filesystemId).getResponseString();
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
	public String getFilesystem(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String username = (String) requestMap.get(UserProperty.username.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			int version = (int) requestMap.get(Change.version.name());
			if(username == null || filesystemId == null || version < 0) throw new MandatoryPropertyNotFound("ERROR: Required property - \"username | filesystemId | version\"");
			
			return FilesystemDatabaseService.getInstance().getFilesystem(username, filesystemId, version).getResponseString();
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
