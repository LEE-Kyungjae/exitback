package com.personal.imp.controller;

import com.personal.imp.model.ReviewsPost;
import com.personal.imp.service.GenerateETagService;
import com.personal.imp.service.ReviewsPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewsPostController.class)
public class ReviewsPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewsPostService reviewsPostService;

    @MockBean
    private GenerateETagService generateETagService;

    @Test
    void testGetAllPosts_NoIfNoneMatchHeader_ReturnsAllPosts() throws Exception {
        ReviewsPost post1 = new ReviewsPost(1L, "Title1", "Content1");
        ReviewsPost post2 = new ReviewsPost(2L, "Title2", "Content2");
        List<ReviewsPost> posts = List.of(post1, post2);

        when(reviewsPostService.getAllPosts()).thenReturn(posts);
        when(reviewsPostService.generateETagForPosts(posts)).thenReturn("etag-all");

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "etag-all"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAllPosts_WithIfNoneMatchHeader_ReturnsNotModified() throws Exception {
        List<ReviewsPost> posts = List.of(new ReviewsPost(1L, "Title1", "Content1"));

        when(reviewsPostService.getAllPosts()).thenReturn(posts);
        when(reviewsPostService.generateETagForPosts(posts)).thenReturn("etag-all");

        mockMvc.perform(get("/api/reviews")
                        .header(HttpHeaders.IF_NONE_MATCH, "etag-all"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testGetPostById_WithIfNoneMatchHeader_ReturnsNotModified() throws Exception {
        ReviewsPost post = new ReviewsPost(1L, "Title1", "Content1");

        when(reviewsPostService.getPostById(1L)).thenReturn(post);
        when(reviewsPostService.generateETagForPost(post)).thenReturn("etag1");

        mockMvc.perform(get("/api/reviews/{id}", 1L)
                        .header(HttpHeaders.IF_NONE_MATCH, "etag1"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testCreatePost_ReturnsCreatedPostWithETag() throws Exception {
        ReviewsPost post = new ReviewsPost(null, "New Post", "Content");
        ReviewsPost createdPost = new ReviewsPost(1L, "New Post", "Content");

        when(reviewsPostService.createPost(post)).thenReturn(createdPost);
        when(reviewsPostService.generateETagForPost(createdPost)).thenReturn("etag1");

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Post\",\"content\":\"Content\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "etag1"))
                .andExpect(jsonPath("$.title").value("New Post"));
    }

    @Test
    void testUpdatePost_ReturnsUpdatedPostWithETag() throws Exception {
        ReviewsPost postDetails = new ReviewsPost(null, "Updated Post", "Updated Content");
        ReviewsPost updatedPost = new ReviewsPost(1L, "Updated Post", "Updated Content");

        when(reviewsPostService.updatePost(1L, postDetails)).thenReturn(updatedPost);
        when(reviewsPostService.generateETagForPost(updatedPost)).thenReturn("etag1");

        mockMvc.perform(put("/api/reviews/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Post\", \"content\": \"Updated Content\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "etag1"))
                .andExpect(jsonPath("$.title").value("Updated Post"));
    }

    @Test
    void testDeletePost_ReturnsOkStatus() throws Exception {
        mockMvc.perform(delete("/api/reviews/{id}", 1L))
                .andExpect(status().isOk());

        verify(reviewsPostService, times(1)).deletePost(1L);
    }
}
