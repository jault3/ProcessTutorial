package edu.msoe.tutorial.process.mappers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.msoe.tutorial.process.core.User;
import edu.msoe.tutorial.process.core.UserRole;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class UserMapperTest {

    private UserMapper mapper;
    private ResultSet rs;

    @BeforeClass
    public void setup() {
        mapper = new UserMapper();
        rs = mock(ResultSet.class);
    }

    public void testMapping() throws SQLException {
        reset(rs);

        String email = "test@test.com";
        String password = "test123";
        String salt = "salt";
        String roleString = "ADMIN";

        User expected = new User();
        expected.setEmail(email);
        expected.setPassword(password);
        expected.setSalt(salt);
        expected.setRole(UserRole.fromString(roleString));

        when(rs.getString("email")).thenReturn(email);
        when(rs.getString("password")).thenReturn(password);
        when(rs.getString("salt")).thenReturn(salt);
        when(rs.getString("role")).thenReturn(roleString);

        User actual = mapper.map(0, rs, null);

        assertEquals(actual, expected);
    }

    public void testEmptyResultSet() throws SQLException {
        reset(rs);

        User user = new User();

        User test = mapper.map(0, rs, null);

        assertEquals(test, user);
    }

}
