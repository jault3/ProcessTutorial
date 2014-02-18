package edu.msoe.tutorial.process.mappers;

import edu.msoe.tutorial.process.core.Content;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContentMapper implements ResultSetMapper<Content> {

    public Content map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Content content = new Content();
        content.setId(r.getString("id"));
        content.setTitle(r.getString("title"));
        content.setDescription(r.getString("description"));
        content.setVideo(r.getString("video"));
        return content;
    }
}