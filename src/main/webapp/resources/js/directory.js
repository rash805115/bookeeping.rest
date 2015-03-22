$(document).ready(function()
{
	$("#sendCreateDirectory").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "directoryPath": "/", "directoryName": "dir1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/directory/create",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseCreateDirectory").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendCreateDirectoryVersion").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "versionChangeType": "duplicate", "directoryPath": "/", "directoryName": "dir1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/directory/create/version",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseCreateDirectoryVersion").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendDeleteDirectoryTemporarily").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "directoryPath": "/", "directoryName": "dir1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/directory/delete/temporary",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseDeleteDirectoryTemporarily").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendRestoreDirectory").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "directoryPath": "/", "directoryName": "dir1", "commitId": "rash-filesystem-1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/directory/restore",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseRestoreDirectory").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendMoveDirectory").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "oldDirectoryPath": "/", "oldDirectoryName": "dir1", "newDirectoryPath": "/", "newDirectoryName": "dir1_mov"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/directory/move",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseMoveDirectory").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendGetDirectory").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "directoryPath": "/", "directoryName": "dir1_mov", "version": 0}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/directory/info",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseGetDirectory").html(JSON.stringify(response));
			}
		});
	});
});