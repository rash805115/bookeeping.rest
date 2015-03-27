package bookeeping.rest.service;

import java.io.InputStream;
import java.util.HashMap;
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
			
			String commitId = requestJson.getString(CommitProperty.commitid.name());
			if(commitId == null) throw new MandatoryPropertyNotFound("ERROR: Required property - \"commitId\"");
			
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
				data.put(CommitProperty.node_version.name(), GenericDatabaseService.getInstance().createNewVersion(commitId, nodeId, changeMetadata, changedProperties).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				
				data.put(CommitProperty.node_delete.name(), GenericDatabaseService.getInstance().deleteNodeTemporarily(commitId, nodeId).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				data.put(CommitProperty.filesystem_create.name(), FilesystemDatabaseService.getInstance().createNewFilesystem(commitId, userId, filesystemId, filesystemProperties).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				
				data.put(CommitProperty.filesystem_restore.name(), FilesystemDatabaseService.getInstance().restoreFilesystem(commitId, userId, filesystemId, nodeIdToBeRestored).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				data.put(CommitProperty.directory_create.name(), DirectoryDatabaseService.getInstance().createNewDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, directoryProperties).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				
				data.put(CommitProperty.directory_restore.name(), DirectoryDatabaseService.getInstance().restoreDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, nodeIdToBeRestored).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				
				data.put(CommitProperty.directory_move.name(), DirectoryDatabaseService.getInstance().moveDirectory(commitId, userId, filesystemId, filesystemVersion, oldDirectoryPath, oldDirectoryName, newDirectoryPath, newDirectoryName).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				data.put(CommitProperty.file_create.name(), FileDatabaseService.getInstance().createNewFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, fileProperties).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				
				data.put(CommitProperty.file_restore.name(), FileDatabaseService.getInstance().restoreFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, nodeIdToBeRestored).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				
				data.put(CommitProperty.file_move.name(), FileDatabaseService.getInstance().moveFile(commitId, userId, filesystemId, filesystemVersion, oldFilePath, oldFileName, newFilePath, newFileName).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				
				data.put(CommitProperty.file_share.name(), FileDatabaseService.getInstance().shareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, shareWithUserId, filePermission).getResponseString());
			}
			catch(JSONException jsonException) {}
			
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
				
				data.put(CommitProperty.file_unshare.name(), FileDatabaseService.getInstance().unshareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, unshareWithUserId).getResponseString());
			}
			catch(JSONException jsonException) {}
			
			response.addData(data);
			return response.addStatusAndOperation(HttpCodes.OK, "success", "INFO: commit successful - \"" + commitId + "\"");
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
