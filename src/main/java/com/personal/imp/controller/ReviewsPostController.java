package com.personal.imp.controller;

import com.personal.imp.model.ReviewsPost;
import com.personal.imp.service.GenerateETagService;
import com.personal.imp.service.ReviewsPostService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewsPostController {

    @Autowired
    private GenerateETagService generateETagService;

    @Autowired
    private ReviewsPostService reviewsPostService;

    @GetMapping
    public List<ReviewsPost> getAllPosts(@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        List<ReviewsPost> posts = reviewsPostService.getAllPosts();
        String eTag = reviewsPostService.generateETagForPosts(posts);
        response.setHeader("ETag", eTag);

        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
        }

        return posts;
    }

    @GetMapping("/{id}")
    public ReviewsPost getPostById(@PathVariable Long id, @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        ReviewsPost post = reviewsPostService.getPostById(id);
        String eTag = reviewsPostService.generateETagForPost(post);

        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
        }

        response.setHeader("ETag", eTag);
        return post;
    }

    @PostMapping
    public ReviewsPost createPost(@RequestBody ReviewsPost post, HttpServletResponse response) {
        ReviewsPost createdPost = reviewsPostService.createPost(post);
        String eTag = reviewsPostService.generateETagForPost(createdPost);
        response.setHeader("ETag", eTag);
        return createdPost;
    }

    @PutMapping("/{id}")
    public ReviewsPost updatePost(@PathVariable Long id, @RequestBody ReviewsPost postDetails, HttpServletResponse response) {
        ReviewsPost updatedPost = reviewsPostService.updatePost(id, postDetails);
        if (updatedPost != null) {
            String eTag = reviewsPostService.generateETagForPost(updatedPost);
            response.setHeader("ETag", eTag);
        }
        return updatedPost;
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        reviewsPostService.deletePost(id);
    }
}
