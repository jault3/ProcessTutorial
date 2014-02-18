package edu.msoe.tutorial.process.resources;

import com.codahale.dropwizard.hibernate.UnitOfWork;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.msoe.tutorial.process.core.Session;
import edu.msoe.tutorial.process.core.User;
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

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDao userDao;
    private final SessionDao sessionDao;

    public UserResource(UserDao userDao, SessionDao sessionDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
    }

    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Session post(@Valid User user) {
        User existingUser = userDao.retrieve(user.getEmail());
        if (existingUser == null) {
            Random r = new SecureRandom();
            byte[] saltBytes = new byte[32];
            r.nextBytes(saltBytes);
            String salt = DigestUtils.sha256Hex(saltBytes);
            user.setSalt(salt);
            String password = user.getPassword();
            String hashedPassword = DigestUtils.sha256Hex(salt+password);
            user.setPassword(hashedPassword);

            userDao.create(user);
        } else {
            String sentPasswordHashed = DigestUtils.sha256Hex(existingUser.getSalt()+user.getPassword());
            if (!sentPasswordHashed.equals(existingUser.getPassword())) {
                ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Invalid email / password");
            }
            user = existingUser;
        }

        Session session = sessionDao.createSession(user.getEmail());

        return session;
    }

    @PUT
    @Path("/{email}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User put(@PathParam("email") String email, User user) {
        User existingUser = userDao.retrieve(email);
        if (existingUser == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "User with email " + email + " does not exist");
        }
        if (user.getPassword() != null) {
            Random r = new SecureRandom();
            byte[] saltBytes = new byte[32];
            r.nextBytes(saltBytes);
            String salt = DigestUtils.sha256Hex(saltBytes);
            existingUser.setSalt(salt);
            String password = user.getPassword();
            String hashedPassword = DigestUtils.sha256Hex(salt+password);
            existingUser.setPassword(hashedPassword);
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getEmail() != null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "You cannot update a user's email");
        }
        userDao.update(existingUser);

        return existingUser;
    }

    @GET
    @Path("/{email}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User get(@PathParam("email") String email) {
        User existingUser = userDao.retrieve(email);
        if (existingUser == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "User with email " + email + " does not exist");
        }

        return existingUser;
    }

    @DELETE
    @Path("/{email}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void delete(@PathParam("email") String email) {
        User existingUser = userDao.retrieve(email);
        if (existingUser == null) {
            ResponseException.formatAndThrow(Response.Status.NOT_FOUND, "User with email " + email + " does not exist");
        }
        userDao.delete(existingUser);
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() { }

    @OPTIONS
    @Path("/{email}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void optionsWithEmail() { }
}
