package edu.msoe.tutorial.process.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Objects;

@JsonIgnoreProperties({"salt"})
public class User {

    @JsonProperty
    @NotEmpty
    private String email;

    @JsonProperty
    @NotEmpty
    private String password;

    @JsonProperty
    private String salt;

    @JsonProperty
    private UserRole role;

    public User() { }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, salt, role);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        }
        if (o == null || !(o instanceof User)) {
            return false;
        }

        User that = (User) o;

        return Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(salt, that.salt) &&
                Objects.equals(role, that.role);
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

    public ObjectNode toJson() {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.put("email", email);
        objectNode.put("role", role.getRole());
        return objectNode;
    }
}
