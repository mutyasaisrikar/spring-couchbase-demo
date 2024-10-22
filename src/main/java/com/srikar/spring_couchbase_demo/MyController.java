package com.srikar.spring_couchbase_demo;

import com.couchbase.client.java.kv.GetOptions;
import com.couchbase.client.java.kv.InsertOptions;
import com.couchbase.client.java.kv.UpsertOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequiredArgsConstructor
public class MyController {

    private final PersonRepository personRepository;

    @PutMapping("set-expiry")
    public void setExpiry(@RequestParam String id,
                          @RequestParam Duration expiry) {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setId(id);
        personEntity.setExpiry(Instant.now().plus(expiry));
        personRepository.save(personEntity).blockOptional();
    }
}
