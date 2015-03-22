$(document).ready(function()
{
	$("#sendCreateFile").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "filePath": "/", "fileName": "file1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/file/create",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseCreateFile").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendCreateFileVersion").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "versionChangeType": "duplicate", "filePath": "/", "fileName": "file1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/file/create/version",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseCreateFileVersion").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendDeleteFileTemporarily").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "filePath": "/", "fileName": "dir1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/file/delete/temporary",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseDeleteFileTemporarily").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendRestoreFile").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "filePath": "/", "fileName": "file1", "commitId": "rash-filesystem-1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/file/restore",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseRestoreFile").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendMoveFile").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "oldFilePath": "/", "oldFileName": "file1", "newFilePath": "/", "newFileName": "file1_mov"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/file/move",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseMoveFile").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendGetFile").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "filePath": "/", "fileName": "file1_mov", "version": 0}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/file/info",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseGetFile").html(JSON.stringify(response));
			}
		});
	});
});