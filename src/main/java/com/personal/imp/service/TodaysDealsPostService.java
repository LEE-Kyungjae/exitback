package com.personal.imp.service;

import com.personal.imp.model.TodaysDealsPost;
import com.personal.imp.repository.TodaysDealsPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TodaysDealsPostService {

    @Autowired
    private TodaysDealsPostRepository todaysDealsPostRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private GenerateETagService generateETagService;

    private static final String POST_CACHE_KEY_PREFIX = "todaysDealsPost_";

    public List<TodaysDealsPost> getAllPosts() {
        String cacheKey = POST_CACHE_KEY_PREFIX + "all";
        List<TodaysDealsPost> posts = (List<TodaysDealsPost>) cacheService.get(cacheKey);

        if (posts == null) {
            posts = todaysDealsPostRepository.findAll();
            cacheService.set(cacheKey, posts, 10, TimeUnit.MINUTES);
        }

        return posts;
    }

    public TodaysDealsPost getPostById(Long id) {
        String cacheKey = POST_CACHE_KEY_PREFIX + id;
        TodaysDealsPost post = (TodaysDealsPost) cacheService.get(cacheKey);

        if (post == null) {
            post = todaysDealsPostRepository.findById(id).orElse(null);
            if (post != null) {
                cacheService.set(cacheKey, post, 10, TimeUnit.MINUTES);
            }
        }

        return post;
    }

    public TodaysDealsPost createPost(TodaysDealsPost post) {
        TodaysDealsPost savedPost = todaysDealsPostRepository.save(post);
        cacheService.set(POST_CACHE_KEY_PREFIX + savedPost.getId(), savedPost, 10, TimeUnit.MINUTES);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
        return savedPost;
    }

    public TodaysDealsPost updatePost(Long id, TodaysDealsPost postDetails) {
        TodaysDealsPost post = todaysDealsPostRepository.findById(id).orElse(null);
        if (post != null) {
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            post.setAuthor(postDetails.getAuthor());
            post.setMediaUrl(postDetails.getMediaUrl());
            post.setCreatedAt(postDetails.getCreatedAt());
            post.setLikeCount(postDetails.getLikeCount());
            post.setTags(postDetails.getTags());
            TodaysDealsPost updatedPost = todaysDealsPostRepository.save(post);
            cacheService.set(POST_CACHE_KEY_PREFIX + updatedPost.getId(), updatedPost, 10, TimeUnit.MINUTES);
            cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
            return updatedPost;
        }
        return null;
    }

    public void deletePost(Long id) {
        todaysDealsPostRepository.deleteById(id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
    }

    public String generateETagForPosts(List<TodaysDealsPost> posts) {
        String combinedHash = posts.stream()
                .map(TodaysDealsPost::hashCode)
                .map(Object::toString)
                .collect(Collectors.joining("-"));
        return Integer.toString(combinedHash.hashCode());
    }

    public String generateETagForPost(TodaysDealsPost post) {
        return Integer.toString(post.hashCode());
    }
}
