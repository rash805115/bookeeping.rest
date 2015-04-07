package bookeeping.rest.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import bookeeping.rest.exception.MandatoryPropertyNotFound;
import bookeeping.rest.request.Request;
import bookeeping.rest.request.expect.CommitProperty;
import bookeeping.rest.request.expect.DirectoryProperty;
import bookeeping.rest.request.expect.FileProperty;
import bookeeping.rest.request.expect.FilesystemProperty;
import bookeeping.rest.request.expect.GenericProperty;
import bookeeping.rest.request.expect.UserProperty;
import bookeeping.rest.response.HttpCodes;
import bookeeping.rest.response.Response;
import bookeeping.rest.service.database.DirectoryDatabaseService;
import bookeeping.rest.service.database.FileDatabaseService;
import bookeeping.rest.service.database.FilesystemDatabaseService;
import bookeeping.rest.service.database.GenericDatabaseService;

@Path("/")
public class Commit
{
	@POST
	@Path("/commit")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String commit(InputStream inputStream)
	{
		Response response = new Response();
		try
		{
			Request request = new Request(inputStream);
			JSONObject requestJson = request.getRequestObject();
			JSONObject data = new JSONObject();
			
			String commitId = null;
			try
			{
				commitId = (String) requestJson.remove(CommitProperty.commitid.name());
				if(commitId == null) throw new ClassCastException();
			}
			catch(ClassCastException classCastException)
			{
				throw new MandatoryPropertyNotFound("ERROR: Required property - \"commitId(String)\"");
			}
			
			@SuppressWarnings("unchecked") Iterator<String> commitOrders = requestJson.keys();
			List<Integer> sortedOrderList = new ArrayList<Integer>();
			while(commitOrders.hasNext())
			{
				Integer order = Integer.parseInt(commitOrders.next());
				sortedOrderList.add(order);
			}
			Collections.sort(sortedOrderList);
			
			boolean success = true;
			for(Integer order : sortedOrderList)
			{
				JSONObject operationJson = requestJson.getJSONObject(order.toString());
				String key = null;
				try
				{
					key = (String) operationJson.keys().next();
				}
				catch(NoSuchElementException noSuchElementException)
				{
					throw new MandatoryPropertyNotFound("ERROR: Missing operation!");
				}
				
				JSONObject commandJson = operationJson.getJSONObject(key);
				Response operationResult = null;
				CommitProperty commitProperty = null;
				try
				{
					commitProperty = CommitProperty.valueOf(key);
				}
				catch(IllegalArgumentException illegalArgumentException)
				{
					throw new MandatoryPropertyNotFound("ERROR: Command is not valid! - \"" + key + "\"");
				}
				
				switch(commitProperty)
				{
					case node_version:
					{
						try
						{
							String nodeId = null;
							try
							{
								nodeId = (String) commandJson.get(GenericProperty.nodeid.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Node_Version) - \"nodeId(String)\"");
							}
							
							Map<String, Object> changeMetadata = new HashMap<String, Object>();
							Map<String, Object> changedProperties = new HashMap<String, Object>();
							operationResult = new GenericDatabaseService().createNewVersion(commitId, nodeId, changeMetadata, changedProperties);
							data.put(CommitProperty.node_version.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case node_delete:
					{
						try
						{
							String nodeId = null;
							try
							{
								nodeId = (String) commandJson.get(GenericProperty.nodeid.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Node_Delete) - \"nodeId(String)\"");
							}
							
							operationResult = new GenericDatabaseService().deleteNodeTemporarily(commitId, nodeId);
							data.put(CommitProperty.node_delete.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case filesystem_create:
					{
						try
						{
							String userId = null, filesystemId = null;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Filesystem_Create) - \"userId(String) | filesystemId(String)\"");
							}
							
							Map<String, Object> filesystemProperties = new HashMap<String, Object>();
							operationResult = new FilesystemDatabaseService().createNewFilesystem(commitId, userId, filesystemId, filesystemProperties);
							data.put(CommitProperty.filesystem_create.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case filesystem_restore:
					{
						try
						{
							String userId = null, filesystemId = null, nodeIdToBeRestored = null;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
								nodeIdToBeRestored = (String) commandJson.get(GenericProperty.nodeidtoberestored.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Filesystem_Restore) - \"userId(String) | filesystemId(String) | nodeIdToBeRestored(String)\"");
							}
							
							operationResult = new FilesystemDatabaseService().restoreFilesystem(commitId, userId, filesystemId, nodeIdToBeRestored);
							data.put(CommitProperty.filesystem_restore.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case directory_create:
					{
						try
						{
							String userId = null, filesystemId = null, directoryPath = null, directoryName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemversion.name());
								directoryPath = (String) commandJson.get(DirectoryProperty.directorypath.name());
								directoryName = (String) commandJson.get(DirectoryProperty.directoryname.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Directory_Create) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | directoryPath(String) | directoryName(String)\"");
							}
							
							Map<String, Object> directoryProperties = new HashMap<String, Object>();
							operationResult = new DirectoryDatabaseService().createNewDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, directoryProperties);
							data.put(CommitProperty.directory_create.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case directory_restore:
					{
						try
						{
							String userId = null, filesystemId = null, directoryPath = null, directoryName = null, nodeIdToBeRestored = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemversion.name());
								directoryPath = (String) commandJson.get(DirectoryProperty.directorypath.name());
								directoryName = (String) commandJson.get(DirectoryProperty.directoryname.name());
								nodeIdToBeRestored = (String) commandJson.get(GenericProperty.nodeidtoberestored.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Directory_Restore) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | directoryPath(String) | directoryName(String) | nodeIdToBeRestored(String)\"");
							}
							
							operationResult = new DirectoryDatabaseService().restoreDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, nodeIdToBeRestored);
							data.put(CommitProperty.directory_restore.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case directory_move:
					{
						try
						{
							String userId = null, filesystemId = null, oldDirectoryPath = null, oldDirectoryName = null, newDirectoryPath = null, newDirectoryName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemversion.name());
								oldDirectoryPath = (String) commandJson.get(DirectoryProperty.olddirectorypath.name());
								oldDirectoryName = (String) commandJson.get(DirectoryProperty.olddirectoryname.name());
								newDirectoryPath = (String) commandJson.get(DirectoryProperty.newdirectorypath.name());
								newDirectoryName = (String) commandJson.get(DirectoryProperty.newdirectoryname.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Directory_Move) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | oldDirectoryPath(String) | oldDirectoryName(String) | newDirectoryPath(String) | newDirectoryName(String)\"");
							}
							
							operationResult = new DirectoryDatabaseService().moveDirectory(commitId, userId, filesystemId, filesystemVersion, oldDirectoryPath, oldDirectoryName, newDirectoryPath, newDirectoryName);
							data.put(CommitProperty.directory_move.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_create:
					{
						try
						{
							String userId = null, filesystemId = null, filePath = null, fileName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemversion.name());
								filePath = (String) commandJson.get(FileProperty.filepath.name());
								fileName = (String) commandJson.get(FileProperty.filename.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Create) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String)\"");
							}
							
							Map<String, Object> fileProperties = new HashMap<String, Object>();
							operationResult = new FileDatabaseService().createNewFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, fileProperties);
							data.put(CommitProperty.file_create.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_restore:
					{
						try
						{
							String userId = null, filesystemId = null, filePath = null, fileName = null, nodeIdToBeRestored = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemversion.name());
								filePath = (String) commandJson.get(FileProperty.filepath.name());
								fileName = (String) commandJson.get(FileProperty.filename.name());
								nodeIdToBeRestored = (String) commandJson.get(GenericProperty.nodeidtoberestored.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Restore) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String) | nodeIdToBeRestored(String)\"");
							}
							
							operationResult = new FileDatabaseService().restoreFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, nodeIdToBeRestored);
							data.put(CommitProperty.file_restore.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_move:
					{
						try
						{
							String userId = null, filesystemId = null, oldFilePath = null, oldFileName = null, newFilePath = null, newFileName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemversion.name());
								oldFilePath = (String) commandJson.get(FileProperty.oldfilepath.name());
								oldFileName = (String) commandJson.get(FileProperty.oldfilename.name());
								newFilePath = (String) commandJson.get(FileProperty.newfilepath.name());
								newFileName = (String) commandJson.get(FileProperty.newfilename.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Move) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | oldFilePath(String) | oldFileName(String) | newFilePath(String) | newFileName(String)\"");
							}
							
							operationResult = new FileDatabaseService().moveFile(commitId, userId, filesystemId, filesystemVersion, oldFilePath, oldFileName, newFilePath, newFileName);
							data.put(CommitProperty.file_move.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_share:
					{
						try
						{
							String userId = null, filesystemId = null, filePath = null, fileName = null, shareWithUserId = null, filePermission = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemversion.name());
								filePath = (String) commandJson.get(FileProperty.filepath.name());
								fileName = (String) commandJson.get(FileProperty.filename.name());
								shareWithUserId = (String) commandJson.get(UserProperty.sharewithuserid.name());
								filePermission = (String) commandJson.get(FileProperty.filepermission.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Share) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String) | shareWithUserId(String) | filePermission(String)\"");
							}
							
							operationResult = new FileDatabaseService().shareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, shareWithUserId, filePermission);
							data.put(CommitProperty.file_share.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_unshare:
					{
						try
						{
							String userId = null, filesystemId = null, filePath = null, fileName = null, unshareWithUserId = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userid.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemversion.name());
								filePath = (String) commandJson.get(FileProperty.filepath.name());
								fileName = (String) commandJson.get(FileProperty.filename.name());
								unshareWithUserId = (String) commandJson.get(UserProperty.unsharewithuserid.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Unshare) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String) | unshareWithUserId(String)\"");
							}
							
							operationResult = new FileDatabaseService().unshareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, unshareWithUserId);
							data.put(CommitProperty.file_unshare.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					default:
					{
						break;
					}
				}
				
				int statusCode = (int) operationResult.getResponseObject().get("status_code");
				if(statusCode >= 300)
				{
					success = false;
					break;
				}
			}
			
			response.addData(data);
			if(success)
			{
				return response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: commit successful - \"" + commitId + "\"");
			}
			else
			{
				return response.addStatusAndOperation(HttpCodes.NOTFOUND, "success", "ERROR: commit failed - \"" + commitId + "\"");
			}
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
