package edu.msoe.tutorial.process.auth;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import edu.msoe.tutorial.process.core.Session;
import edu.msoe.tutorial.process.core.User;
import edu.msoe.tutorial.process.db.SessionDao;
import edu.msoe.tutorial.process.db.UserDao;
import edu.msoe.tutorial.process.exception.ResponseException;

/**
 * Provides the injection of the currently authenticated user into the resource method.
 */
public class AuthorizedProvider implements InjectableProvider<Authorized, Parameter> {

    private final UserDao userDao;
    private final SessionDao sessionDao;

    public AuthorizedProvider(UserDao userDao, SessionDao sessionDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
    }

    /**
     * @return the scope of this provider
     */
    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    /**
     * @param ic         the component context
     * @param annotation used by this provider
     * @param c          the Parameter
     * @return the AuthorizedInjectable that does the injecting of the User who is currently logged
     *         in
     */
    @Override
    public Injectable<User> getInjectable(ComponentContext ic, Authorized annotation, Parameter c) {
        return new AuthorizedInjectable(userDao, sessionDao);
    }

    /**
     * The class that does the work for the {@link Authorized} annotation. Its main method, {@link
     * #getValue(com.sun.jersey.api.core.HttpContext)} returns the currently logged in User.
     */
    public static class AuthorizedInjectable extends AbstractHttpContextInjectable<User> {

        private final UserDao userDao;
        private final SessionDao sessionDao;

        private AuthorizedInjectable(UserDao userDao, SessionDao sessionDao) {
            this.userDao = userDao;
            this.sessionDao = sessionDao;
        }

        /**
         * This method actually finds the currently logged in User from the database by parsing the
         * Authorization header and extracting the session token.
         *
         * @param c the HttpContext
         * @return the User logged in
         */
        @Override
        public User getValue(HttpContext c) {
            //get the "Authorization" header which should contain their session token
            final String sessionToken = c.getRequest().getHeaderValue(HttpHeaders.AUTHORIZATION);
            if (sessionToken == null) {
                ResponseException.formatAndThrow(Response.Status.UNAUTHORIZED, "Authorization header was not set");
            }

            Session session = sessionDao.lookupSession(new Session(sessionToken));

            //now we find the user in the database
            User user = userDao.retrieve(session.getEmail());

            // If we need a user and dont have one, throw an exception
            if (user == null) {
                ResponseException.formatAndThrow(Response.Status.UNAUTHORIZED, "You must be logged in to view this resource");
            }

            return user;
        }
    }
}
