package edu.msoe.tutorial.process.resources;

import com.codahale.dropwizard.hibernate.UnitOfWork;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.msoe.tutorial.process.core.Content;
import edu.msoe.tutorial.process.db.ContentDao;
import edu.msoe.tutorial.process.db.RatingDao;
import edu.msoe.tutorial.process.exception.ResponseException;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.UUID;

@Path("/content")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContentResource {

    private final ContentDao contentDao;
    private final RatingDao ratingDao;

    public ContentResource(ContentDao contentDao, RatingDao ratingDao) {
        this.contentDao = contentDao;
        this.ratingDao = ratingDao;
    }

    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Content post(@Valid Content content) {
        content.setId(UUID.randomUUID().toString());
        contentDao.create(content);

        return content;
    }

    @PUT
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Content put(@PathParam("id") String id, Content content) {
        Content existingContent = contentDao.retrieve(id);
        if (existingContent == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "Content with id " + id + " does not exist");
        }
        if (content.getTitle() != null) {
            existingContent.setTitle(content.getTitle());
        }
        if (content.getDescription() != null) {
            existingContent.setDescription(content.getDescription());
        }
        if (content.getVideo() != null) {
            existingContent.setVideo(content.getVideo());
        }
        contentDao.update(existingContent);

        return existingContent;
    }

    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Content get(@PathParam("id") String id) {
        Content existingContent = contentDao.retrieve(id);
        if (existingContent == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "Content with id " + id + " does not exist");
        }

        return existingContent;
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void delete(@PathParam("id") String id) {
        Content existingContent = contentDao.retrieve(id);
        if (existingContent == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "Content with id " + id + " does not exist");
        }
        contentDao.delete(existingContent);
        ratingDao.deleteAll(id);
    }

    @GET
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Set<Content> getAll() {
        Set<Content> content = contentDao.retrieveAll();
        return content;
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() { }

    @OPTIONS
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void optionsWithId() { }
}
