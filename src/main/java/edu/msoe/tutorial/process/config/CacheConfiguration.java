package edu.msoe.tutorial.process.config;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to store configuration parameters relating to the 
 * cache datastore utilizing Redis.
 *
 */
public class CacheConfiguration {

    /** Host Address of the Redis Server. */
    @JsonProperty
    @NotNull
    private String address;
    
    /** Host Port of the Redis Server */
    @JsonProperty
    @NotNull
    private int port;
    
    /** Cache Size Property, represents the max number of items allowed to occupy the cache. */
    @JsonProperty
    @NotNull
    private Integer cacheSize;
    
    /** Session Expiration Property, represents the time (in seconds) a Session will live. */
    @JsonProperty
    @NotNull
    private String sessionExpiration;
    
    /**
     * Getter for the host address for the Redis Server.
     * @return String - address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Getter for the host port for the Redis Server.
     * @return String - port
     */
    public int getPort() {
        return port;
    }

    /**
     * Getter for the Cache Size Property
     * @return Integer - size.
     */
    public Integer getCacheSize() {
        return cacheSize;
    }
    
    /**
     * Getter for the Session Expiration Time, in seconds.
     * @return Integer - size.
     */
    public String getSessionExpiration() {
        return sessionExpiration;
    }
}
