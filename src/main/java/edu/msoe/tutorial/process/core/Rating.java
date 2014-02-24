package edu.msoe.tutorial.process.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

public class Rating {

    @JsonProperty
    private String user;

    @JsonProperty
    private String content;

    @JsonProperty
    @Min(1)
    @Max(5)
    private int rating;

    public Rating() { }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, content, rating);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rating)) {
            return false;
        }
        if (o == null || !(o instanceof Rating)) {
            return false;
        }

        Rating that = (Rating) o;

        return Objects.equals(user, that.user) &&
                Objects.equals(content, that.content) &&
                Objects.equals(rating, that.rating);
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
