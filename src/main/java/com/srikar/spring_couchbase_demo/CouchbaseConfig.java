package com.srikar.spring_couchbase_demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.core.mapping.CouchbaseDocument;
import org.springframework.data.couchbase.core.mapping.CouchbaseMappingContext;
import org.springframework.data.couchbase.core.mapping.CouchbasePersistentEntity;
import org.springframework.data.couchbase.core.mapping.CouchbasePersistentProperty;
import org.springframework.data.mapping.context.MappingContext;

import java.time.Duration;
import java.time.Instant;

import static com.couchbase.client.core.api.kv.CoreExpiry.EARLIEST_VALID_EXPIRY_INSTANT;
import static java.util.concurrent.TimeUnit.DAYS;

@Configuration
public class CouchbaseConfig {

    private static final int LATEST_VALID_EXPIRY_DURATION = (int) DAYS.toSeconds(365) * 50;

    @Bean
    public MappingCouchbaseConverter mappingCouchbaseConverter(CouchbaseMappingContext couchbaseMappingContext,
                                                               CustomConversions customConversions) throws Exception {
        MappingCouchbaseConverter converter = new ExpiringDocumentCouchbaseConverter(couchbaseMappingContext);
        converter.setCustomConversions(customConversions);
        return converter;
    }

    public static class ExpiringDocumentCouchbaseConverter extends MappingCouchbaseConverter {

        public ExpiringDocumentCouchbaseConverter(MappingContext<? extends CouchbasePersistentEntity<?>, CouchbasePersistentProperty> mappingContext) {
            super(mappingContext);
        }

        // Setting custom TTL on documents.
        @Override
        public void write(final Object source, final CouchbaseDocument target) {
            super.write(source, target);
            if (source instanceof BaseEntity) {
                Instant expiry = ((BaseEntity) source).getExpiry();
                long seconds = Duration.between(Instant.now(), expiry).toSeconds();
                if (seconds > LATEST_VALID_EXPIRY_DURATION) {
                    target.setExpiration(0);
                } else {
                    target.setExpiration((int) seconds);
                }
            }
        }
    }
}
