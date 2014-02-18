package edu.msoe.tutorial.process.logging;

import com.sun.jersey.api.core.HttpContext;
import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class Log {

    public static final Logger resourceLogger = Logger.getLogger(Log.class.getCanonicalName());

    public static void log(String message) {
        resourceLogger.info(message);
    }

    public static void log(HttpContext c) {
        log(c, null);
    }

    public static void log(HttpContext c, String message) {
        String httpMethod = c.getRequest().getMethod();
        String requestURL = c.getRequest().getRequestUri().toString();
        String body = c.getRequest().getEntity(String.class);
        resourceLogger.info(String.format("%s - %s - %s - %s", httpMethod, requestURL, body, message));
    }

    public static void logAndThrow(Response response) {
        logAndThrow(null, response);
    }

    public static void logAndThrow(Throwable cause, Response response) {
        if (response == null) {
            resourceLogger.error(500 + " - ");
            throw new WebApplicationException(cause, Response.serverError().build());
        } else {
            int status = response.getStatus();
            String msg = "";
            if (response.getEntity() != null) {
                msg = response.getEntity().toString();
            }
            msg = "{\"error\":\"" + msg + "\"}";
            response = Response.status(status).entity(msg).build();
            resourceLogger.error(status + " - " + msg);
            throw new WebApplicationException(cause, response);
        }
    }
}
