package edu.msoe.tutorial.process.db;

import edu.msoe.tutorial.process.core.User;
import edu.msoe.tutorial.process.mappers.UserMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Set;

@RegisterMapper(UserMapper.class)
public interface UserDao {

    @SqlUpdate("insert into pt_user (email, password, salt, role) values (:email, :password, :salt, :role)")
    public void create(@Bind("email") String email, @Bind("password") String password, @Bind("salt") String salt, @Bind("role") String role);

    @SqlQuery("select email, password, salt, role from pt_user where email = :email")
    public User retrieve(@Bind("email") String email);

    @SqlUpdate("update pt_user set password = :password, salt = :salt, role = :role where email = :email")
    public void update(@Bind("email") String email, @Bind("password") String password, @Bind("salt") String salt, @Bind("role") String role);

    @SqlUpdate("delete from pt_user where email = :email")
    public void delete(@BindBean User user);

    @SqlQuery("select email, password, salt, role from pt_user limit :limit, offset :offset")
    public Set<User> list(@Bind("limit") int limit, @Bind("offset") int offset);
}
