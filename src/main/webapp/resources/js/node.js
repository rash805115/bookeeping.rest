$(document).ready(function()
{
	$("#sendGetNode").click(function()
	{
		var data = '{"nodeId": "0"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/node/info",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseGetNode").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendGetNodeVersion").click(function()
	{
		var data = '{"nodeId": "4", "version": 1}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/node/version/info",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseGetNodeVersion").html(JSON.stringify(response));
			}
		});
	});
});