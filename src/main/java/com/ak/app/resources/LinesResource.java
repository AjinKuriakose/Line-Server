package com.ak.app.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/lines")
public class LinesResource {
	@Autowired
	private LineServer lineServer;
	/*
	 * Handles GET /lines/{lineid}.
	 * Sends http status 413 for lines that are out of range.
	 * 
	 */
	@GET
	@Path("/{lineid}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getLine(@PathParam("lineid") int index) {
		String line = new String();
		try {
			line = lineServer.getLinefromIndex(index);
		} catch (Exception e) {
			return Response.status(Status.REQUEST_ENTITY_TOO_LARGE).build();
		}
		return Response.status(Status.ACCEPTED).entity(line).build();
	}
}
