package edu.msoe.tutorial.process.resources;

import com.codahale.dropwizard.hibernate.UnitOfWork;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.msoe.tutorial.process.auth.Authorized;
import edu.msoe.tutorial.process.core.Rating;
import edu.msoe.tutorial.process.core.User;
import edu.msoe.tutorial.process.db.ContentDao;
import edu.msoe.tutorial.process.db.RatingDao;
import edu.msoe.tutorial.process.exception.ResponseException;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/content/{contentId}/rate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RatingResource {

    private final ContentDao contentDao;
    private final RatingDao ratingDao;

    public RatingResource(ContentDao contentDao, RatingDao ratingDao) {
        this.contentDao = contentDao;
        this.ratingDao = ratingDao;
    }

    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Rating post(@PathParam("contentId") String contentId, @Authorized User user, @Valid Rating rating) {
        if (contentDao.retrieve(contentId) == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "Content with id " + contentId + " was not found");
        }
        if (ratingDao.retrieve(user.getEmail(), contentId) != null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "You have already rated this content");
        }
        rating.setContent(contentId);
        rating.setUser(user.getEmail());
        ratingDao.create(rating);
        return rating;
    }

    @PUT
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Rating put(@PathParam("contentId") String contentId, @Authorized User user, Rating rating) {
        if (contentDao.retrieve(contentId) == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "Content with id " + contentId + " was not found");
        }
        Rating existingRating = ratingDao.retrieve(user.getEmail(), contentId);
        if (existingRating == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "Rating for content with id " + contentId + " by " + user.getEmail() + " does not exist");
        }
        existingRating.setRating(rating.getRating());
        ratingDao.update(existingRating);
        return existingRating;
    }

    @GET
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Rating get(@PathParam("contentId") String contentId, @Authorized User user) {
        if (contentDao.retrieve(contentId) == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "Content with id " + contentId + " was not found");
        }
        Rating rating = ratingDao.retrieve(user.getEmail(), contentId);
        if (rating == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "Rating for content with id " + contentId + " by " + user.getEmail() + " does not exist");
        }
        return rating;
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() { }
}
