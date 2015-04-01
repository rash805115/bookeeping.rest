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
			
			boolean success = true;
			@SuppressWarnings("unchecked") Iterator<String> keys = requestJson.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				CommitProperty commitProperty = CommitProperty.valueOf(key);
				Response operationResult = null;
				
				switch(commitProperty)
				{
					case node_version:
					{
						try
						{
							JSONObject nodeVersionJson = requestJson.getJSONObject(CommitProperty.node_version.name());
							String nodeId = null;
							try
							{
								nodeId = (String) nodeVersionJson.get(GenericProperty.nodeid.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Node_Version) - \"nodeId(String)\"");
							}
							
							Map<String, Object> changeMetadata = new HashMap<String, Object>();
							Map<String, Object> changedProperties = new HashMap<String, Object>();
							operationResult = GenericDatabaseService.getInstance().createNewVersion(commitId, nodeId, changeMetadata, changedProperties);
							data.put(CommitProperty.node_version.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case node_delete:
					{
						try
						{
							JSONObject nodeDeleteJson = requestJson.getJSONObject(CommitProperty.node_delete.name());
							String nodeId = null;
							try
							{
								nodeId = (String) nodeDeleteJson.get(GenericProperty.nodeid.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Node_Delete) - \"nodeId(String)\"");
							}
							
							operationResult = GenericDatabaseService.getInstance().deleteNodeTemporarily(commitId, nodeId);
							data.put(CommitProperty.node_delete.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case filesystem_create:
					{
						try
						{
							JSONObject filesystemCreateJson = requestJson.getJSONObject(CommitProperty.filesystem_create.name());
							String userId = null, filesystemId = null;
							try
							{
								userId = (String) filesystemCreateJson.get(UserProperty.userid.name());
								filesystemId = (String) filesystemCreateJson.get(FilesystemProperty.filesystemid.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Filesystem_Create) - \"userId(String) | filesystemId(String)\"");
							}
							
							Map<String, Object> filesystemProperties = new HashMap<String, Object>();
							operationResult = FilesystemDatabaseService.getInstance().createNewFilesystem(commitId, userId, filesystemId, filesystemProperties);
							data.put(CommitProperty.filesystem_create.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case filesystem_restore:
					{
						try
						{
							JSONObject filesystemRestoreJson = requestJson.getJSONObject(CommitProperty.filesystem_restore.name());
							String userId = null, filesystemId = null, nodeIdToBeRestored = null;
							try
							{
								userId = (String) filesystemRestoreJson.get(UserProperty.userid.name());
								filesystemId = (String) filesystemRestoreJson.get(FilesystemProperty.filesystemid.name());
								nodeIdToBeRestored = (String) filesystemRestoreJson.get(GenericProperty.nodeidtoberestored.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Filesystem_Restore) - \"userId(String) | filesystemId(String) | nodeIdToBeRestored(String)\"");
							}
							
							operationResult = FilesystemDatabaseService.getInstance().restoreFilesystem(commitId, userId, filesystemId, nodeIdToBeRestored);
							data.put(CommitProperty.filesystem_restore.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case directory_create:
					{
						try
						{
							JSONObject directoryCreateJson = requestJson.getJSONObject(CommitProperty.directory_create.name());
							String userId = null, filesystemId = null, directoryPath = null, directoryName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) directoryCreateJson.get(UserProperty.userid.name());
								filesystemId = (String) directoryCreateJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) directoryCreateJson.get(FilesystemProperty.filesystemversion.name());
								directoryPath = (String) directoryCreateJson.get(DirectoryProperty.directorypath.name());
								directoryName = (String) directoryCreateJson.get(DirectoryProperty.directoryname.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Directory_Create) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | directoryPath(String) | directoryName(String)\"");
							}
							
							Map<String, Object> directoryProperties = new HashMap<String, Object>();
							operationResult = DirectoryDatabaseService.getInstance().createNewDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, directoryProperties);
							data.put(CommitProperty.directory_create.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case directory_restore:
					{
						try
						{
							JSONObject directoryRestoreJson = requestJson.getJSONObject(CommitProperty.directory_restore.name());
							String userId = null, filesystemId = null, directoryPath = null, directoryName = null, nodeIdToBeRestored = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) directoryRestoreJson.get(UserProperty.userid.name());
								filesystemId = (String) directoryRestoreJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) directoryRestoreJson.get(FilesystemProperty.filesystemversion.name());
								directoryPath = (String) directoryRestoreJson.get(DirectoryProperty.directorypath.name());
								directoryName = (String) directoryRestoreJson.get(DirectoryProperty.directoryname.name());
								nodeIdToBeRestored = (String) directoryRestoreJson.get(GenericProperty.nodeidtoberestored.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Directory_Restore) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | directoryPath(String) | directoryName(String) | nodeIdToBeRestored(String)\"");
							}
							
							operationResult = DirectoryDatabaseService.getInstance().restoreDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, nodeIdToBeRestored);
							data.put(CommitProperty.directory_restore.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case directory_move:
					{
						try
						{
							JSONObject directoryMoveJson = requestJson.getJSONObject(CommitProperty.directory_move.name());
							String userId = null, filesystemId = null, oldDirectoryPath = null, oldDirectoryName = null, newDirectoryPath = null, newDirectoryName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) directoryMoveJson.get(UserProperty.userid.name());
								filesystemId = (String) directoryMoveJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) directoryMoveJson.get(FilesystemProperty.filesystemversion.name());
								oldDirectoryPath = (String) directoryMoveJson.get(DirectoryProperty.olddirectorypath.name());
								oldDirectoryName = (String) directoryMoveJson.get(DirectoryProperty.olddirectoryname.name());
								newDirectoryPath = (String) directoryMoveJson.get(DirectoryProperty.newdirectorypath.name());
								newDirectoryName = (String) directoryMoveJson.get(DirectoryProperty.newdirectoryname.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (Directory_Move) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | oldDirectoryPath(String) | oldDirectoryName(String) | newDirectoryPath(String) | newDirectoryName(String)\"");
							}
							
							operationResult = DirectoryDatabaseService.getInstance().moveDirectory(commitId, userId, filesystemId, filesystemVersion, oldDirectoryPath, oldDirectoryName, newDirectoryPath, newDirectoryName);
							data.put(CommitProperty.directory_move.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_create:
					{
						try
						{
							JSONObject fileCreateJson = requestJson.getJSONObject(CommitProperty.file_create.name());
							String userId = null, filesystemId = null, filePath = null, fileName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) fileCreateJson.get(UserProperty.userid.name());
								filesystemId = (String) fileCreateJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) fileCreateJson.get(FilesystemProperty.filesystemversion.name());
								filePath = (String) fileCreateJson.get(FileProperty.filepath.name());
								fileName = (String) fileCreateJson.get(FileProperty.filename.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Create) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String)\"");
							}
							
							Map<String, Object> fileProperties = new HashMap<String, Object>();
							operationResult = FileDatabaseService.getInstance().createNewFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, fileProperties);
							data.put(CommitProperty.file_create.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_restore:
					{
						try
						{
							JSONObject fileRestoreJson = requestJson.getJSONObject(CommitProperty.file_restore.name());
							String userId = null, filesystemId = null, filePath = null, fileName = null, nodeIdToBeRestored = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) fileRestoreJson.get(UserProperty.userid.name());
								filesystemId = (String) fileRestoreJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) fileRestoreJson.get(FilesystemProperty.filesystemversion.name());
								filePath = (String) fileRestoreJson.get(FileProperty.filepath.name());
								fileName = (String) fileRestoreJson.get(FileProperty.filename.name());
								nodeIdToBeRestored = (String) fileRestoreJson.get(GenericProperty.nodeidtoberestored.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Restore) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String) | nodeIdToBeRestored(String)\"");
							}
							
							operationResult = FileDatabaseService.getInstance().restoreFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, nodeIdToBeRestored);
							data.put(CommitProperty.file_restore.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_move:
					{
						try
						{
							JSONObject fileMoveJson = requestJson.getJSONObject(CommitProperty.file_move.name());
							String userId = null, filesystemId = null, oldFilePath = null, oldFileName = null, newFilePath = null, newFileName = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) fileMoveJson.get(UserProperty.userid.name());
								filesystemId = (String) fileMoveJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) fileMoveJson.get(FilesystemProperty.filesystemversion.name());
								oldFilePath = (String) fileMoveJson.get(FileProperty.oldfilepath.name());
								oldFileName = (String) fileMoveJson.get(FileProperty.oldfilename.name());
								newFilePath = (String) fileMoveJson.get(FileProperty.newfilepath.name());
								newFileName = (String) fileMoveJson.get(FileProperty.newfilename.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Move) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | oldFilePath(String) | oldFileName(String) | newFilePath(String) | newFileName(String)\"");
							}
							
							operationResult = FileDatabaseService.getInstance().moveFile(commitId, userId, filesystemId, filesystemVersion, oldFilePath, oldFileName, newFilePath, newFileName);
							data.put(CommitProperty.file_move.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_share:
					{
						try
						{
							JSONObject fileShareJson = requestJson.getJSONObject(CommitProperty.file_share.name());
							String userId = null, filesystemId = null, filePath = null, fileName = null, shareWithUserId = null, filePermission = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) fileShareJson.get(UserProperty.userid.name());
								filesystemId = (String) fileShareJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) fileShareJson.get(FilesystemProperty.filesystemversion.name());
								filePath = (String) fileShareJson.get(FileProperty.filepath.name());
								fileName = (String) fileShareJson.get(FileProperty.filename.name());
								shareWithUserId = (String) fileShareJson.get(UserProperty.sharewithuserid.name());
								filePermission = (String) fileShareJson.get(FileProperty.filepermission.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Share) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String) | shareWithUserId(String) | filePermission(String)\"");
							}
							
							operationResult = FileDatabaseService.getInstance().shareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, shareWithUserId, filePermission);
							data.put(CommitProperty.file_share.name(), operationResult.getResponseString());
						}
						catch(JSONException jsonException) {}
						break;
					}
					case file_unshare:
					{
						try
						{
							JSONObject fileUnshareJson = requestJson.getJSONObject(CommitProperty.file_unshare.name());
							String userId = null, filesystemId = null, filePath = null, fileName = null, unshareWithUserId = null;
							int filesystemVersion = -1;
							try
							{
								userId = (String) fileUnshareJson.get(UserProperty.userid.name());
								filesystemId = (String) fileUnshareJson.get(FilesystemProperty.filesystemid.name());
								filesystemVersion = (int) fileUnshareJson.get(FilesystemProperty.filesystemversion.name());
								filePath = (String) fileUnshareJson.get(FileProperty.filepath.name());
								fileName = (String) fileUnshareJson.get(FileProperty.filename.name());
								unshareWithUserId = (String) fileUnshareJson.get(UserProperty.unsharewithuserid.name());
							}
							catch(JSONException | ClassCastException e)
							{
								throw new MandatoryPropertyNotFound("ERROR: Required property (File_Unshare) - \"userId(String) | filesystemId(String) | filesystemVersion(Integer) | filePath(String) | fileName(String) | unshareWithUserId(String)\"");
							}
							
							operationResult = FileDatabaseService.getInstance().unshareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, unshareWithUserId);
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
