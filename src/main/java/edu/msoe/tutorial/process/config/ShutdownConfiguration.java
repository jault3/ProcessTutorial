package edu.msoe.tutorial.process.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class ShutdownConfiguration {

    /** Password to shutdown the server */
    @JsonProperty
    @NotEmpty
    private String password;
    
    /**
     * Getter for the host address for the Redis Server.
     * @return String - address
     */
    public String getPassword() {
        return password;
    }
}
