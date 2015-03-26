$(document).ready(function()
{
	$("#sendCreateUser").click(function()
	{
		var data = '{"username": "rash", "firstName": "Rahul", "lastName": "Chaudhary"}';
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
				$("#responseCreateUser").html(JSON.stringify(response));
			}
		});
	});
	
	$("#sendGetUser").click(function()
	{
		var data = '{"username": "rash"}';
		$.ajax(
		{
			url: "http://localhost:8080/bookeeping.rest/api/user/info",
			async: false,
			type : "post",
			data : data,
			contentType : "application/json",
			dataType : "json",
			success: function(response)
			{
				$("#responseGetUser").html(JSON.stringify(response));
			}
		});
	});
});