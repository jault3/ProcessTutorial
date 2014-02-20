package edu.msoe.tutorial.process.resources;

import com.codahale.dropwizard.hibernate.UnitOfWork;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.msoe.tutorial.process.auth.Authorized;
import edu.msoe.tutorial.process.core.Session;
import edu.msoe.tutorial.process.core.User;
import edu.msoe.tutorial.process.core.UserRole;
import edu.msoe.tutorial.process.db.RatingDao;
import edu.msoe.tutorial.process.db.SessionDao;
import edu.msoe.tutorial.process.db.UserDao;
import edu.msoe.tutorial.process.exception.ResponseException;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.Random;

@Path("/user/{email}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserManagementResource {

    private final UserDao userDao;
    private final RatingDao ratingDao;

    public UserManagementResource(UserDao userDao, RatingDao ratingDao) {
        this.userDao = userDao;
        this.ratingDao = ratingDao;
    }

    @PUT
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User put(@PathParam("email") String email, @Authorized(admin = true) User existingUser, User user) {
        User userInQuestion = userDao.retrieve(email);
        if (userInQuestion == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "User with email " + email + " does not exist");
        }
        if (user.getRole() != null) {
            userInQuestion.setRole(user.getRole());
        }
        userDao.update(userInQuestion.getEmail(), userInQuestion.getPassword(), userInQuestion.getSalt(), userInQuestion.getRole().toString());

        return userInQuestion;
    }

    @GET
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User get(@PathParam("email") String email, @Authorized(admin = true) User existingUser) {
        User userInQuestion = userDao.retrieve(email);
        if (userInQuestion == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "User with email " + email + " does not exist");
        }
        return userInQuestion;
    }

    @DELETE
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void delete(@PathParam("email") String email, @Authorized(admin = true) User existingUser, @HeaderParam("Authorization") String sessionToken) {
        User userInQuestion = userDao.retrieve(email);
        if (userInQuestion == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "User with email " + email + " does not exist");
        }

        ratingDao.deleteAllByUser(userInQuestion.getEmail());
        userDao.delete(userInQuestion);
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() { }
}
