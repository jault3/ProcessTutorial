package edu.msoe.tutorial.process.resources;

import com.codahale.dropwizard.hibernate.UnitOfWork;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/healthcheck")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthCheckResource {

    public HealthCheckResource() { }

    @GET
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public ObjectNode check() {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("status", "OK");
        node.put("service", "se3800Final");
        return node;
    }

    @OPTIONS
    @UnitOfWork
    @Timed
    @ExceptionMetered
    public void options() { }
}
