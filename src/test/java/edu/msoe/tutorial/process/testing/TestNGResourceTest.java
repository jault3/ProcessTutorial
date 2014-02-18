package edu.msoe.tutorial.process.testing;

import java.util.Map;
import java.util.Set;

import javax.validation.Validation;
import javax.validation.Validator;

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

/**
 * A base test class for testing Dropwizard resources.
 */
public abstract class TestNGResourceTest {

    static {
        LoggingFactory.bootstrap();
    }

    /**
     * Set of objects that will be added to the Jersey environment.
     */
    protected Set<Object> singletons;

    /**
     * Set of Objects which will be used as providers by the Jersey environment.
     */
    private Set<Class<?>> providers;

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
        singletons.add(resource);
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
        singletons.add(provider);
    }

    /**
     * @return the jackson mapper being used
     */
    protected ObjectMapper getObjectMapperFactory() {
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

        // Need to initialize this here because most people probably won't implement a proper
        // equals() or hashcode() method for their resource classes and providers
        singletons = Sets.newHashSet();
        providers = Sets.newHashSet();

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
}
