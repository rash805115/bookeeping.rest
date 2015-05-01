package bookeeping.rest.service.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import bookeeping.backend.database.MandatoryProperties;
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
	
	public Response xrayNodeFull(String nodeId)
	{
		Response response = new Response();
		JSONObject xray = new JSONObject();
		try
		{
			List<String> unvisitedNodes = new ArrayList<String>();
			unvisitedNodes.add(nodeId);
			List<List<String>> levelList = new ArrayList<List<String>>();
			
			do
			{
				List<String> level = new ArrayList<String>();
				String parentNodeId = unvisitedNodes.remove(0);
				JSONArray childrenNodes = this.xrayNode(parentNodeId).getResponseObject().getJSONArray("data");
				for(int i = 0; i < childrenNodes.length(); i++)
				{
					JSONObject child = childrenNodes.getJSONObject(i);
					child.put("parent", parentNodeId);
					child.put("children", new JSONArray());
					String childNodeId = child.getString(MandatoryProperties.nodeId.name());
					
					level.add(childNodeId);
					unvisitedNodes.add(0, childNodeId);
					xray.put(childNodeId, child);
				}
				
				levelList.add(level);
			}
			while(!unvisitedNodes.isEmpty());
			
			for(int i = levelList.size() - 1; i >= 0; i--)
			{
				List<String> level = levelList.get(i);
				for(String childNodeId : level)
				{
					JSONObject child = xray.getJSONObject(childNodeId);
					try
					{
						JSONObject parent = xray.getJSONObject(child.getString("parent"));
						parent.getJSONArray("children").put(child);
						xray.remove(childNodeId);
						xray.put(parent.getString(MandatoryProperties.nodeId.name()), parent);
					}
					catch(JSONException jsonException) {}
				}
			}
			
			JSONArray data =  new JSONArray();
			@SuppressWarnings("unchecked") Iterator<String> keys = xray.keys();
			while(keys.hasNext())
			{
				data.put(xray.get(keys.next()));
			}
			
			response.addData(data);
			response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: record found for xray");
			return response;
		}
		catch(JSONException jsonException)
		{
			response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", "ERROR: node not found! - \"" + nodeId + "\"");
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
