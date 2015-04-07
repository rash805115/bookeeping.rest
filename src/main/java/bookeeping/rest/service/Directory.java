package bookeeping.rest.service;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import bookeeping.rest.exception.MandatoryPropertyNotFound;
import bookeeping.rest.request.Request;
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
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getDirectory(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			JSONObject requestJson = request.getRequestObject();
			
			String userId = null, filesystemId = null, directoryPath = null, directoryName = null;
			int filesystemVersion = -1;
			try
			{
				userId = (String) requestJson.get(UserProperty.userid.name());
				filesystemId = (String) requestJson.get(FilesystemProperty.filesystemid.name());
				filesystemVersion = (int) requestJson.get(FilesystemProperty.filesystemversion.name());
				directoryPath = (String) requestJson.get(DirectoryProperty.directorypath.name());
				directoryName = (String) requestJson.get(DirectoryProperty.directoryname.name());
			}
			catch(JSONException | ClassCastException e)
			{
				throw new MandatoryPropertyNotFound("ERROR: Required property - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | directoryPath(String) | directoryName(String)\"");
			} 
			
			return new DirectoryDatabaseService().getDirectory(userId, filesystemId, filesystemVersion, directoryPath, directoryName).getResponseString();
		}
		catch(JSONException jsonException)
		{
			return response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", "ERROR: Malformed Json");
		}
		catch(MandatoryPropertyNotFound mandatoryPropertyNotFound)
		{
			return response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", mandatoryPropertyNotFound.getMessage());
		}
	}
}
