package com.personal.imp.service;

import com.personal.imp.model.CommunityPost;
import com.personal.imp.repository.CommunityPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CommunityPostService {

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    static private GenerateETagService generateETagForPosts;

    @Autowired
    private CacheService cacheService;

    private static final String POST_CACHE_KEY_PREFIX = "communityPost_";

    public List<CommunityPost> getAllPosts() {
        String cacheKey = POST_CACHE_KEY_PREFIX + "all";
        List<CommunityPost> posts = (List<CommunityPost>) cacheService.get(cacheKey);

        if (posts == null) {
            posts = communityPostRepository.findAll();
            cacheService.set(cacheKey, posts, 10, TimeUnit.MINUTES);
        }

        return posts;
    }

    public CommunityPost getPostById(Long id) {
        String cacheKey = POST_CACHE_KEY_PREFIX + id;
        CommunityPost post = (CommunityPost) cacheService.get(cacheKey);

        if (post == null) {
            post = communityPostRepository.findById(id).orElse(null);
            if (post != null) {
                cacheService.set(cacheKey, post, 10, TimeUnit.MINUTES);
            }
        }

        return post;
    }

    public CommunityPost createPost(CommunityPost post) {
        CommunityPost savedPost = communityPostRepository.save(post);
        cacheService.set(POST_CACHE_KEY_PREFIX + savedPost.getId(), savedPost, 10, TimeUnit.MINUTES);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
        return savedPost;
    }

    public CommunityPost updatePost(Long id, CommunityPost postDetails) {
        CommunityPost post = communityPostRepository.findById(id).orElse(null);
        if (post != null) {
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            post.setAuthor(postDetails.getAuthor());
            post.setMediaUrl(postDetails.getMediaUrl());
            post.setCreatedAt(postDetails.getCreatedAt());
            post.setLikeCount(postDetails.getLikeCount());
            post.setComments(postDetails.getComments());
            post.setTags(postDetails.getTags());
            CommunityPost updatedPost = communityPostRepository.save(post);
            cacheService.set(POST_CACHE_KEY_PREFIX + updatedPost.getId(), updatedPost, 10, TimeUnit.MINUTES);
            cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
            return updatedPost;
        }
        return null;
    }

    public void deletePost(Long id) {
        communityPostRepository.deleteById(id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
    }

    public String generateETagForPosts(List<CommunityPost> posts) {
        String combinedHash = posts.stream()
                .map(CommunityPost::hashCode)
                .map(Object::toString)
                .collect(Collectors.joining("-"));
        return Integer.toString(combinedHash.hashCode());
    }

    public String generateETagForPost(CommunityPost post) {
        return Integer.toString(post.hashCode());
    }
}
