package com.srikar.spring_couchbase_demo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.couchbase.core.mapping.Document;

import java.time.Instant;

import static java.util.UUID.randomUUID;

@Setter
@Getter
public abstract class BaseEntity {
   @Transient private Instant expiry;
}
