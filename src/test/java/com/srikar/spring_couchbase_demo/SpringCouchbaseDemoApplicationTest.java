package com.srikar.spring_couchbase_demo;

import com.couchbase.client.java.kv.GetOptions;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.Duration.ofDays;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.concurrent.TimeUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class SpringCouchbaseDemoApplicationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private CouchbaseTemplate couchbaseTemplate;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@ParameterizedTest
	@MethodSource("provideStringsForIsBlank")
	void set_expiry(String id,
					Duration expiry,
					boolean noExpiry) {

		testRestTemplate.put(
				format("http://localhost:%s/set-expiry?id=%s&expiry=%s", port, id, expiry),
				null
		);

		Optional<Instant> actualExpiry = couchbaseTemplate.getCouchbaseClientFactory().getBucket().defaultCollection().get(id, GetOptions.getOptions().withExpiry(true)).expiryTime();
		if (noExpiry) {
			assertThat(actualExpiry).isEmpty();
		} else {
			assertThat(actualExpiry).isPresent();
			assertThat(actualExpiry.get()).isCloseTo(Instant.now().plus(expiry), within(2, SECONDS));
		}
	}

	private static Stream<Arguments> provideStringsForIsBlank() {
		return Stream.of(
			Arguments.of("sample", ofMinutes(5), false),
			Arguments.of("sample", ofDays(29).plus(23, HOURS).plus(56, SECONDS), false),
			Arguments.of("sample", ofDays(30), false),
			Arguments.of("sample", ofDays(30).plusSeconds(10), false),
			Arguments.of("sample", ofSeconds(DAYS.toSeconds(365) * 50), false),
			Arguments.of("sample", ofSeconds(DAYS.toSeconds(365) * 50).plusSeconds(10), true),
			Arguments.of("sample", ofSeconds(DAYS.toSeconds(365) * 99), true),
			Arguments.of("sample", ofSeconds(DAYS.toSeconds(365) * 99).plusSeconds(10), true)
		);
	}
}
