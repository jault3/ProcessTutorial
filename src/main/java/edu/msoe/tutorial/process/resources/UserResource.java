package edu.msoe.tutorial.process.resources;

import com.codahale.dropwizard.hibernate.UnitOfWork;
import com.codahale.dropwizard.jackson.Jackson;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Set;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final RatingDao ratingDao;

    public UserResource(UserDao userDao, SessionDao sessionDao, RatingDao ratingDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.ratingDao = ratingDao;
    }

    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public JsonNode post(@Valid User user) {
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

            user.setRole(UserRole.VIEWER);

            userDao.create(user.getEmail(), user.getPassword(), user.getSalt(), user.getRole().toString());
        } else {
            String sentPasswordHashed = DigestUtils.sha256Hex(existingUser.getSalt()+user.getPassword());
            if (!sentPasswordHashed.equals(existingUser.getPassword())) {
                ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Invalid email / password");
            }
            user = existingUser;
        }

        Session session = sessionDao.createSession(user.getEmail());
        ObjectNode object = (ObjectNode)Jackson.newObjectMapper().convertValue(user, JsonNode.class);
        object.put("session", session.getSession());
        return object;
    }

    @PUT
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User put(@Authorized User existingUser, User user) {
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
        userDao.update(existingUser.getEmail(), existingUser.getPassword(), existingUser.getSalt(), existingUser.getRole().toString());

        return existingUser;
    }

    @GET
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User get(@Authorized User existingUser) {
        return existingUser;
    }

    @DELETE
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public String delete(@Authorized User existingUser, @HeaderParam("Authorization") String sessionToken) {
        ratingDao.deleteAllByUser(existingUser.getEmail());
        userDao.delete(existingUser);
        Session session = new Session(sessionToken);
        session.setEmail(existingUser.getEmail());
        sessionDao.deleteSession(session);
        return "{}";
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() { }
}
