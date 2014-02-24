package edu.msoe.tutorial.process.core;

import edu.msoe.tutorial.process.testing.FixtureTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests the UserRole enum.
 */
public class UserRoleTest extends FixtureTest {

    @Test
    public void testValueOf() {
        assertEquals(UserRole.valueOf("ADMIN"), UserRole.ADMIN);
        assertEquals(UserRole.valueOf("VIEWER"), UserRole.VIEWER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidValueOf() {
        assertEquals(UserRole.valueOf("invalid"), null);
    }

    @Test
    public void testGetRole() {
        assertEquals(UserRole.ADMIN.getRole(), "ADMIN");
        assertEquals(UserRole.VIEWER.getRole(), "VIEWER");
    }

    @Test
    public void testToStringEqualsGetRole() throws Exception {
        assertEquals(UserRole.ADMIN.getRole(), UserRole.ADMIN.toString());
        assertEquals(UserRole.VIEWER.getRole(), UserRole.VIEWER.toString());
    }
}
