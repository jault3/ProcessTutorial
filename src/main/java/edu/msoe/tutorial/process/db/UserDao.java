package edu.msoe.tutorial.process.db;

import edu.msoe.tutorial.process.core.User;
import edu.msoe.tutorial.process.mappers.UserMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(UserMapper.class)
public interface UserDao {

    @SqlUpdate("insert into user (email, password, salt, role) values (:email, :password, :salt, :role)")
    public void create(@BindBean User user);

    @SqlQuery("select email, password, salt, role from user where email = :email")
    public User retrieve(@Bind("email") String email);

    @SqlUpdate("update user set password = :password, salt = :salt, role = :role where email = :email")
    public void update(@BindBean User user);

    @SqlUpdate("delete from user where email = :email")
    public void delete(@BindBean User user);
}
