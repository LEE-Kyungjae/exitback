package com.personal.imp.service;

import com.personal.imp.model.RecommendedPost;
import com.personal.imp.repository.RecommendedPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RecommendedPostService {

    @Autowired
    private RecommendedPostRepository recommendedPostRepository;

    @Autowired
    private CacheService cacheService;

    private static final String POST_CACHE_KEY_PREFIX = "recommendedPost_";

    public List<RecommendedPost> getAllPosts() {
        String cacheKey = POST_CACHE_KEY_PREFIX + "all";
        List<RecommendedPost> posts = (List<RecommendedPost>) cacheService.get(cacheKey);

        if (posts == null) {
            posts = recommendedPostRepository.findAll();
            cacheService.set(cacheKey, posts, 10, TimeUnit.MINUTES);
        }

        return posts;
    }

    public RecommendedPost getPostById(Long id) {
        String cacheKey = POST_CACHE_KEY_PREFIX + id;
        RecommendedPost post = (RecommendedPost) cacheService.get(cacheKey);

        if (post == null) {
            post = recommendedPostRepository.findById(id).orElse(null);
            if (post != null) {
                cacheService.set(cacheKey, post, 10, TimeUnit.MINUTES);
            }
        }

        return post;
    }

    public RecommendedPost createPost(RecommendedPost post) {
        RecommendedPost savedPost = recommendedPostRepository.save(post);
        cacheService.set(POST_CACHE_KEY_PREFIX + savedPost.getId(), savedPost, 10, TimeUnit.MINUTES);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
        return savedPost;
    }

    public RecommendedPost updatePost(Long id, RecommendedPost postDetails) {
        RecommendedPost post = recommendedPostRepository.findById(id).orElse(null);
        if (post != null) {
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            post.setAuthor(postDetails.getAuthor());
            post.setMediaUrl(postDetails.getMediaUrl());
            post.setCreatedAt(postDetails.getCreatedAt());
            post.setLikeCount(postDetails.getLikeCount());
            post.setTags(postDetails.getTags());
            RecommendedPost updatedPost = recommendedPostRepository.save(post);
            cacheService.set(POST_CACHE_KEY_PREFIX + updatedPost.getId(), updatedPost, 10, TimeUnit.MINUTES);
            cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
            return updatedPost;
        }
        return null;
    }

    public void deletePost(Long id) {
        recommendedPostRepository.deleteById(id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
    }

    public String generateETagForPosts(List<RecommendedPost> posts) {
        String combinedHash = posts.stream()
                .map(RecommendedPost::hashCode)
                .map(Object::toString)
                .collect(Collectors.joining("-"));
        return Integer.toString(combinedHash.hashCode());
    }

    public String generateETagForPost(RecommendedPost post) {
        return Integer.toString(post.hashCode());
    }
}
