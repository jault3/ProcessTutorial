package edu.msoe.tutorial.process.resources;

import com.codahale.dropwizard.hibernate.UnitOfWork;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/shutdown")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShutdownResource {

    private final String shutdownPassword;

    public ShutdownResource(String shutdownPassword) {
        this.shutdownPassword = shutdownPassword;
    }

    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void shutdown(JsonNode body) {
        if (body.has("password") && body.get("password").asText().equals(shutdownPassword)) {
            System.exit(0);
        }
    }

    @OPTIONS
    @UnitOfWork
    @Timed
    @ExceptionMetered
    public void options() { }
}
