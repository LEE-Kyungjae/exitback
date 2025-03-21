package com.personal.imp.controller;

import com.personal.imp.model.TodaysDealsPost;
import com.personal.imp.service.GenerateETagService;
import com.personal.imp.service.TodaysDealsPostService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todaysdeals")
public class TodaysDealsPostController {

    @Autowired
    private GenerateETagService generateETagService;

    @Autowired
    private TodaysDealsPostService todaysDealsPostService;

    @GetMapping
    public List<TodaysDealsPost> getAllPosts(@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        List<TodaysDealsPost> posts = todaysDealsPostService.getAllPosts();

        Map<Long, String> eTagMap = posts.stream().collect(
                Collectors.toMap(
                        TodaysDealsPost::getId,
                        post -> generateETagService.generateETagForPost(post)
                )
        );

        if (ifNoneMatch != null) {
            List<String> clientETags = List.of(ifNoneMatch.split(","));
            List<TodaysDealsPost> modifiedPosts = posts.stream()
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
    public TodaysDealsPost getPostById(@PathVariable Long id, @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        TodaysDealsPost post = todaysDealsPostService.getPostById(id);
        String eTag = generateETagService.generateETagForPost(post);

        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
        }

        response.setHeader("ETag", eTag);
        return post;
    }

    @PostMapping
    public TodaysDealsPost createPost(@RequestBody TodaysDealsPost post, HttpServletResponse response) {
        TodaysDealsPost createdPost = todaysDealsPostService.createPost(post);
        String eTag = generateETagService.generateETagForPost(createdPost);
        response.setHeader("ETag", eTag);
        return createdPost;
    }

    @PutMapping("/{id}")
    public TodaysDealsPost updatePost(@PathVariable Long id, @RequestBody TodaysDealsPost postDetails, HttpServletResponse response) {
        TodaysDealsPost updatedPost = todaysDealsPostService.updatePost(id, postDetails);
        if (updatedPost != null) {
            String eTag = generateETagService.generateETagForPost(updatedPost);
            response.setHeader("ETag", eTag);
        }
        return updatedPost;
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        todaysDealsPostService.deletePost(id);
    }
}
