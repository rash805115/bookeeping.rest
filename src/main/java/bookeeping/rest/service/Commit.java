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
				String nodeId = (String) nodeVersionJson.get(GenericProperty.nodeid.name());
				if(nodeId == null) throw new MandatoryPropertyNotFound("ERROR: Required property (Node_Version) - \"nodeId\"");
				
				Map<String, Object> changeMetadata = new HashMap<String, Object>();
				Map<String, Object> changedProperties = new HashMap<String, Object>();
				data.put(CommitProperty.node_version.name(), GenericDatabaseService.getInstance().createNewVersion(commitId, nodeId, changeMetadata, changedProperties));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject nodeDeleteJson = requestJson.getJSONObject(CommitProperty.node_delete.name());
				String nodeId = (String) nodeDeleteJson.get(GenericProperty.nodeid.name());
				if(nodeId == null) throw new MandatoryPropertyNotFound("ERROR: Required property (Node_Delete) - \"nodeId\"");
				
				data.put(CommitProperty.node_delete.name(), GenericDatabaseService.getInstance().deleteNodeTemporarily(commitId, nodeId));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject filesystemCreateJson = requestJson.getJSONObject(CommitProperty.filesystem_create.name());
				String userId = (String) filesystemCreateJson.get(UserProperty.userid.name());
				String filesystemId = (String) filesystemCreateJson.get(FilesystemProperty.filesystemid.name());
				if(userId == null || filesystemId == null) throw new MandatoryPropertyNotFound("ERROR: Required property (Filesystem_Create) - \"userId | filesystemId\"");
				
				Map<String, Object> filesystemProperties = new HashMap<String, Object>();
				data.put(CommitProperty.filesystem_create.name(), FilesystemDatabaseService.getInstance().createNewFilesystem(commitId, userId, filesystemId, filesystemProperties));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject filesystemRestoreJson = requestJson.getJSONObject(CommitProperty.filesystem_restore.name());
				String userId = (String) filesystemRestoreJson.get(UserProperty.userid.name());
				String filesystemId = (String) filesystemRestoreJson.get(FilesystemProperty.filesystemid.name());
				String nodeIdToBeRestored = (String) filesystemRestoreJson.get(GenericProperty.nodeidtoberestored.name());
				if(userId == null || filesystemId == null || nodeIdToBeRestored == null) throw new MandatoryPropertyNotFound("ERROR: Required property (Filesystem_Restore) - \"userId | filesystemId | nodeIdToBeRestored\"");
				
				data.put(CommitProperty.filesystem_restore.name(), FilesystemDatabaseService.getInstance().restoreFilesystem(commitId, userId, filesystemId, nodeIdToBeRestored));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject directoryCreateJson = requestJson.getJSONObject(CommitProperty.directory_create.name());
				String userId = (String) directoryCreateJson.get(UserProperty.userid.name());
				String filesystemId = (String) directoryCreateJson.get(FilesystemProperty.filesystemid.name());
				int filesystemVersion = (int) directoryCreateJson.get(FilesystemProperty.filesystemversion.name());
				String directoryPath = (String) directoryCreateJson.get(DirectoryProperty.directorypath.name());
				String directoryName = (String) directoryCreateJson.get(DirectoryProperty.directoryname.name());
				if(userId == null || filesystemId == null || filesystemVersion < 0 || directoryPath == null || directoryName == null) throw new MandatoryPropertyNotFound("ERROR: Required property (Directory_Create) - \"userId | filesystemId | filesystemVersion | directoryPath | directoryName\"");
				
				Map<String, Object> directoryProperties = new HashMap<String, Object>();
				data.put(CommitProperty.directory_create.name(), DirectoryDatabaseService.getInstance().createNewDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, directoryProperties));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject directoryRestoreJson = requestJson.getJSONObject(CommitProperty.directory_restore.name());
				String userId = (String) directoryRestoreJson.get(UserProperty.userid.name());
				String filesystemId = (String) directoryRestoreJson.get(FilesystemProperty.filesystemid.name());
				int filesystemVersion = (int) directoryRestoreJson.get(FilesystemProperty.filesystemversion.name());
				String directoryPath = (String) directoryRestoreJson.get(DirectoryProperty.directorypath.name());
				String directoryName = (String) directoryRestoreJson.get(DirectoryProperty.directoryname.name());
				String nodeIdToBeRestored = (String) directoryRestoreJson.get(GenericProperty.nodeidtoberestored.name());
				if(userId == null || filesystemId == null || filesystemVersion < 0 || directoryPath == null || directoryName == null || nodeIdToBeRestored == null) throw new MandatoryPropertyNotFound("ERROR: Required property (Directory_Restore) - \"userId | filesystemId | filesystemVersion | directoryPath | directoryName | nodeIdToBeRestored\"");
				
				data.put(CommitProperty.directory_restore.name(), DirectoryDatabaseService.getInstance().restoreDirectory(commitId, userId, filesystemId, filesystemVersion, directoryPath, directoryName, nodeIdToBeRestored));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject directoryMoveJson = requestJson.getJSONObject(CommitProperty.directory_move.name());
				String userId = (String) directoryMoveJson.get(UserProperty.userid.name());
				String filesystemId = (String) directoryMoveJson.get(FilesystemProperty.filesystemid.name());
				int filesystemVersion = (int) directoryMoveJson.get(FilesystemProperty.filesystemversion.name());
				String oldDirectoryPath = (String) directoryMoveJson.get(DirectoryProperty.olddirectorypath.name());
				String oldDirectoryName = (String) directoryMoveJson.get(DirectoryProperty.olddirectoryname.name());
				String newDirectoryPath = (String) directoryMoveJson.get(DirectoryProperty.newdirectorypath.name());
				String newDirectoryName = (String) directoryMoveJson.get(DirectoryProperty.newdirectoryname.name());
				if(userId == null || filesystemId == null || filesystemVersion < 0 || oldDirectoryPath == null || oldDirectoryName == null || newDirectoryPath == null || newDirectoryName == null) throw new MandatoryPropertyNotFound("ERROR: Required property (Directory_Move) - \"userId | filesystemId | filesystemVersion | oldDirectoryPath | oldDirectoryName | newDirectoryPath | newDirectoryName\"");
				
				data.put(CommitProperty.directory_move.name(), DirectoryDatabaseService.getInstance().moveDirectory(commitId, userId, filesystemId, filesystemVersion, oldDirectoryPath, oldDirectoryName, newDirectoryPath, newDirectoryName));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject fileCreateJson = requestJson.getJSONObject(CommitProperty.file_create.name());
				String userId = (String) fileCreateJson.get(UserProperty.userid.name());
				String filesystemId = (String) fileCreateJson.get(FilesystemProperty.filesystemid.name());
				int filesystemVersion = (int) fileCreateJson.get(FilesystemProperty.filesystemversion.name());
				String filePath = (String) fileCreateJson.get(FileProperty.filepath.name());
				String fileName = (String) fileCreateJson.get(FileProperty.filename.name());
				if(userId == null || filesystemId == null || filesystemVersion < 0 || filePath == null || fileName == null) throw new MandatoryPropertyNotFound("ERROR: Required property (File_Create) - \"userId | filesystemId | filesystemVersion | filePath | fileName\"");
				
				Map<String, Object> fileProperties = new HashMap<String, Object>();
				data.put(CommitProperty.file_create.name(), FileDatabaseService.getInstance().createNewFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, fileProperties));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject fileRestoreJson = requestJson.getJSONObject(CommitProperty.file_restore.name());
				String userId = (String) fileRestoreJson.get(UserProperty.userid.name());
				String filesystemId = (String) fileRestoreJson.get(FilesystemProperty.filesystemid.name());
				int filesystemVersion = (int) fileRestoreJson.get(FilesystemProperty.filesystemversion.name());
				String filePath = (String) fileRestoreJson.get(FileProperty.filepath.name());
				String fileName = (String) fileRestoreJson.get(FileProperty.filename.name());
				String nodeIdToBeRestored = (String) fileRestoreJson.get(GenericProperty.nodeidtoberestored.name());
				if(userId == null || filesystemId == null || filesystemVersion < 0 || filePath == null || fileName == null || nodeIdToBeRestored == null) throw new MandatoryPropertyNotFound("ERROR: Required property (File_Restore) - \"userId | filesystemId | filesystemVersion | filePath | fileName | nodeIdToBeRestored\"");
				
				data.put(CommitProperty.file_restore.name(), FileDatabaseService.getInstance().restoreFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, nodeIdToBeRestored));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject fileMoveJson = requestJson.getJSONObject(CommitProperty.file_move.name());
				String userId = (String) fileMoveJson.get(UserProperty.userid.name());
				String filesystemId = (String) fileMoveJson.get(FilesystemProperty.filesystemid.name());
				int filesystemVersion = (int) fileMoveJson.get(FilesystemProperty.filesystemversion.name());
				String oldFilePath = (String) fileMoveJson.get(FileProperty.oldfilepath.name());
				String oldFileName = (String) fileMoveJson.get(FileProperty.oldfilename.name());
				String newFilePath = (String) fileMoveJson.get(FileProperty.newfilepath.name());
				String newFileName = (String) fileMoveJson.get(FileProperty.newfilename.name());
				if(userId == null || filesystemId == null || filesystemVersion < 0 || oldFilePath == null || oldFileName == null || newFilePath == null || newFileName == null) throw new MandatoryPropertyNotFound("ERROR: Required property (File_Move) - \"userId | filesystemId | filesystemVersion | oldFilePath | oldFileName | newFilePath | newFileName\"");
				
				data.put(CommitProperty.file_move.name(), FileDatabaseService.getInstance().moveFile(commitId, userId, filesystemId, filesystemVersion, oldFilePath, oldFileName, newFilePath, newFileName));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject fileShareJson = requestJson.getJSONObject(CommitProperty.file_share.name());
				String userId = (String) fileShareJson.get(UserProperty.userid.name());
				String filesystemId = (String) fileShareJson.get(FilesystemProperty.filesystemid.name());
				int filesystemVersion = (int) fileShareJson.get(FilesystemProperty.filesystemversion.name());
				String filePath = (String) fileShareJson.get(FileProperty.filepath.name());
				String fileName = (String) fileShareJson.get(FileProperty.filename.name());
				String shareWithUserId = (String) fileShareJson.get(UserProperty.sharewithuserid.name());
				String filePermission = (String) fileShareJson.get(FileProperty.filepermission.name());
				if(userId == null || filesystemId == null || filesystemVersion < 0 || filePath == null || fileName == null || shareWithUserId == null || filePermission == null) throw new MandatoryPropertyNotFound("ERROR: Required property (File_Share) - \"userId | filesystemId | filesystemVersion | filePath | fileName | shareWithUserId | filePermission\"");
				
				data.put(CommitProperty.file_share.name(), FileDatabaseService.getInstance().shareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, shareWithUserId, filePermission));
			}
			catch(JSONException jsonException) {}
			
			try
			{
				JSONObject fileUnshareJson = requestJson.getJSONObject(CommitProperty.file_unshare.name());
				String userId = (String) fileUnshareJson.get(UserProperty.userid.name());
				String filesystemId = (String) fileUnshareJson.get(FilesystemProperty.filesystemid.name());
				int filesystemVersion = (int) fileUnshareJson.get(FilesystemProperty.filesystemversion.name());
				String filePath = (String) fileUnshareJson.get(FileProperty.filepath.name());
				String fileName = (String) fileUnshareJson.get(FileProperty.filename.name());
				String unshareWithUserId = (String) fileUnshareJson.get(UserProperty.unsharewithuserid.name());
				if(userId == null || filesystemId == null || filesystemVersion < 0 || filePath == null || fileName == null || unshareWithUserId == null) throw new MandatoryPropertyNotFound("ERROR: Required property (File_Unshare) - \"userId | filesystemId | filesystemVersion | filePath | fileName | unshareWithUserId\"");
				
				data.put(CommitProperty.file_unshare.name(), FileDatabaseService.getInstance().unshareFile(commitId, userId, filesystemId, filesystemVersion, filePath, fileName, unshareWithUserId));
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
