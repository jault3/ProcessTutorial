package edu.msoe.tutorial.process.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.testing.EqualsTester;
import edu.msoe.tutorial.process.testing.FixtureAssertion;
import edu.msoe.tutorial.process.testing.FixtureTest;
import edu.msoe.tutorial.process.testing.HibernateMessage;
import org.hibernate.validator.constraints.NotEmpty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;

import static org.testng.Assert.*;

/**
 * Tests the User POJO.
 */
public class UserTest extends FixtureTest {

    private final FixtureAssertion mAssert = new FixtureAssertion();

    private User workingUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("test123");
        user.setRole(UserRole.ADMIN);
        return user;
    }

    @Test
    public void serializesToJSON() throws IOException {
        assertEquals(asJson(workingUser()),
                jsonFixture("fixtures/user/user.json", User.class),
                "a User can be serialized to JSON");
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        User p = fromJson("fixtures/user/user.json", User.class);
        assertEquals(p, workingUser(), "a User can be deserialized from JSON");
        mAssert.assertIsValid(p);
    }

    @DataProvider
    public Object[][] fixtureAndError() {
        return new Object[][] {
                {"fixtures/user/email/integerEmail.json", null, null},
                {"fixtures/user/email/booleanEmail.json", null, null},
                {"fixtures/user/email/email.json", null, null},
                {"fixtures/user/email/doubleEmail.json", null, null},
                {"fixtures/user/email/nullEmail.json", HibernateMessage.NOT_BLANK, null},
                {"fixtures/user/email/emptyEmail.json", HibernateMessage.NOT_BLANK, null},
                {"fixtures/user/password/integerPassword.json", null, null},
                {"fixtures/user/password/booleanPassword.json", null, null},
                {"fixtures/user/password/password.json", null, null},
                {"fixtures/user/password/doublePassword.json", null, null},
                {"fixtures/user/password/nullPassword.json", HibernateMessage.NOT_BLANK, null},
                {"fixtures/user/password/emptyPassword.json", HibernateMessage.NOT_BLANK, null},
                {"fixtures/user/role/integerRole.json", null, JsonMappingException.class},
                {"fixtures/user/role/booleanRole.json", null, JsonMappingException.class},
                {"fixtures/user/role/role.json", null, null},
                {"fixtures/user/role/doubleRole.json", null, JsonMappingException.class},
                {"fixtures/user/role/nullRole.json", null, null},
                {"fixtures/user/role/emptyRole.json", null, JsonMappingException.class},
                {"fixtures/user/salt/integerSalt.json", null, null},
                {"fixtures/user/salt/booleanSalt.json", null, null},
                {"fixtures/user/salt/salt.json", null, null},
                {"fixtures/user/salt/doubleSalt.json", null, null},
                {"fixtures/user/salt/nullSalt.json", null, null},
                {"fixtures/user/salt/emptySalt.json", null, null}
        };
    }

    @Test(dataProvider = "fixtureAndError")
    public void testUserProperties(String fixture, String errorMessage, Class exceptionClass) throws Exception {
        try {
            User user = fromJson(fixture, User.class);
            if (errorMessage == null) {
                mAssert.assertIsValid(user);
            } else {
                mAssert.assertIsNotValid(user, errorMessage);
            }
        } catch (Exception e) {
            if (!e.getClass().equals(exceptionClass)) {
                throw e;
            }
        }
    }

    @Test
    public void testSaltNotDeserialized() throws Exception {
        User user = fromJson("fixtures/user/user.json", User.class);
        assertNull(user.getSalt());
    }

    @Test
    public void testSaltNotSerialized() throws Exception {
        User user = workingUser();
        user.setSalt("salt");
        assertEquals(asJson(user),
                jsonFixture("fixtures/user/userNoSalt.json", User.class),
                "a User can be serialized to JSON");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, workingUser());
        assertNotEquals(new Content(), workingUser());

        User u1 = workingUser();
        User u2 = workingUser();

        User u3 = new User();
        u3.setRole(UserRole.ADMIN);

        User u4 = new User();
        u4.setSalt("salt");

        User u5 = new User();
        u5.setEmail("test@test.com");

        User u6 = new User();
        u6.setPassword("test123");

        EqualsTester tester = new EqualsTester();
        tester.addEqualityGroup(u1, u1, u2);
        tester.addEqualityGroup(u3);
        tester.addEqualityGroup(u4);
        tester.addEqualityGroup(u5);
        tester.addEqualityGroup(u6);
        tester.testEquals();
    }
}
