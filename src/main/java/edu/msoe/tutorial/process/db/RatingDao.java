package edu.msoe.tutorial.process.db;

import edu.msoe.tutorial.process.core.Rating;
import edu.msoe.tutorial.process.mappers.RatingMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(RatingMapper.class)
public interface RatingDao {

    @SqlUpdate("insert into ratings (ptuser, content, rating) values (:user, :content, :rating)")
    public void create(@BindBean Rating rating);

    @SqlQuery("select ptuser, content, rating from ratings where ptuser = :user and content = :content")
    public Rating retrieve(@Bind("user") String user, @Bind("content") String content);

    @SqlUpdate("update ratings set rating = :rating where ptuser = :user and content = :content")
    public void update(@BindBean Rating rating);

    @SqlUpdate("delete from ratings where ptuser = :user and content = :content")
    public void delete(@BindBean Rating rating);

    @SqlUpdate("delete from ratings where content = :content")
    public void deleteAll(@Bind("content") String contentId);
}
