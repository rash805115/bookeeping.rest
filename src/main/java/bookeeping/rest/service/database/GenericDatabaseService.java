package bookeeping.rest.service.database;

import java.util.HashMap;
import java.util.Map;

import bookeeping.backend.database.service.GenericService;
import bookeeping.backend.database.service.neo4jrest.impl.GenericServiceImpl;
import bookeeping.backend.exception.NodeNotFound;
import bookeeping.backend.exception.NodeUnavailable;
import bookeeping.backend.exception.VersionNotFound;
import bookeeping.rest.request.expect.GenericProperty;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;

public class GenericDatabaseService
{
	private GenericService genericService;
	
	public GenericDatabaseService()
	{
		this.genericService = new GenericServiceImpl();
	}
	
	public Response createNewVersion(String commitId, String nodeId, Map<String, Object> changeMetadata, Map<String, Object> changedProperties)
	{
		Response response = new Response();
		try
		{
			String versionNodeId = this.genericService.createNewVersion(commitId, nodeId, changeMetadata, changedProperties);
			Map<String, Object> responseProperty = new HashMap<String, Object>();
			responseProperty.put(GenericProperty.nodeid.name(), versionNodeId);
			response.addData(responseProperty);
			response.addStatusAndOperation(HttpCodes.CREATED, "success", "INFO: node versioned - \"" + nodeId + "\"");
			return response;
		}
		catch (NodeNotFound | NodeUnavailable e)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", e.getMessage());
			return response;
		}
	}
	
	public Response getNode(String nodeId)
	{
		Response response = new Response();
		try
		{
			Map<String, Object> retrievedProperties = this.genericService.getNode(nodeId);
			response.addData(retrievedProperties);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for node - \"" + nodeId + "\"");
			return response;
		}
		catch (NodeNotFound nodeNotFound)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", nodeNotFound.getMessage());
			return response;
		}
	}
	
	public Response getNodeVersion(String nodeId, int version)
	{
		Response response = new Response();
		try
		{
			Map<String, Object> retrievedProperties = this.genericService.getNodeVersion(nodeId, version);
			response.addData(retrievedProperties);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for node - \"" + nodeId + " (v=" + version + ")\"");
			return response;
		}
		catch (NodeNotFound | VersionNotFound | NodeUnavailable e)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", e.getMessage());
			return response;
		}
	}
	
	public Response deleteNodeTemporarily(String commitId, String nodeId)
	{
		Response response = new Response();
		try
		{
			this.genericService.deleteNodeTemporarily(commitId, nodeId);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: node temporarily deleted - \"" + nodeId + "\"");
			return response;
		}
		catch (NodeNotFound | NodeUnavailable e)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", e.getMessage());
			return response;
		}
	}
}
