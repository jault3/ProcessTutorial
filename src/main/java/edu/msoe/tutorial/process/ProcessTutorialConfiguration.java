package edu.msoe.tutorial.process;

import com.codahale.dropwizard.Configuration;
import com.codahale.dropwizard.db.DataSourceFactory;
import com.codahale.dropwizard.server.SimpleServerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.msoe.tutorial.process.config.CacheConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Links the yaml configuration to the framework.
 */
public class ProcessTutorialConfiguration extends Configuration {

    /**
     * Constructs a new SE3800Configuration.
     */
    public ProcessTutorialConfiguration() {
        setServerFactory(new SimpleServerFactory());
    }

    /**
     * Container for the database configuration.
     */
    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    /**
     * Container for the caching configuration.
     */
    @Valid
    @NotNull
    @JsonProperty
    private CacheConfiguration cacheConfig = new CacheConfiguration();

    /**
     * @return the container with the database information
     */
    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    /**
     * @return the container with the cache config information
     */
    @JsonProperty("cacheConfig")
    public CacheConfiguration getCacheConfiguration() {
        return cacheConfig;
    }
}
