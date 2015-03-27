$(document).ready(function()
{
	$("#sendGetFile").click(function()
	{
		var data = '{"userId": "rash", "filesystemId": "rash-filesystem-1", "filesystemVersion": 0, "filePath": "/", "fileName": "todo.txt"}';
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