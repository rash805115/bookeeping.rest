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
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response commit(InputStream inputStream)
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
				commitId = (String) requestJson.remove(CommitProperty.commitId.name());
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
					case NODE_VERSION:
					{
						try
						{
							String nodeId = null;
							try
							{
								nodeId = (String) commandJson.remove(GenericProperty.nodeId.name());
								if(nodeId == null) throw new JSONException("");
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (NODE_VERSION) - \"nodeId(String)\"");
							}
							
							Map<String, Object> changeMetadata = new HashMap<String, Object>();
							Map<String, Object> changedProperties = new HashMap<String, Object>();
							@SuppressWarnings("unchecked") Iterator<Object> keyset = commandJson.keys();
							while(keyset.hasNext())
							{
								try
								{
									String propertyKey = (String) keyset.next();
									if(propertyKey.equalsIgnoreCase(CommitProperty.CHANGE_METADATA.name()))
									{
										JSONObject changeMetadataProperties = commandJson.getJSONObject(propertyKey);
										@SuppressWarnings("unchecked") Iterator<Object> changeMetadataKeyset = changeMetadataProperties.keys();
										while(changeMetadataKeyset.hasNext())
										{
											String metadataKey = (String) changeMetadataKeyset.next();
											changeMetadata.put(metadataKey, changeMetadataProperties.get(metadataKey));
										}
									}
									else
									{
										changedProperties.put(propertyKey, commandJson.get(propertyKey));
									}
								}
								catch(JSONException jsonException)
								{
									throw new ClassCastException("ERROR: Metadata properties must be inside a JSON structure.");
								}
								catch(ClassCastException e)
								{
									throw new ClassCastException("ERROR: Optional property keys must be string.");
								}
							}
							
							operationResult = new GenericDatabaseService().createNewVersion(commitId, nodeId, changeMetadata, changedProperties);
							data.put(CommitProperty.NODE_VERSION.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case NODE_DELETE:
					{
						try
						{
							String nodeId = null;
							try
							{
								nodeId = (String) commandJson.get(GenericProperty.nodeId.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (NODE_DELETE) - \"nodeId(String)\"");
							}
							
							operationResult = new GenericDatabaseService().deleteNodeTemporarily(commitId, nodeId);
							data.put(CommitProperty.NODE_DELETE.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case FILESYSTEM_RESTORE:
					{
						try
						{
							String userId = null, filesystemId = null, nodeIdToBeRestored = null;
							try
							{
								userId = (String) commandJson.get(UserProperty.userId.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemId.name());
								nodeIdToBeRestored = (String) commandJson.get(GenericProperty.nodeIdToBeRestored.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (FILESYSTEM_RESTORE) - \"userId(String) | filesystemId(String) | nodeIdToBeRestored(String)\"");
							}
							
							operationResult = new FilesystemDatabaseService().restoreFilesystem(commitId, userId, filesystemId, nodeIdToBeRestored);
							data.put(CommitProperty.FILESYSTEM_RESTORE.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case DIRECTORY_CREATE:
					{
						try
						{
							String userId = null, filesystemId = null, directoryPath = null, directoryName = null;
							Integer filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.remove(UserProperty.userId.name());
								filesystemId = (String) commandJson.remove(FilesystemProperty.filesystemId.name());
								filesystemVersion = (Integer) commandJson.remove(FilesystemProperty.filesystemVersion.name());
								directoryPath = (String) commandJson.remove(DirectoryProperty.directoryPath.name());
								directoryName = (String) commandJson.remove(DirectoryProperty.directoryName.name());
								if(userId == null || filesystemId == null || filesystemVersion == null || directoryPath == null || directoryName == null) throw new JSONException("");
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (DIRECTORY_CREATE) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | directoryPath(String) | directoryName(String)\"");
							}
							
							Map<String, Object> directoryProperties = new HashMap<String, Object>();
							@SuppressWarnings("unchecked") Iterator<Object> keyset = commandJson.keys();
							while(keyset.hasNext())
							{
								try
								{
									String propertyKey = (String) keyset.next();
									directoryProperties.put(propertyKey, commandJson.get(propertyKey));
								}
								catch(JSONException jsonException) {}
								catch(ClassCastException e)
								{
									throw new ClassCastException("ERROR: Optional property keys must be string.");
								}
							}
							
							operationResult = new DirectoryDatabaseService().createNewDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, directoryProperties);
							data.put(CommitProperty.DIRECTORY_CREATE.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case DIRECTORY_RESTORE:
					{
						try
						{
							String userId = null, filesystemId = null, directoryPath = null, directoryName = null, nodeIdToBeRestored = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userId.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemId.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemVersion.name());
								directoryPath = (String) commandJson.get(DirectoryProperty.directoryPath.name());
								directoryName = (String) commandJson.get(DirectoryProperty.directoryName.name());
								nodeIdToBeRestored = (String) commandJson.get(GenericProperty.nodeIdToBeRestored.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (DIRECTORY_RESTORE) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | directoryPath(String) | directoryName(String) | nodeIdToBeRestored(String)\"");
							}
							
							operationResult = new DirectoryDatabaseService().restoreDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, nodeIdToBeRestored);
							data.put(CommitProperty.DIRECTORY_RESTORE.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case DIRECTORY_MOVE:
					{
						try
						{
							String userId = null, filesystemId = null, oldDirectoryPath = null, oldDirectoryName = null, newDirectoryPath = null, newDirectoryName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userId.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemId.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemVersion.name());
								oldDirectoryPath = (String) commandJson.get(DirectoryProperty.oldDirectoryPath.name());
								oldDirectoryName = (String) commandJson.get(DirectoryProperty.oldDirectoryName.name());
								newDirectoryPath = (String) commandJson.get(DirectoryProperty.newDirectoryPath.name());
								newDirectoryName = (String) commandJson.get(DirectoryProperty.newDirectoryName.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (DIRECTORY_MOVE) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | oldDirectoryPath(String) | oldDirectoryName(String) | newDirectoryPath(String) | newDirectoryName(String)\"");
							}
							
							operationResult = new DirectoryDatabaseService().moveDirectory(commitId, userId, filesystemId, filesystemVersion, oldDirectoryPath, oldDirectoryName, newDirectoryPath, newDirectoryName);
							data.put(CommitProperty.DIRECTORY_MOVE.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case FILE_CREATE:
					{
						try
						{
							String userId = null, filesystemId = null, filePath = null, fileName = null;
							Integer filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.remove(UserProperty.userId.name());
								filesystemId = (String) commandJson.remove(FilesystemProperty.filesystemId.name());
								filesystemVersion = (Integer) commandJson.remove(FilesystemProperty.filesystemVersion.name());
								filePath = (String) commandJson.remove(FileProperty.filePath.name());
								fileName = (String) commandJson.remove(FileProperty.fileName.name());
								if(userId == null || filesystemId == null || filesystemVersion == null || filePath == null || fileName == null) throw new JSONException("");
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (FILE_CREATE) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String)\"");
							}
							
							Map<String, Object> fileProperties = new HashMap<String, Object>();
							@SuppressWarnings("unchecked") Iterator<Object> keyset = commandJson.keys();
							while(keyset.hasNext())
							{
								try
								{
									String propertyKey = (String) keyset.next();
									fileProperties.put(propertyKey, commandJson.get(propertyKey));
								}
								catch(JSONException jsonException) {}
								catch(ClassCastException e)
								{
									throw new ClassCastException("ERROR: Optional property keys must be string.");
								}
							}
							operationResult = new FileDatabaseService().createNewFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, fileProperties);
							data.put(CommitProperty.FILE_CREATE.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case FILE_RESTORE:
					{
						try
						{
							String userId = null, filesystemId = null, filePath = null, fileName = null, nodeIdToBeRestored = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userId.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemId.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemVersion.name());
								filePath = (String) commandJson.get(FileProperty.filePath.name());
								fileName = (String) commandJson.get(FileProperty.fileName.name());
								nodeIdToBeRestored = (String) commandJson.get(GenericProperty.nodeIdToBeRestored.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (FILE_RESTORE) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String) | nodeIdToBeRestored(String)\"");
							}
							
							operationResult = new FileDatabaseService().restoreFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, nodeIdToBeRestored);
							data.put(CommitProperty.FILE_RESTORE.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case FILE_MOVE:
					{
						try
						{
							String userId = null, filesystemId = null, oldFilePath = null, oldFileName = null, newFilePath = null, newFileName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userId.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemId.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemVersion.name());
								oldFilePath = (String) commandJson.get(FileProperty.oldFilePath.name());
								oldFileName = (String) commandJson.get(FileProperty.oldFileName.name());
								newFilePath = (String) commandJson.get(FileProperty.newFilePath.name());
								newFileName = (String) commandJson.get(FileProperty.newFileName.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (FILE_MOVE) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | oldFilePath(String) | oldFileName(String) | newFilePath(String) | newFileName(String)\"");
							}
							
							operationResult = new FileDatabaseService().moveFile(commitId, userId, filesystemId, filesystemVersion, oldFilePath, oldFileName, newFilePath, newFileName);
							data.put(CommitProperty.FILE_MOVE.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case FILE_SHARE:
					{
						try
						{
							String userId = null, filesystemId = null, filePath = null, fileName = null, shareWithUserId = null, filePermission = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userId.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemId.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemVersion.name());
								filePath = (String) commandJson.get(FileProperty.filePath.name());
								fileName = (String) commandJson.get(FileProperty.fileName.name());
								shareWithUserId = (String) commandJson.get(UserProperty.shareWithUserId.name());
								filePermission = (String) commandJson.get(FileProperty.filePermission.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (FILE_SHARE) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String) | shareWithUserId(String) | filePermission(String)\"");
							}
							
							operationResult = new FileDatabaseService().shareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, shareWithUserId, filePermission);
							data.put(CommitProperty.FILE_SHARE.name(), operationResult.getResponseObject());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case FILE_UNSHARE:
					{
						try
						{
							String userId = null, filesystemId = null, filePath = null, fileName = null, unshareWithUserId = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) commandJson.get(UserProperty.userId.name());
								filesystemId = (String) commandJson.get(FilesystemProperty.filesystemId.name());
								filesystemVersion = (int) commandJson.get(FilesystemProperty.filesystemVersion.name());
								filePath = (String) commandJson.get(FileProperty.filePath.name());
								fileName = (String) commandJson.get(FileProperty.fileName.name());
								unshareWithUserId = (String) commandJson.get(UserProperty.unshareWithUserId.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (FILE_UNSHARE) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String) | unshareWithUserId(String)\"");
							}
							
							operationResult = new FileDatabaseService().unshareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, unshareWithUserId);
							data.put(CommitProperty.FILE_UNSHARE.name(), operationResult.getResponseObject());
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
				if(statusCode >= 400)
				{
					success = false;
					break;
				}
			}
			
			response.addData(data);
			if(success)
			{
				response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: commit successful - \"" + commitId + "\"");
				return response.getServerResponse();
			}
			else
			{
				response.addStatusAndOperation(HttpCodes.NOTFOUND, "failure", "ERROR: commit failed - \"" + commitId + "\"");
				return response.getServerResponse();
			}
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
