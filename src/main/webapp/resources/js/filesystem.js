$(document).ready(function()
{
	$("#sendCreateFilesystem").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/filesystem/create",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseCreateFilesystem").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendCreateFilesystemVersion").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "versionChangeType": "duplicate"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/filesystem/create/version",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseCreateFilesystemVersion").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendDeleteFilesystemTemporarily").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/filesystem/delete/temporary",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseDeleteFilesystemTemporarily").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendRestoreFilesystem").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/filesystem/restore",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseRestoreFilesystem").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendGetFilesystem").click(function()
	{
		var data = '{"username": "rash", "filesystemId": "rash-filesystem-1", "version": 0}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/filesystem/info",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseGetFilesystem").html(JSON.stringify(response));
			}
		});
	});
});