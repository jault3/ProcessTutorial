package edu.msoe.tutorial.process.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.codahale.dropwizard.validation.ValidationMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Session {

    public static final String SESSION_PARAM = "session";
    public static final String EMAIL_PARAM = "email";

    /** The Session ID or Token */
    @JsonProperty
    private final String session;
    
    /** Person ID - the owner of the Session */
    @JsonProperty
    private String email;

    
    /**
     * Empty constructor creates a new Session object with a generated
     * session ID (aka token).
     */
    public Session () {
        this.session = UUID.randomUUID().toString();
        this.email = "";
    }
    
    /**
     * Creates a Session object with the specified ID, or token.
     * @param sessionToken
     */
    public Session(String sessionToken) {
        this.session = sessionToken;
        this.email = "";
    }
    
    /**
     * Validates the session's token to proper UUID form.
     * @return A boolean value. If true the session token is correctly formed, otherwise false.
     */
    @JsonIgnore
    @ValidationMethod(message="may not be correctly formatted token")
    public boolean isValidToken() {
        boolean isValid = true;
        try {
            UUID.fromString(session);
        } catch (Exception e) {
            isValid = false;
        }
        return isValid;
    }
    
    /**
     * Returns the session's token.
     * @return String containing the session token value.
     */
    public String getSession() {
        return session;
    }

    /**
     * @return the users email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the users email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> paramMap() {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        return params;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, session);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if(obj == null || !(obj instanceof Session) ) {
            return false;
        }
        Session that = (Session)obj;
        return Objects.equals(getSession(), that.getSession())
                    && Objects.equals(getEmail(), that.getEmail());
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("session", session)
                .add("email", email)
                .toString();
    }
}
