package com.personal.imp.controller;

import com.personal.imp.model.RecommendedPost;
import com.personal.imp.service.GenerateETagService;
import com.personal.imp.service.RecommendedPostService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommended")
public class RecommendedPostController {

    @Autowired
    private GenerateETagService generateETagService;

    @Autowired
    private RecommendedPostService recommendedPostService;

    @GetMapping
    public List<RecommendedPost> getAllPosts(@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        List<RecommendedPost> posts = recommendedPostService.getAllPosts();

        Map<Long, String> eTagMap = posts.stream().collect(
                Collectors.toMap(
                        RecommendedPost::getId,
                        post -> generateETagService.generateETagForPost(post)
                )
        );

        if (ifNoneMatch != null) {
            List<String> clientETags = List.of(ifNoneMatch.split(","));
            List<RecommendedPost> modifiedPosts = posts.stream()
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
    public RecommendedPost getPostById(@PathVariable Long id, @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        RecommendedPost post = recommendedPostService.getPostById(id);
        String eTag = generateETagService.generateETagForPost(post);

        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
        }

        response.setHeader("ETag", eTag);
        return post;
    }

    @PostMapping
    public RecommendedPost createPost(@RequestBody RecommendedPost post, HttpServletResponse response) {
        RecommendedPost createdPost = recommendedPostService.createPost(post);
        String eTag = generateETagService.generateETagForPost(createdPost);
        response.setHeader("ETag", eTag);
        return createdPost;
    }

    @PutMapping("/{id}")
    public RecommendedPost updatePost(@PathVariable Long id, @RequestBody RecommendedPost postDetails, HttpServletResponse response) {
        RecommendedPost updatedPost = recommendedPostService.updatePost(id, postDetails);
        if (updatedPost != null) {
            String eTag = generateETagService.generateETagForPost(updatedPost);
            response.setHeader("ETag", eTag);
        }
        return updatedPost;
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        recommendedPostService.deletePost(id);
    }
}
