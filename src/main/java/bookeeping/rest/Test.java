package bookeeping.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import bookeeping.backend.database.service.AutoIncrementService;
import bookeeping.backend.database.service.neo4jembedded.impl.AutoIncrementServiceImpl;

@Path("/test")
public class Test
{
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt()
	{
		AutoIncrementService autoIncrementService = new AutoIncrementServiceImpl();
		return autoIncrementService.getNextAutoIncrement();
	}
}
