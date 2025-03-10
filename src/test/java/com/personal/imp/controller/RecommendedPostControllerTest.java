package com.personal.imp.controller;

import com.personal.imp.model.RecommendedPost;
import com.personal.imp.service.GenerateETagService;
import com.personal.imp.service.RecommendedPostService;
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

@WebMvcTest(RecommendedPostController.class)
public class RecommendedPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendedPostService recommendedPostService;

    @MockBean
    private GenerateETagService generateETagService;

    @Test
    void testGetAllPosts_NoIfNoneMatchHeader_ReturnsAllPosts() throws Exception {
        RecommendedPost post1 = new RecommendedPost(1L, "Title1", "Content1");
        RecommendedPost post2 = new RecommendedPost(2L, "Title2", "Content2");
        List<RecommendedPost> posts = List.of(post1, post2);

        when(recommendedPostService.getAllPosts()).thenReturn(posts);
        when(generateETagService.generateETagForPost(post1)).thenReturn("etag1");
        when(generateETagService.generateETagForPost(post2)).thenReturn("etag2");

        mockMvc.perform(get("/api/recommended"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag-1", "etag1"))
                .andExpect(header().string("ETag-2", "etag2"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAllPosts_WithIfNoneMatchHeader_ReturnsNotModified() throws Exception {
        RecommendedPost post = new RecommendedPost(1L, "Title1", "Content1");

        when(recommendedPostService.getAllPosts()).thenReturn(List.of(post));
        when(generateETagService.generateETagForPost(post)).thenReturn("etag1");

        mockMvc.perform(get("/api/recommended")
                        .header(HttpHeaders.IF_NONE_MATCH, "etag1"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testGetPostById_WithIfNoneMatchHeader_ReturnsNotModified() throws Exception {
        RecommendedPost post = new RecommendedPost(1L, "Title1", "Content1");

        when(recommendedPostService.getPostById(1L)).thenReturn(post);
        when(generateETagService.generateETagForPost(post)).thenReturn("etag1");

        mockMvc.perform(get("/api/recommended/{id}", 1L)
                        .header(HttpHeaders.IF_NONE_MATCH, "etag1"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testCreatePost_ReturnsCreatedPostWithETag() throws Exception {
        RecommendedPost post = new RecommendedPost(null, "New Post", "Content");
        RecommendedPost createdPost = new RecommendedPost(1L, "New Post", "Content");

        when(recommendedPostService.createPost(post)).thenReturn(createdPost);
        when(generateETagService.generateETagForPost(createdPost)).thenReturn("etag1");

        mockMvc.perform(post("/api/recommended")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Post\",\"content\":\"Content\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "etag1"))
                .andExpect(jsonPath("$.title").value("New Post"));
    }

    @Test
    void testUpdatePost_ReturnsUpdatedPostWithETag() throws Exception {
        RecommendedPost postDetails = new RecommendedPost(null, "Updated Post", "Updated Content");
        RecommendedPost updatedPost = new RecommendedPost(1L, "Updated Post", "Updated Content");

        when(recommendedPostService.updatePost(1L, postDetails)).thenReturn(updatedPost);
        when(generateETagService.generateETagForPost(updatedPost)).thenReturn("etag1");

        mockMvc.perform(put("/api/recommended/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Post\", \"content\": \"Updated Content\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "etag1"))
                .andExpect(jsonPath("$.title").value("Updated Post"));
    }

    @Test
    void testDeletePost_ReturnsOkStatus() throws Exception {
        mockMvc.perform(delete("/api/recommended/{id}", 1L))
                .andExpect(status().isOk());

        verify(recommendedPostService, times(1)).deletePost(1L);
    }
}
