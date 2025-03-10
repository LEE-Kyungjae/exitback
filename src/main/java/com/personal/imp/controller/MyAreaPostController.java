package com.personal.imp.controller;

import com.personal.imp.model.MyAreaPost;
import com.personal.imp.service.GenerateETagService;
import com.personal.imp.service.MyAreaPostService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/myarea")
public class MyAreaPostController {

    @Autowired
    private GenerateETagService generateETagService;

    @Autowired
    private MyAreaPostService myAreaPostService;

    @GetMapping
    public List<MyAreaPost> getAllPosts(@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        List<MyAreaPost> posts = myAreaPostService.getAllPosts();

        Map<Long, String> eTagMap = posts.stream().collect(
                Collectors.toMap(
                        MyAreaPost::getId,
                        post -> generateETagService.generateETagForPost(post)
                )
        );

        if (ifNoneMatch != null) {
            List<String> clientETags = List.of(ifNoneMatch.split(","));
            List<MyAreaPost> modifiedPosts = posts.stream()
                    .filter(post -> !clientETags.contains(eTagMap.get(post.getId())))
                    .collect(Collectors.toList());

            if (modifiedPosts.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
            }

            modifiedPosts.forEach(post -> response.addHeader("ETag-" + post.getId(), eTagMap.get(post.getId())));
            return modifiedPosts;
        }

        posts.forEach(post -> response.addHeader("ETag-" + post.getId(), eTagMap.get(post.getId())));
        return posts;
    }

    @GetMapping("/{id}")
    public MyAreaPost getPostById(@PathVariable Long id, @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        MyAreaPost post = myAreaPostService.getPostById(id);
        String eTag = generateETagService.generateETagForPost(post);

        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
        }

        response.setHeader("ETag", eTag);
        return post;
    }

    @PostMapping
    public MyAreaPost createPost(@RequestBody MyAreaPost post, HttpServletResponse response) {
        MyAreaPost createdPost = myAreaPostService.createPost(post);
        String eTag = generateETagService.generateETagForPost(createdPost);
        response.setHeader("ETag", eTag);
        return createdPost;
    }

    @PutMapping("/{id}")
    public MyAreaPost updatePost(@PathVariable Long id, @RequestBody MyAreaPost postDetails, HttpServletResponse response) {
        MyAreaPost updatedPost = myAreaPostService.updatePost(id, postDetails);
        if (updatedPost != null) {
            String eTag = generateETagService.generateETagForPost(updatedPost);
            response.setHeader("ETag", eTag);
        }
        return updatedPost;
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        myAreaPostService.deletePost(id);
    }
}
