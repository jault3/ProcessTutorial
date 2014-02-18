package edu.msoe.tutorial.process.db;

import edu.msoe.tutorial.process.core.Content;
import edu.msoe.tutorial.process.mappers.ContentMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Set;

@RegisterMapper(ContentMapper.class)
public interface ContentDao {

    @SqlUpdate("insert into content (id, title, description, video) values (:id, :title, :description, :video)")
    public void create(@BindBean Content content);

    @SqlQuery("select id, title, description, video from content where id = :id")
    public Content retrieve(@Bind("email") String email);

    @SqlQuery("select id, title, description, video from content")
    public Set<Content> retrieveAll();

    @SqlUpdate("update content set title = :title, description = :description, video = :video where id = :id")
    public void update(@BindBean Content content);

    @SqlUpdate("delete from content where id = :id")
    public void delete(@BindBean Content content);
}
