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
import bookeeping.rest.request.expect.GenericProperty;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;
import bookeeping.rest.service.database.GenericDatabaseService;

@Path("/node")
public class Node
{
	@POST
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getNode(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String nodeId = (String) requestMap.get(GenericProperty.nodeid.name());
			if(nodeId == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"nodeId\"");
			
			return GenericDatabaseService.getInstance().getNode(nodeId).getResponseString();
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
	
	@POST
	@Path("/version/info")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getNodeVersion(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			Map<String, Object> requestMap = request.getRequestMap();
			
			String nodeId = (String) requestMap.get(GenericProperty.nodeid.name());
			int version = (int) requestMap.get(GenericProperty.version.name());
			if(nodeId == null || version < 0) throw new MandatoryPropertyNotFound("ERROR: Required property - \"nodeId | version\"");
			
			return GenericDatabaseService.getInstance().getNodeVersion(nodeId, version).getResponseString();
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
