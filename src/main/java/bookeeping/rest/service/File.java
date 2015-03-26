package bookeeping.rest.service;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;

import bookeeping.rest.exception.MandatoryPropertyNotFound;
import bookeeping.rest.request.Request;
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
			
			String userId = (String) requestMap.get(UserProperty.userid.name());
			String filesystemId = (String) requestMap.get(FilesystemProperty.filesystemid.name());
			int filesystemVersion = (int) requestMap.get(FilesystemProperty.filesystemversion.name());
			String filePath = (String) requestMap.get(FileProperty.filepath.name());
			String fileName = (String) requestMap.get(FileProperty.filename.name());
			if(userId == null || filesystemId == null || filesystemVersion < 0 || filePath == null || fileName == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"userId | filesystemId | filesystemVersion | filePath | fileName\"");
			
			return FileDatabaseService.getInstance().getFile(userId, filesystemId, filesystemVersion, filePath, fileName).getResponseString();
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
