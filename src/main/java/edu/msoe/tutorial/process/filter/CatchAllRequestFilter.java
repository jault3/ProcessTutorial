package edu.msoe.tutorial.process.filter;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import edu.msoe.tutorial.process.logging.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CatchAllRequestFilter implements ContainerRequestFilter {

    @Override
    public ContainerRequest filter(ContainerRequest request) throws ContainerException {
        String body = request.getEntity(String.class);
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n" + request.getMethod() + " " + request.getRequestUri().toString() + "\n");
        for (String header : request.getRequestHeaders().keySet()) {
            logMessage.append(header + ": " + request.getRequestHeader(header) + "\n");
        }
        logMessage.append("\n");
        logMessage.append(body + "\n");
        Log.log(logMessage.toString());

        try {
            if (body.length() > 0) {
                request.setEntityInputStream(new ByteArrayInputStream(body.getBytes("UTF-8")));
            }
        } catch (IOException ex) {
            throw new ContainerException(ex);
        }

        return request;
    }
}
