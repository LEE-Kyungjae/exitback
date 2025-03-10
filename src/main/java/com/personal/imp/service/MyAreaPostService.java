package com.personal.imp.service;

import com.personal.imp.model.MyAreaPost;
import com.personal.imp.repository.MyAreaPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MyAreaPostService {

    @Autowired
    private MyAreaPostRepository myAreaPostRepository;

    @Autowired
    private GenerateETagService generateETagService;

    @Autowired
    private CacheService cacheService;

    private static final String POST_CACHE_KEY_PREFIX = "myAreaPost_";

    public List<MyAreaPost> getAllPosts() {
        String cacheKey = POST_CACHE_KEY_PREFIX + "all";
        List<MyAreaPost> posts = (List<MyAreaPost>) cacheService.get(cacheKey);

        if (posts == null) {
            posts = myAreaPostRepository.findAll();
            cacheService.set(cacheKey, posts, 10, TimeUnit.MINUTES);
        }

        return posts;
    }

    public MyAreaPost getPostById(Long id) {
        String cacheKey = POST_CACHE_KEY_PREFIX + id;
        MyAreaPost post = (MyAreaPost) cacheService.get(cacheKey);

        if (post == null) {
            post = myAreaPostRepository.findById(id).orElse(null);
            if (post != null) {
                cacheService.set(cacheKey, post, 10, TimeUnit.MINUTES);
            }
        }

        return post;
    }

    public MyAreaPost createPost(MyAreaPost post) {
        MyAreaPost savedPost = myAreaPostRepository.save(post);
        cacheService.set(POST_CACHE_KEY_PREFIX + savedPost.getId(), savedPost, 10, TimeUnit.MINUTES);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
        return savedPost;
    }

    public MyAreaPost updatePost(Long id, MyAreaPost postDetails) {
        MyAreaPost post = myAreaPostRepository.findById(id).orElse(null);
        if (post != null) {
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            post.setAuthor(postDetails.getAuthor());
            post.setMediaUrl(postDetails.getMediaUrl());
            post.setCreatedAt(postDetails.getCreatedAt());
            post.setLikeCount(postDetails.getLikeCount());
            post.setTags(postDetails.getTags());
            MyAreaPost updatedPost = myAreaPostRepository.save(post);
            cacheService.set(POST_CACHE_KEY_PREFIX + updatedPost.getId(), updatedPost, 10, TimeUnit.MINUTES);
            cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
            return updatedPost;
        }
        return null;
    }

    public void deletePost(Long id) {
        myAreaPostRepository.deleteById(id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
    }

    public String generateETagForPosts(List<MyAreaPost> posts) {
        String combinedHash = posts.stream()
                .map(MyAreaPost::hashCode)
                .map(Object::toString)
                .collect(Collectors.joining("-"));
        return Integer.toString(combinedHash.hashCode());
    }

    public String generateETagForPost(MyAreaPost post) {
        return Integer.toString(post.hashCode());
    }
}
