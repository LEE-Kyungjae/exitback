package com.personal.imp.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenerateETagService {

    public <T> String generateETagForPosts(List<T> posts) {
        String combinedHash = posts.stream()
                .map(Object::hashCode)
                .map(Object::toString)
                .collect(Collectors.joining("-"));
        return Integer.toString(combinedHash.hashCode());
    }

    public <T> String generateETagForPost(T post) {
        return Integer.toString(post.hashCode());
    }
}
