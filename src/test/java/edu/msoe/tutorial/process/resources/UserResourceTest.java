package edu.msoe.tutorial.process.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import edu.msoe.tutorial.process.core.User;
import edu.msoe.tutorial.process.core.UserRole;
import edu.msoe.tutorial.process.testing.TestJerseyWrapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UserResourceTest extends TestJerseyWrapper {

    public UserResourceTest() throws Exception { }

    public User user() {
        User user = new User();
        user.setRole(UserRole.ADMIN);
        user.setPassword("test123");
        user.setEmail("test@test.com");
        return user;
    }

    @Override
    protected void setUpResources() {
        addResource(new UserResource(_userDao, _sessionDao, _ratingDao));
    }

    @Test
    public void testGet() throws Exception {
        User user = user();

        // Run test
        WebResource wr = client().resource("/user");
        WebResource.Builder bldr = readyToSend(wr);
        ClientResponse response = bldr.get(ClientResponse.class);

        assertEquals(response.getStatus(), 200);

        User returnUser = response.getEntity(User.class);
        assertEquals(user, returnUser);
    }
}
