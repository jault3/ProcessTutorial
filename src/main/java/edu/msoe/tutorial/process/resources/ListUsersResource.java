package edu.msoe.tutorial.process.resources;

import com.codahale.dropwizard.hibernate.UnitOfWork;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.msoe.tutorial.process.auth.Authorized;
import edu.msoe.tutorial.process.core.User;
import edu.msoe.tutorial.process.core.UserRole;
import edu.msoe.tutorial.process.db.UserDao;
import edu.msoe.tutorial.process.exception.ResponseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ListUsersResource {

    private final UserDao userDao;

    public ListUsersResource(UserDao userDao) {
        this.userDao = userDao;
    }

    @GET
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Set<User> list(@Authorized User existingUser, @DefaultValue("1") @QueryParam("pageNum") int pageNum, @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
        if (!existingUser.getRole().equals(UserRole.ADMIN)) {
            ResponseException.formatAndThrow(Response.Status.UNAUTHORIZED, "You must be an admin to list all users");
        }
        return userDao.list(pageSize, ((pageNum-1)*pageSize));
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() { }
}
