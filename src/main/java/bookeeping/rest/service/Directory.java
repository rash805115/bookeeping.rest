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
			
			String userId = (String) requestJson.get(UserProperty.userid.name());
			String filesystemId = (String) requestJson.get(FilesystemProperty.filesystemid.name());
			int filesystemVersion = (int) requestJson.get(FilesystemProperty.filesystemversion.name());
			String directoryPath = (String) requestJson.get(DirectoryProperty.directorypath.name());
			String directoryName = (String) requestJson.get(DirectoryProperty.directoryname.name());
			
			if(userId == null || filesystemId == null || filesystemVersion < 0 || directoryPath == null || directoryName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"userId | filesystemId | filesystemVersion | directoryPath | directoryName\"");
			
			return DirectoryDatabaseService.getInstance().getDirectory(userId, filesystemId, filesystemVersion, directoryPath, directoryName).getResponseString();
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
