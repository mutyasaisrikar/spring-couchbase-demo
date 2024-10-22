package com.srikar.spring_couchbase_demo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;

import static java.util.UUID.randomUUID;

@Document
@Getter
@Setter
public class PersonEntity extends BaseEntity {
    @Id       private String id;
              private String firstName = randomUUID().toString();
              private String lastName = randomUUID().toString();
              private int age = randomUUID().hashCode();
}
