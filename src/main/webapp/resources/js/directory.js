$(document).ready(function()
{
	$("#sendGetDirectory").click(function()
	{
		var data = '{"userId": "rash", "filesystemId": "rash-filesystem-1", "directoryPath": "/", "directoryName": "Music"}';
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