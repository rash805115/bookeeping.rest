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
import bookeeping.rest.request.expect.FilesystemProperty;
import bookeeping.rest.request.expect.UserProperty;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;
import bookeeping.rest.service.database.FilesystemDatabaseService;

@Path("/filesystem")
public class Filesystem
{
	@POST
	@Path("/info")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response getFilesystem(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			JSONObject requestJson = request.getRequestObject();
			
			String userId = null, filesystemId = null;
			try
			{
				userId = (String) requestJson.get(UserProperty.userid.name());
				filesystemId = (String) requestJson.get(FilesystemProperty.filesystemid.name());
			}
			catch(JSONException | ClassCastException e)
			{
				throw new MandatoryPropertyNotFound("ERROR: Required property - \"userId(String) | filesystemId(String)\"");
			}
			
			return new FilesystemDatabaseService().getFilesystem(userId, filesystemId).getServerResponse();
		}
		catch(JSONException jsonException)
		{
			response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", "ERROR: Malformed Json");
			return response.getServerResponse();
		}
		catch(MandatoryPropertyNotFound mandatoryPropertyNotFound)
		{
			response.addStatusAndOperation(HttpCodes.BADREQUEST, "failure", mandatoryPropertyNotFound.getMessage());
			return response.getServerResponse();
		}
	}
}
