package edu.msoe.tutorial.process.resources;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jersey.api.client.ClientResponse;
import edu.msoe.tutorial.process.core.Content;
import edu.msoe.tutorial.process.testing.TestJerseyWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sun.jersey.api.client.WebResource;

import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

public class ContentResourceTest extends TestJerseyWrapper {

    public ContentResourceTest() throws Exception { }

    public Content content() {
        Content content = new Content();
        content.setId("34284385-a8bb-43f9-94cb-5540dbe2628b");
        content.setTitle("Content Title");
        content.setDescription("content description");
        content.setVideo("https://www.youtube.com/watch?v=wCkerYMffMo");
        return content;
    }

    @Override
    protected void setUpResources() {
        addResource(new ContentResource(_contentDao, _ratingDao));
    }

    @DataProvider
    public Object[][] postProvider() {
        return new Object[][] {
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", "Content Title", "content description", "https://www.youtube.com/watch?v=wCkerYMffMo", 200, null},
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", null, "content description", "https://www.youtube.com/watch?v=wCkerYMffMo", 400, ConstraintViolationException.class},
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", "Content Title", null, "https://www.youtube.com/watch?v=wCkerYMffMo", 400, ConstraintViolationException.class},
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", "Content Title", "content description", null, 400, ConstraintViolationException.class}
        };
    }

    @Test(dataProvider = "postProvider")
    public void testPost(String id, String title, String description, String video, int status, Class expectedExceptionClass) throws Exception {
        Content content = new Content();
        content.setId(id);
        content.setTitle(title);
        content.setDescription(description);
        content.setVideo(video);

        // Run test
        WebResource.Builder bldr = client().resource("/content").entity(asJson(content));
        bldr = readyToSend(bldr);
        try {
            ClientResponse response = bldr.post(ClientResponse.class);

            // Verify
            assertEquals(response.getStatus(), status);
            if (status == 200) {
                Content result = response.getEntity(Content.class);
                //we cant guess the randomly assigned id, so we set it so the test passes
                content.setId(result.getId());
                assertEquals(result, content);
                verify(_contentDao).create(content);
                verifyNoMoreInteractions(_contentDao);
            } else {
                verifyZeroInteractions(_contentDao);
            }
        } catch (Exception e) {
            if (!e.getClass().equals(expectedExceptionClass)) {
                throw e;
            }
        }
    }

    @DataProvider
    public Object[][] putProvider() {
        return new Object[][] {
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", "Content Title New", "content description new", "https://www.youtube.com/watch?v=wCkerYMffMoNEW", 200, null},
                {"1", "Content Title New", "content description new", "https://www.youtube.com/watch?v=wCkerYMffMoNEW", 404, null},
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", null, "content description new", "https://www.youtube.com/watch?v=wCkerYMffMoNEW", 200, null},
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", "Content Title New", null, "https://www.youtube.com/watch?v=wCkerYMffMoNEW", 200, null},
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", "Content Title New", "content description new", null, 200, null}
        };
    }

    @Test(dataProvider = "putProvider")
    public void testPut(String id, String title, String description, String video, int status, Class expectedExceptionClass) throws Exception {
        Content previous = content();
        Content expected = content();
        ObjectNode contentNode = new ObjectNode(JsonNodeFactory.instance);
        if (id != null) {
            expected.setId(id);
            contentNode.put("id", id);
        }
        if (title != null) {
            expected.setTitle(title);
            contentNode.put("title", title);
        }
        if (description != null) {
            expected.setDescription(description);
            contentNode.put("description", description);
        }
        if (video != null) {
            expected.setVideo(video);
            contentNode.put("video", video);
        }

        when(_contentDao.retrieve(previous.getId())).thenReturn(previous);

        // Run test
        WebResource.Builder bldr = client().resource("/content/" + id).entity(contentNode);
        bldr = readyToSend(bldr);
        try {
            ClientResponse response = bldr.put(ClientResponse.class);

            // Verify
            assertEquals(response.getStatus(), status);
            verify(_contentDao).retrieve(id);
            if (status == 200) {
                Content result = response.getEntity(Content.class);
                assertEquals(result, expected);

                verify(_contentDao).update(expected);
                verifyNoMoreInteractions(_contentDao);
            } else {
                verifyNoMoreInteractions(_contentDao);
            }
        } catch (Exception e) {
            if (!e.getClass().equals(expectedExceptionClass)) {
                throw e;
            }
        }
    }

    @DataProvider
    public Object[][] getProvider() {
        return new Object[][] {
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", 200},
                {"1", 404}
        };
    }

    @Test(dataProvider = "getProvider")
    public void testGet(String id, int status) throws Exception {
        Content previous = content();

        when(_contentDao.retrieve(previous.getId())).thenReturn(previous);

        // Run test
        WebResource wr = client().resource("/content/" + id);
        WebResource.Builder bldr = readyToSend(wr);
        ClientResponse response = bldr.get(ClientResponse.class);

        // Verify
        assertEquals(response.getStatus(), status);
        verify(_contentDao).retrieve(id);
        if (status == 200) {
            Content result = response.getEntity(Content.class);
            assertEquals(result, previous);
        }
        verifyNoMoreInteractions(_contentDao);
    }

    @DataProvider
    public Object[][] deleteProvider() {
        return new Object[][] {
                {"34284385-a8bb-43f9-94cb-5540dbe2628b", 200},
                {"1", 404}
        };
    }

    @Test(dataProvider = "deleteProvider")
    public void testDelete(String id, int status) throws Exception {
        Content previous = content();

        when(_contentDao.retrieve(previous.getId())).thenReturn(previous);

        // Run test
        WebResource wr = client().resource("/content/" + id);
        WebResource.Builder bldr = readyToSend(wr);
        ClientResponse response = bldr.delete(ClientResponse.class);

        // Verify
        assertEquals(response.getStatus(), status);
        verify(_contentDao).retrieve(id);
        if (status == 200) {
            String result = response.getEntity(String.class);
            assertEquals(result, "{}");
            verify(_contentDao).delete(previous);
        }
        verifyNoMoreInteractions(_contentDao);
    }

    @Test
    public void testGetAll() throws Exception {
        Set<Content> allContent = new HashSet<>();
        allContent.add(content());

        when(_contentDao.retrieveAll()).thenReturn(allContent);

        // Run test
        WebResource wr = client().resource("/content");
        WebResource.Builder bldr = readyToSend(wr);
        ClientResponse response = bldr.get(ClientResponse.class);

        // Verify
        assertEquals(response.getStatus(), 200);
        verify(_contentDao).retrieveAll();
        verifyNoMoreInteractions(_contentDao);
    }

    @Test
    public void testOptions() {
        // Run test
        WebResource wr = client().resource("/content");
        WebResource.Builder bldr = readyToSend(wr);
        ClientResponse response = bldr.options(ClientResponse.class);
        assertEquals(response.getStatus(), 204);
    }

    @Test
    public void testOptionsWithId() {
        WebResource wr = client().resource("/content/1");
        WebResource.Builder bldr = readyToSend(wr);
        ClientResponse response = bldr.options(ClientResponse.class);
        assertEquals(response.getStatus(), 204);
    }
}
