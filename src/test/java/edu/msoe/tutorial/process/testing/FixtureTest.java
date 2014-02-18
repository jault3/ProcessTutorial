package edu.msoe.tutorial.process.testing;

import static com.codahale.dropwizard.testing.FixtureHelpers.fixture;

import java.io.IOException;

import com.codahale.dropwizard.jackson.Jackson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A set of helper methods for testing the serialization and deserialization of classes to and from
 * JSON. <p>For example, a test for reading and writing a {@code User} object as JSON:</p>
 * <pre><code>
 * assertThat("writing a person as JSON produces the appropriate JSON object",
 *            asJson(person),
 *            is(jsonFixture("fixtures/person.json"));
 * <p/>
 * assertThat("reading a JSON object as a person produces the appropriate person",
 *            fromJson(jsonFixture("fixtures/person.json"), User.class),
 *            is(person));
 * </code></pre>
 */
public class FixtureTest {
    /**
     * Provides the ability to easy map JSON objects.
     */
    private final ObjectMapper mapper = Jackson.newObjectMapper();

    public FixtureTest() {
    }

    /**
     * Converts the given object into a JsonNode.
     *
     * @param object an object
     * @return {@code object} as a {@link JsonNode}
     * @throws IOException if there is an error encoding {@code object}
     */
    public JsonNode asJson(Object object) throws IOException {
        return mapper.valueToTree(object);
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
        JsonNode node = mapper.readTree(fixture(filename));
        return mapper.treeToValue(node, klass);
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
        return mapper.readValue(mapper.writeValueAsString(json), reference);
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

    /**
     * @return the jackson mapper used to translate between json and java
     */
    public ObjectMapper getMapper() {
        return mapper;
    }
}
