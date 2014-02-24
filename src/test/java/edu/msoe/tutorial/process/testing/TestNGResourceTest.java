package edu.msoe.tutorial.process.testing;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.validation.Validation;
import javax.validation.Validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import edu.msoe.tutorial.process.core.Session;
import edu.msoe.tutorial.process.core.User;
import edu.msoe.tutorial.process.core.UserRole;
import edu.msoe.tutorial.process.db.ContentDao;
import edu.msoe.tutorial.process.db.RatingDao;
import edu.msoe.tutorial.process.db.SessionDao;
import edu.msoe.tutorial.process.db.UserDao;
import org.junit.After;
import org.junit.Before;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.codahale.dropwizard.jackson.Jackson;
import com.codahale.dropwizard.jersey.DropwizardResourceConfig;
import com.codahale.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import com.codahale.dropwizard.logging.LoggingFactory;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;

import static com.codahale.dropwizard.testing.FixtureHelpers.fixture;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * A base test class for testing Dropwizard resources.
 */
public abstract class TestNGResourceTest {

    protected ContentDao _contentDao;
    protected RatingDao _ratingDao;
    protected SessionDao _sessionDao;
    protected UserDao _userDao;

    protected final String sessionToken = UUID.randomUUID().toString();

    private Session session;
    private User loggedIn;

    static {
        LoggingFactory.bootstrap();
    }

    public TestNGResourceTest() {
        _contentDao = mock(ContentDao.class);
        _ratingDao = mock(RatingDao.class);
        _sessionDao = mock(SessionDao.class);
        _userDao = mock(UserDao.class);

        loggedIn = new User();
        loggedIn.setPassword("test123");
        loggedIn.setEmail("test@test.com");
        loggedIn.setSalt("salt");
        loggedIn.setRole(UserRole.ADMIN);

        session = new Session(sessionToken);
        session.setEmail(loggedIn.getEmail());
    }

    @BeforeMethod(alwaysRun = true)
    public void resetDAOs() {
        reset(_contentDao);
        reset(_ratingDao);
        reset(_sessionDao);
        reset(_userDao);
    }

    /**
     * Set of objects that will be added to the Jersey environment.
     */
    protected Set<Object> singletons = Sets.newHashSet();

    /**
     * Set of Objects which will be used as providers by the Jersey environment.
     */
    private Set<Class<?>> providers = Sets.newHashSet();

    /**
     * Jackson mapper to translate between JSON and java.
     */
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    /**
     * Configuration parameters for the Jersey environment.
     */
    private final Map<String, Boolean> features = Maps.newHashMap();

    /**
     * Properties ot apply to the Jersey environment.
     */
    private final Map<String, Object> properties = Maps.newHashMap();

    /**
     * The instrumented test itself.
     */
    private JerseyTest test;

    /**
     * The validator to use when checking POJO's.
     */
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Runs before each test method is executed. Configure the Jersey environment in here.
     * @throws Exception if something goes wrong
     */
    protected abstract void setUpResources() throws Exception;

    /**
     * @return the validator being used
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * @param validator to be used
     */
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * @param resource to add to Jersey
     */
    protected void addResource(Object resource) {
        boolean has = false;
        for (Object o : singletons) {
            if (o.getClass() == resource.getClass()) {
                has = true;
            }
        }
        if (!has) {
            singletons.add(resource);
        }
    }

    /**
     * @param klass of the provider to add to Jersey
     */
    public void addProvider(Class<?> klass) {
        providers.add(klass);
    }

    /**
     * @param provider to add to Jersey
     */
    public void addProvider(Object provider) {
        boolean has = false;
        for (Object o : singletons) {
            if (o.getClass() == provider.getClass()) {
                has = true;
            }
        }
        if (!has) {
            singletons.add(provider);
        }
    }

    /**
     * @return the jackson mapper being used
     */
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * @param feature key to add to Jersey
     * @param value value to add to Jersey
     */
    protected void addFeature(String feature, Boolean value) {
        features.put(feature, value);
    }

    /**
     * @param property to add to Jersey
     * @param value to add to Jersey
     */
    protected void addProperty(String property, Object value) {
        properties.put(property, value);
    }

    /**
     * @return the Jersey client
     */
    protected Client client() {
        return test.client();
    }

    /**
     * @return the Jersey test instrumentation
     */
    protected JerseyTest getJerseyTest() {
        return test;
    }

    /**
     * Configures the instrumented Jersey test environment.
     * @throws Exception if Jersey cannot be configured
     */
    @BeforeMethod(alwaysRun = true)
    @Before
    public final void setUpJersey() throws Exception {

        setUpResources();

        this.test = new JerseyTest() {
            @Override
            protected AppDescriptor configure() {
                final DropwizardResourceConfig config =
                        DropwizardResourceConfig.forTesting(new MetricRegistry());
                for (Class<?> provider : providers) {
                    config.getClasses().add(provider);
                }
                for (Map.Entry<String, Boolean> feature : features.entrySet()) {
                    config.getFeatures().put(feature.getKey(), feature.getValue());
                }
                for (Map.Entry<String, Object> property : properties.entrySet()) {
                    config.getProperties().put(property.getKey(), property.getValue());
                }
                config.getSingletons().add(new JacksonMessageBodyProvider(objectMapper, validator));
                config.getSingletons().addAll(singletons);
                return new LowLevelAppDescriptor.Builder(config).build();
            }
        };

        test.setUp();

        // Authentication setup
        when(_sessionDao.lookupSession(new Session(sessionToken))).thenReturn(session);
        when(_userDao.retrieve(loggedIn.getEmail())).thenReturn(loggedIn);
    }

    /**
     * Destroy the test.
     * @throws Exception if things go horribly wrong
     */
    @AfterMethod(alwaysRun = true)
    @After
    public final void tearDownJersey() throws Exception {
        if (test != null) {
            test.tearDown();
        }
    }

    /**
     * Converts the given object into a JsonNode.
     *
     * @param object an object
     * @return {@code object} as a {@link com.fasterxml.jackson.databind.JsonNode}
     * @throws java.io.IOException if there is an error encoding {@code object}
     */
    public JsonNode asJson(Object object) throws IOException {
        return objectMapper.valueToTree(object);
    }

    /**
     * Converts the specified JSON file into an object of the given type. The specified path will be
     * looked for inside of the resources directory.
     *
     * @param filename the path to the JSON file
     * @param klass    the class of the type that {@code json} should be converted to
     * @param <T>      the type that {@code json} should be converted to
     * @return {@code json} as an instance of {@code T}
     * @throws IOException if there is an error reading {@code json} as an instance of {@code T}
     */
    public <T> T fromJson(String filename, Class<T> klass) throws IOException {
        JsonNode node = objectMapper.readTree(fixture(filename));
        return objectMapper.treeToValue(node, klass);
    }

    /**
     * Converts the given JSON string into an object of the given type.
     *
     * @param json      a JSON string
     * @param reference a reference of the type that {@code json} should be converted to
     * @param <T>       the type that {@code json} should be converted to
     * @return {@code json} as an instance of {@code T}
     * @throws IOException if there is an error reading {@code json} as an instance of {@code T}
     */
    public <T> T fromJson(JsonNode json, TypeReference<T> reference) throws IOException {
        return objectMapper.readValue(objectMapper.writeValueAsString(json), reference);
    }

    /**
     * Loads the given fixture resource as a JsonNode.
     * Keeps the given timestamps.
     *
     * @param filename the filename of the fixture
     * @param klass    the class type of JSON being parsed
     * @return the contents of {@code filename} as a {@link JsonNode}
     * @throws IOException if there is an error parsing {@code filename}
     */
    public JsonNode jsonFixture(String filename, Class<?> klass) throws IOException {
        return asJson(fromJson(filename, klass));
    }
}
