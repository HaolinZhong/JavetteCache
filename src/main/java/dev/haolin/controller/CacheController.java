package dev.haolin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static dev.haolin.model.JavetteCache.getTopic;

@RestController
@RequestMapping("/javette")
@Slf4j
public class CacheController {

    @GetMapping("/{topic}/{key}")
    public Object getFromCache(@PathVariable("topic") String topicName, @PathVariable("key") String key) {
        return Optional.ofNullable(getTopic(topicName)).map(topic -> topic.get(key)).orElse("error");
    }
}
