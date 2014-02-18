package edu.msoe.tutorial.process.filter;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import edu.msoe.tutorial.process.logging.Log;

public class CatchAllRequestFilter implements ContainerRequestFilter {

    @Override
    public ContainerRequest filter(ContainerRequest request) throws ContainerException {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n" + request.getMethod() + " " + request.getRequestUri().toString() + "\n");
        for (String header : request.getRequestHeaders().keySet()) {
            logMessage.append(header + ": " + request.getRequestHeader(header) + "\n");
        }
        logMessage.append("\n");
        logMessage.append(request.getEntity(String.class) + "\n");
        Log.log(logMessage.toString());

        return request;
    }
}
