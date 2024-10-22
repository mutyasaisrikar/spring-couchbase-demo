package com.srikar.spring_couchbase_demo;

import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends ReactiveCouchbaseRepository<PersonEntity, String> {
}
