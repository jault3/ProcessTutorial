package edu.msoe.tutorial.process.testing;

import static org.testng.Assert.assertEquals;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.testng.annotations.Test;

public class FixtureAssertionTest {


    @Test
    public void assertIsValidPassesOnValidObject() {
        TestObject object = new TestObject("test@gmail.com", "NotNull");
        FixtureAssertion assertion = new FixtureAssertion();
        assertion.assertIsValid(object);
    }

    @Test
    public void assertIsValidFailsOnInvalidObject() {
        TestObject object = new TestObject("should fail");
        FixtureAssertion assertion = new FixtureAssertion();
        try {
            assertion.assertIsValid(object);
        } catch (ConstraintViolationException e) {
            assertEquals(e.getConstraintViolations().size(), 2);
        }
    }

    @Test
    public void assertIsInvalidWithNumbersPassesOnInvalidObject() {
        TestObject object = new TestObject("should pass");
        FixtureAssertion assertion = new FixtureAssertion();
        assertion.assertIsNotValid(object, 2);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void assertIsInvalidWithNumbersFailsOnValidObject() {
        TestObject object = new TestObject("test@gmail.com", "NotNull");
        FixtureAssertion assertion = new FixtureAssertion();
        assertion.assertIsNotValid(object, 2);
    }

    @Test
    public void assertIsInvalidWithNumbersPassesOnPartiallyValidObject() {
        TestObject object = new TestObject("test@gmail.com");
        FixtureAssertion assertion = new FixtureAssertion();
        assertion.assertIsNotValid(object, 1);
    }

    @Test
    public void assertIsInvalidWithStringPassesOnInvalidObject() {
        TestObject object = new TestObject("test@gmail.com");
        FixtureAssertion assertion = new FixtureAssertion();
        assertion.assertIsNotValid(object, "may not be null");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void assertIsInvalidWithStringFailsOnValidObject() {
        TestObject object = new TestObject("test@gmail.com", "NotNull");
        FixtureAssertion assertion = new FixtureAssertion();
        assertion.assertIsNotValid(object, "message");
    }

    private class TestObject {

        @Email
        private final String email;
        @NotNull
        private final String nul;

        public TestObject(String email, String nul) {
            this.email = email;
            this.nul = nul;
        }

        public TestObject(String email) {
            this(email, null);
        }
    }
}
