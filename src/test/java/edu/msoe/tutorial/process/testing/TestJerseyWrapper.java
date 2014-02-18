package edu.msoe.tutorial.process.testing;

import java.io.IOException;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.client.WebResource;

/**
 * PURPOSE
 *  This class is to be used as the superclass for any TestNG tests that need to test routes.
 *  Specifically, this class provides all the setup needed for doing an authorized web request to
 *  Catalyze.
 *
 * @author Dagnon
 */
public abstract class TestJerseyWrapper extends TestNGResourceTest {

    /**
     * This is used to create objects from .json files.
     */
    protected FixtureTest jsonHelper = new FixtureTest();

    public TestJerseyWrapper() throws IOException {

        setupJersey();
    }

    /**
     * NOTE:    For the time being this is called from the constructor(s).
     *          This is because putting it in setUpResources() as designed
     *              adds to the global object on each test call = internal Jersey error.
     *
     * @throws IOException  Thrown if any .json file is missing or has errors
     *                          (WATCH OUT for the Mac's smart-quotes auto-replacing!)
     */
    public void setupJersey() throws IOException {

        this.singletons = Sets.newHashSet(); // TODO DELETE THIS LINE AFTER we correct the framework
    }

    /**
     * USAGE:       Use this just before doing a .post(), .put(), .get(), etc. in your test.
     * EXAMPLE:
     WebResource got = client().resource("/classes");
     CustomClass result = readyToSend(bldr) // *HERE*
     .post(CustomClass.class);
     assertEquals(result, customClass);
     *
     * @param bldr
     * @return
     */
    public WebResource.Builder readyToSend(WebResource.Builder bldr) {
        return bldr.header(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    public WebResource.Builder readyToSend(WebResource wr) {
        return wr.header(HttpHeaders.CONTENT_TYPE, "application/json");
    }
    public FixtureTest getJsonHelper() {
        return jsonHelper;
    }

}
