package edu.msoe.tutorial.process;

import com.codahale.dropwizard.Application;
import com.codahale.dropwizard.db.DataSourceFactory;
import com.codahale.dropwizard.jdbi.DBIFactory;
import com.codahale.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import com.codahale.dropwizard.migrations.MigrationsBundle;
import com.codahale.dropwizard.setup.Bootstrap;
import com.codahale.dropwizard.setup.Environment;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.sun.jersey.api.core.ResourceConfig;
import edu.msoe.tutorial.process.auth.AuthorizedProvider;
import edu.msoe.tutorial.process.config.CacheConfiguration;
import edu.msoe.tutorial.process.db.ContentDao;
import edu.msoe.tutorial.process.db.RatingDao;
import edu.msoe.tutorial.process.db.SessionDao;
import edu.msoe.tutorial.process.db.UserDao;
import edu.msoe.tutorial.process.filter.CatchAllRequestFilter;
import edu.msoe.tutorial.process.resources.ContentResource;
import edu.msoe.tutorial.process.resources.HealthCheckResource;
import edu.msoe.tutorial.process.resources.RatingResource;
import edu.msoe.tutorial.process.resources.UserResource;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.net.UnknownHostException;
import java.util.EnumSet;

/**
 * The server entry point.
 * <p/>
 * Configures the framework to perform all the operations we expect. Operations include Health
 * Checks, Resources, Providers and Bundles.
 */
public class ProcessTutorialApplication extends Application<ProcessTutorialConfiguration> {

    /**
     * Entry point.
     *
     * @param args from command line
     * @throws Exception if the server cannot start
     */
    public static void main(String[] args) throws Exception {
        new ProcessTutorialApplication().run(args);
    }

    @Override
    public String getName() {
        return "se3800Final";
    }

    @Override
    public void initialize(Bootstrap<ProcessTutorialConfiguration> bootstrap) {
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(new MigrationsBundle<ProcessTutorialConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ProcessTutorialConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.getObjectMapper().registerModule(new GuavaModule());
    }

    @Override
    public void run(ProcessTutorialConfiguration configuration, Environment environment) throws
            UnknownHostException, ClassNotFoundException {

        /*
         * JDBI
         */
        final DBIFactory dbiFactory = new DBIFactory();
        final DBI jdbi = dbiFactory.build(environment, configuration.getDataSourceFactory(), "postgresql");

        CacheConfiguration cacheConfig = configuration.getCacheConfiguration();
        final JedisPool pool = new JedisPool(cacheConfig.getAddress(), cacheConfig.getPort());

        Jedis jedis = pool.getResource();
        jedis.configSet("timeout", cacheConfig.getSessionExpiration());
        pool.returnResource(jedis);

        /*
         * DAOs
         */
        final UserDao userDao = jdbi.onDemand(UserDao.class);
        final ContentDao contentDao = jdbi.onDemand(ContentDao.class);
        final RatingDao ratingDao = jdbi.onDemand(RatingDao.class);

        final SessionDao sessionDao = new SessionDao(pool, configuration.getCacheConfiguration());

        /*
         * Add Resources
         */
        environment.jersey().register(new HealthCheckResource());
        environment.jersey().register(new UserResource(userDao, sessionDao, ratingDao));
        environment.jersey().register(new ContentResource(contentDao, ratingDao));
        environment.jersey().register(new RatingResource(contentDao, ratingDao));

        /*
         * Add Providers
         */
        environment.jersey().register(new AuthorizedProvider(userDao, sessionDao));


        /*
         * Add Health Checks
         */

        /*
         * Filters
         */
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter("allowedOrigins", "*");
        filter.setInitParameter("allowedHeaders", "Authorization,Content-Type,X-Api-Key,Accept,Origin");
        filter.setInitParameter("allowedMethods", "GET,POST,PUT,DELETE,OPTIONS");
        filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months
        filter.setInitParameter("allowCredentials", "true");

        environment.jersey().property(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, CatchAllRequestFilter.class.getName());

        /*
         * Metrics Reporting
         */


        /*
         * Serializers and Deserializers
         */
        /*SimpleModule module = new SimpleModule();
        module.addSerializer(User.class, new UserSerializer());
        module.addDeserializer(User.class, new UserDeserializer());
        environment.getObjectMapper().registerModule(module);*/
    }
}
