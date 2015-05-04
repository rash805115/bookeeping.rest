package bookeeping.rest.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response getNode(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			JSONObject requestJson = request.getRequestObject();
			
			String nodeId = null;
			try
			{
				nodeId = (String) requestJson.get(GenericProperty.nodeid.name());
			}
			catch(JSONException | ClassCastException e)
			{
				throw new MandatoryPropertyNotFound("ERROR: Required property - \"nodeId(String)\"");
			}
			
			return new GenericDatabaseService().getNode(nodeId).getServerResponse();
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
	
	@POST
	@Path("/version/info")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response getNodeVersion(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			JSONObject requestJson = request.getRequestObject();
			
			String nodeId = null;
			int version = -1;
			try
			{
				nodeId = (String) requestJson.get(GenericProperty.nodeid.name());
				version = (int) requestJson.get(GenericProperty.version.name());
			}
			catch(JSONException | ClassCastException e)
			{
				throw new MandatoryPropertyNotFound("ERROR: Required property - \"nodeId(String) | version(Integer)\"");
			}
			
			return new GenericDatabaseService().getNodeVersion(nodeId, version).getServerResponse();
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
	
	@POST
	@Path("/modify")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response changeNodeProperty(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			JSONObject requestJson = request.getRequestObject();
			
			String nodeId = null;
			try
			{
				nodeId = (String) requestJson.remove(GenericProperty.nodeid.name());
				if(nodeId == null) throw new JSONException("");
			}
			catch(JSONException | ClassCastException e)
			{
				throw new MandatoryPropertyNotFound("ERROR: Required property - \"nodeId(String)\"");
			}
			
			Map<String, Object> properties = new HashMap<String, Object>();
			@SuppressWarnings("unchecked") Iterator<Object> keyset = requestJson.keys();
			while(keyset.hasNext())
			{
				try
				{
					String key = (String) keyset.next();
					properties.put(key, requestJson.get(key));
				}
				catch(JSONException jsonException) {}
				catch(ClassCastException e)
				{
					throw new ClassCastException("ERROR: Property keys must be string.");
				}
			}
			
			return new GenericDatabaseService().changeNodeProperties(nodeId, properties).getServerResponse();
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
