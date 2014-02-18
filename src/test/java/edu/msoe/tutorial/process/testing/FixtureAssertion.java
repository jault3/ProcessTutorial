package edu.msoe.tutorial.process.testing;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.testng.asserts.Assertion;

/**
 * Provides assertions for fixture-created objects.
 *
 * @author mvolkhart, ault
 */
public class FixtureAssertion extends Assertion {

    /**
     * Provides the ability to validate java beans after de/serialization from/to JSON.
     */
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Runs the provided object through the validator. Throws a ConstraintViolationException if
     * constraints are violated.
     *
     * @param o   object to  validate
     * @param <T> Type of the object
     */
    public <T> void assertIsValid(T o) {
        Set<ConstraintViolation<T>> violations = validator.validate(o);
        if (violations.size() > 0) {
            ConstraintViolation<T> cv = violations.iterator().next();
            System.out.println(cv.getMessage() + " ~ " + cv.getInvalidValue() + " ~ " + cv.getPropertyPath());
            throw new ConstraintViolationException(violations);
        }
    }

    /**
     * Asserts that the specified object is invalid with the specified number of violations.
     * @param o the object to validate
     * @param expected number of {@link ConstraintViolation ConstraintViolations}
     * @param <T> the type of the object
     */
    public <T> void assertIsNotValid(T o, int expected) {
        Set<ConstraintViolation<T>> violations = validator.validate(o);
        for (ConstraintViolation<T> cv : violations) {
            System.out.println("violation: " + cv.getMessage() + ", " + cv.getPropertyPath());
        }
        assertEquals(violations.size(), expected, "Found an incorrect number of violations.");
    }

    /**
     * Asserts that the specified object is invalid with AT LEAST ONE violation,
     * and that the given text message appears within those violations.
     * 
     * Finding EXACTLY one violation would be correct IF this system were setup to fail fast.
     * 
     * @param o object to validate
     * @param expectedMessage provided by the {@link ConstraintViolation}
     * @param <T> type of the provided object
     */
    public <T> void assertIsNotValid(T o, String expectedMessage) {
        Set<ConstraintViolation<T>> violations = validator.validate(o);
//      assertEquals(violations.size(), 1, "There were an incorrect number of violations.");
//      assertEquals(violations.iterator().next().getMessage(), message);
      assertTrue(violations.size() > 0, "There were an incorrect number of violations.");
      Set<String> messages = new HashSet<>();
      for( ConstraintViolation<T> err: violations) {
      	messages.add(err.getMessage());
      }
      assertTrue(messages.contains(expectedMessage));
    }

    /**
     * @return the validator being used
     */
    public Validator getValidator() {
        return validator;
    }
}
