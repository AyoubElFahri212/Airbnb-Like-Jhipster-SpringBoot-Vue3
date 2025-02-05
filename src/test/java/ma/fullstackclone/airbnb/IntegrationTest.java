package ma.fullstackclone.airbnb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ma.fullstackclone.airbnb.config.AsyncSyncConfiguration;
import ma.fullstackclone.airbnb.config.EmbeddedElasticsearch;
import ma.fullstackclone.airbnb.config.EmbeddedKafka;
import ma.fullstackclone.airbnb.config.EmbeddedSQL;
import ma.fullstackclone.airbnb.config.JacksonConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { JhipsterApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
