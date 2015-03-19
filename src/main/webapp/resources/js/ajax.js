$(document).ready(function()
{
	var data = '{"username": "rash", "password": "pass"}';
	$("#sendRequest").click(function()
	{
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/user/create",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#response").html(JSON.stringify(response));
			}
		});
	});
});