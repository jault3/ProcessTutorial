package edu.msoe.tutorial.process.core;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import edu.msoe.tutorial.process.testing.FixtureAssertion;
import edu.msoe.tutorial.process.testing.FixtureTest;
import org.testng.annotations.Test;

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














    /*@Test
    public void testCannotSendIntForString() throws IOException {
        ObjectNode objectNode = fromJson("fixtures/user/userWithTransientPropsSet.json", ObjectNode.class);
        objectNode.remove("userId");
        objectNode.put("userId", 1);
        try {
            getMapper().readValue(objectNode.toString(), User.class);
        } catch (WebApplicationException wae) {
            int status = wae.getResponse().getStatus();
            assertEquals(status, 400);
            ObjectNode response = (ObjectNode) getMapper().readTree(wae.getResponse().getEntity().toString());
            assertEquals(response.has("errors"), true);
            assertEquals(response.get("errors").isArray(), true);
            ArrayNode array = (ArrayNode) response.get("errors");
            assertEquals(array.size(), 1);
            ObjectNode error = (ObjectNode) array.get(0);
            assertEquals(error.get("message").asText(), "userId must be a string");
            assertEquals(error.get("code").asInt(), 400);
        }
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void testUserIdMissingFails() throws IOException {
        User user = fromJson("fixtures/user/userWithoutUserId.json", User.class);
        mAssert.assertIsNotValid(user, HibernateMessage.NOT_BLANK);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void testIsActiveDeserialized() throws IOException {
        User user = fromJson("fixtures/user/userWithTransientPropsSet.json", User.class);
        assertEquals(user.isActive(), true);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void testValidationCodeNotDeserialized() throws IOException {
        User user = fromJson("fixtures/user/userWithTransientPropsSet.json", User.class);
        assertEquals(user.getValidationCode(), null);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void testSessionTokenNotDeserialized() throws IOException {
        User user = fromJson("fixtures/user/userWithTransientPropsSet.json", User.class);
        assertEquals(user.getSessionToken(), null);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void testAppIdNotDeserialized() throws IOException {
        User user = fromJson("fixtures/user/userWithTransientPropsSet.json", User.class);
        assertEquals(user.getAppId(), null);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void testPaymentIdNotDeserialized() throws IOException {
        User user = fromJson("fixtures/user/userWithTransientPropsSet.json", User.class);
        assertEquals(user.getPaymentId(), null);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void testPlanNotDeserialized() throws IOException {
        User user = fromJson("fixtures/user/userWithTransientPropsSet.json", User.class);
        assertEquals(user.getPlan(), null);
    }

    @DataProvider
    public Object[][] dobDataProvider() {
        return new Object[][] {
                { "01-01-1980", IllegalArgumentException.class },
                { "1980-01-01", null } };
    }

    @Test(dataProvider = "dobDataProvider")
    public void userDobTest(String dateString, Class<? extends Exception> clazz) {
        User user = workingUser();
        try {
            user.setDob(ISODateUtils.parseCalendarDate(dateString));
        } catch (Exception e) {
            if (!clazz.isInstance(e)) {
                fail("Test did not expect exception of type " + e.getClass());
            }
        }
        mAssert.assertIsValid(user);
    }

    @DataProvider
    public Object[][] ageDataProvider() {
        return new Object[][] {
                { -1, false },
                { 0, true },
                { 1, true } };
    }

    @Test(dataProvider = "ageDataProvider")
    public void userAgeTest(int age, boolean succeeds) {
        User user = workingUser();
        user.setAge(age);
        if (succeeds) {
            mAssert.assertIsValid(user);
        } else {
            mAssert.assertIsNotValid(user, HibernateMessage.MIN);
        }
    }

    @DataProvider
    public Object[][] phoneNumberDataProvider() {
        return new Object[][] {
                { "1112345678", true }, // valid
                { "14149398876", true }, // valid containing country code
                { "1-414-939-8876", true }, // valid containing country code and separaters
                { "414-939-8876", true }, // valid containing separaters
                { "71 23 45 67 89", true } }; // valid paris phone number
    }

    @Test(dataProvider = "phoneNumberDataProvider")
    public void phoneNumberTest(String phoneNumber, boolean succeeds) {
        User user = workingUser();
        user.setHomePhoneNumber(phoneNumber);
        user.setMobilePhoneNumber(phoneNumber);
        user.setWorkPhoneNumber(phoneNumber);
        user.setOtherPhoneNumber(phoneNumber);
        if (succeeds) {
            mAssert.assertIsValid(user);
        } else {
            mAssert.assertIsNotValid(user, PhoneNumber.MESSAGE);
        }
    }

    @DataProvider
    public Object[][] preferredPhoneNumberDataProvider() {
        return new Object[][] {
                { "home", true, null },
                { "mobile", true, null },
                { "work", true, null },
                { "other", true, null },
                // invalid, can only be "home", "mobile", "work", or "other"
                { "personal", false, IllegalArgumentException.class } };
    }

    @Test(dataProvider = "preferredPhoneNumberDataProvider")
    public void preferredPhoneNumberTest(String prefferedPhoneNumber, boolean succeeds, Class<? extends Exception> expectedExceptionClass)
            throws Exception {
        User user = workingUser();
        try {
            user.setPreferredPhoneNumber(PreferredPhoneNumber.fromString(prefferedPhoneNumber));
        } catch (Exception e) {
            if (!expectedExceptionClass.isInstance(e)) {
                fail("Test did not expect exception of type " + e.getClass());
            }
        }
    }

    @Test
    public void testPatch() {
        User user = workingUser();
        User test = new User();
        test.setId(user.getId());
        test.setActive(user.isActive());
        test.setValidationCode(user.getValidationCode());
        test.setPaymentId(user.getPaymentId());
        test.setPlan(user.getPlan());
        test.setInviteCode(user.getInviteCode());
        test.setCreatedAt(user.getCreatedAt());
        test.setUpdatedAt(user.getUpdatedAt());

        HashMap<String, Object> patch = test.patch(user);
        assertEquals(test, user);
        assertTrue(patch.containsKey("primaryEmail"));
        assertTrue(patch.containsKey("secondaryEmail"));
        assertTrue(patch.containsKey("workEmail"));
        assertTrue(patch.containsKey("otherEmail"));
        assertTrue(patch.containsKey("prefix"));
        assertTrue(patch.containsKey("firstName"));
        assertTrue(patch.containsKey("middleName"));
        assertTrue(patch.containsKey("lastName"));
        assertTrue(patch.containsKey("maidenName"));
        assertTrue(patch.containsKey("dob"));
        assertTrue(patch.containsKey("age"));
        assertTrue(patch.containsKey("homePhoneNumber"));
        assertTrue(patch.containsKey("mobilePhoneNumber"));
        assertTrue(patch.containsKey("workPhoneNumber"));
        assertTrue(patch.containsKey("otherPhoneNumber"));
        assertTrue(patch.containsKey("preferredPhoneNumber"));
        assertTrue(patch.containsKey("address"));
        assertTrue(patch.containsKey("gender"));
        assertTrue(patch.containsKey("maritalStatus"));
        assertTrue(patch.containsKey("religion"));
        assertTrue(patch.containsKey("race"));
        assertTrue(patch.containsKey("ethnicity"));
        assertTrue(patch.containsKey("guardian"));
        assertTrue(patch.containsKey("confCode"));
        assertTrue(patch.containsKey("language"));
        assertTrue(patch.containsKey("socialId"));
        assertTrue(patch.containsKey("mrn"));
        assertTrue(patch.containsKey("healthPlan"));
        assertTrue(patch.containsKey("avatar"));
        assertTrue(patch.containsKey("ssn"));
        assertTrue(patch.containsKey("profilePhoto"));
        assertTrue(patch.containsKey("extras"));

        // Run a patch when both objects are the same. The ArrayList should be
        // empty and the objects remain equal.
        HashMap<String, Object> secondPatch = test.patch(user);
        assertEquals(test, user);
        assertTrue(secondPatch.isEmpty());
    }*/
}
