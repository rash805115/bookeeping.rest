package bookeeping.rest.service.database;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import bookeeping.backend.database.service.XrayService;
import bookeeping.backend.database.service.neo4jrest.impl.XrayServiceImpl;
import bookeeping.backend.exception.NodeNotFound;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;

public class XrayDatabaseService
{
	private XrayService xrayService;
	
	public XrayDatabaseService()
	{
		this.xrayService = new XrayServiceImpl();
	}
	
	public Response xrayNode(String nodeId)
	{
		Response response = new Response();
		try
		{
			List<Map<String, Object>> xray = this.xrayService.xrayNode(nodeId);
			JSONArray data = new JSONArray();
			
			for(Map<String, Object> piece : xray)
			{
				JSONObject dataPiece = new JSONObject();
				for(Entry<String, Object> entry : piece.entrySet())
				{
					try
					{
						dataPiece.put(entry.getKey(), entry.getValue());
					}
					catch (JSONException exception) {}
				}
				
				data.put(dataPiece);
			}
			
			response.addData(data);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for xray");
			return response;
		}
		catch (NodeNotFound nodeNotFound)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", nodeNotFound.getMessage());
			return response;
		}
	}
	
	public Response xrayVersion(String nodeId)
	{
		Response response = new Response();
		try
		{
			List<Map<String, Object>> xray = this.xrayService.xrayVersion(nodeId);
			JSONArray data = new JSONArray();
			
			for(Map<String, Object> piece : xray)
			{
				JSONObject dataPiece = new JSONObject();
				for(Entry<String, Object> entry : piece.entrySet())
				{
					try
					{
						dataPiece.put(entry.getKey(), entry.getValue());
					}
					catch (JSONException exception) {}
				}
				
				data.put(dataPiece);
			}
			
			response.addData(data);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for xray");
			return response;
		}
		catch (NodeNotFound nodeNotFound)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", nodeNotFound.getMessage());
			return response;
		}
	}
	
	public Response xrayDeleted(String nodeId)
	{
		Response response = new Response();
		try
		{
			List<Map<String, Object>> xray = this.xrayService.xrayDeleted(nodeId);
			JSONArray data = new JSONArray();
			
			for(Map<String, Object> piece : xray)
			{
				JSONObject dataPiece = new JSONObject();
				for(Entry<String, Object> entry : piece.entrySet())
				{
					try
					{
						dataPiece.put(entry.getKey(), entry.getValue());
					}
					catch (JSONException exception) {}
				}
				
				data.put(dataPiece);
			}
			
			response.addData(data);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for xray");
			return response;
		}
		catch (NodeNotFound nodeNotFound)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", nodeNotFound.getMessage());
			return response;
		}
	}
}
