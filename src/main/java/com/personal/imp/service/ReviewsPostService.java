package com.personal.imp.service;

import com.personal.imp.model.ReviewsPost;
import com.personal.imp.repository.ReviewsPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ReviewsPostService {

    @Autowired
    private ReviewsPostRepository reviewsPostRepository;

    @Autowired
    private CacheService cacheService;

    private static final String POST_CACHE_KEY_PREFIX = "reviewsPost_";

    public List<ReviewsPost> getAllPosts() {
        String cacheKey = POST_CACHE_KEY_PREFIX + "all";
        List<ReviewsPost> posts = (List<ReviewsPost>) cacheService.get(cacheKey);

        if (posts == null) {
            posts = reviewsPostRepository.findAll();
            cacheService.set(cacheKey, posts, 10, TimeUnit.MINUTES);
        }

        return posts;
    }

    public ReviewsPost getPostById(Long id) {
        String cacheKey = POST_CACHE_KEY_PREFIX + id;
        ReviewsPost post = (ReviewsPost) cacheService.get(cacheKey);

        if (post == null) {
            post = reviewsPostRepository.findById(id).orElse(null);
            if (post != null) {
                cacheService.set(cacheKey, post, 10, TimeUnit.MINUTES);
            }
        }

        return post;
    }

    public ReviewsPost createPost(ReviewsPost post) {
        ReviewsPost savedPost = reviewsPostRepository.save(post);
        cacheService.set(POST_CACHE_KEY_PREFIX + savedPost.getId(), savedPost, 10, TimeUnit.MINUTES);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
        return savedPost;
    }

    public ReviewsPost updatePost(Long id, ReviewsPost postDetails) {
        ReviewsPost post = reviewsPostRepository.findById(id).orElse(null);
        if (post != null) {
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            post.setAuthor(postDetails.getAuthor());
            post.setMediaUrl(postDetails.getMediaUrl());
            post.setCreatedAt(postDetails.getCreatedAt());
            post.setLikeCount(postDetails.getLikeCount());
            post.setComments(postDetails.getComments());
            post.setTags(postDetails.getTags());
            ReviewsPost updatedPost = reviewsPostRepository.save(post);
            cacheService.set(POST_CACHE_KEY_PREFIX + updatedPost.getId(), updatedPost, 10, TimeUnit.MINUTES);
            cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
            return updatedPost;
        }
        return null;
    }

    public void deletePost(Long id) {
        reviewsPostRepository.deleteById(id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + id);
        cacheService.delete(POST_CACHE_KEY_PREFIX + "all"); // Invalidate the cache for all posts
    }

    public String generateETagForPosts(List<ReviewsPost> posts) {
        String combinedHash = posts.stream()
                .map(ReviewsPost::hashCode)
                .map(Object::toString)
                .collect(Collectors.joining("-"));
        return Integer.toString(combinedHash.hashCode());
    }

    public String generateETagForPost(ReviewsPost post) {
        return Integer.toString(post.hashCode());
    }
}
