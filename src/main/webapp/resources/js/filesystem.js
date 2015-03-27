$(document).ready(function()
{
	$("#sendGetFilesystem").click(function()
	{
		var data = '{"userId": "rash", "filesystemId": "rash-filesystem-1"}';
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