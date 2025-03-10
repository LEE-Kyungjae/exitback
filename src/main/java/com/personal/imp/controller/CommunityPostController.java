package com.personal.imp.controller;

import com.personal.imp.model.CommunityPost;
import com.personal.imp.service.CommunityPostService;
import com.personal.imp.service.GenerateETagService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community")
public class CommunityPostController {

    @Autowired
    private GenerateETagService generateETagService;

    @Autowired
    private CommunityPostService communityPostService;

    @GetMapping
    public List<CommunityPost> getAllPosts(@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        List<CommunityPost> posts = communityPostService.getAllPosts();

        Map<Long, String> eTagMap = posts.stream().collect(
                Collectors.toMap(
                        CommunityPost::getId,
                        post -> generateETagService.generateETagForPost(post)
                )
        );

        if (ifNoneMatch != null) {
            List<String> clientETags = List.of(ifNoneMatch.split(","));
            List<CommunityPost> modifiedPosts = posts.stream()
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
    public CommunityPost getPostById(@PathVariable Long id, @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch, HttpServletResponse response) {
        CommunityPost post = communityPostService.getPostById(id);
        String eTag = generateETagService.generateETagForPost(post);

        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
        }

        response.setHeader("ETag", eTag);
        return post;
    }

    @PostMapping
    public CommunityPost createPost(@RequestBody CommunityPost post, HttpServletResponse response) {
        CommunityPost createdPost = communityPostService.createPost(post);
        String eTag = generateETagService.generateETagForPost(createdPost);
        response.setHeader("ETag", eTag);
        return createdPost;
    }

    @PutMapping("/{id}")
    public CommunityPost updatePost(@PathVariable Long id, @RequestBody CommunityPost postDetails, HttpServletResponse response) {
        CommunityPost updatedPost = communityPostService.updatePost(id, postDetails);
        if (updatedPost != null) {
            String eTag = generateETagService.generateETagForPost(updatedPost);
            response.setHeader("ETag", eTag);
        }
        return updatedPost;
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        communityPostService.deletePost(id);
    }
}
