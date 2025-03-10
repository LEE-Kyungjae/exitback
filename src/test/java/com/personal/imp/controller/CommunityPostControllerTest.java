package com.personal.imp.controller;

import com.personal.imp.model.CommunityPost;
import com.personal.imp.service.CommunityPostService;
import com.personal.imp.service.GenerateETagService;
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

@WebMvcTest(CommunityPostController.class)
public class CommunityPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommunityPostService communityPostService;

    @MockBean
    private GenerateETagService generateETagService;

    @Test
    void testGetAllPosts_NoIfNoneMatchHeader_ReturnsAllPosts() throws Exception {
        CommunityPost post1 = new CommunityPost(1L, "Post 1", "Content 1");
        CommunityPost post2 = new CommunityPost(2L, "Post 2", "Content 2");
        List<CommunityPost> posts = List.of(post1, post2);

        when(communityPostService.getAllPosts()).thenReturn(posts);
        when(generateETagService.generateETagForPost(post1)).thenReturn("etag1");
        when(generateETagService.generateETagForPost(post2)).thenReturn("etag2");

        mockMvc.perform(get("/api/community"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag-1", "etag1"))
                .andExpect(header().string("ETag-2", "etag2"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAllPosts_WithIfNoneMatchHeader_ReturnsNotModified() throws Exception {
        CommunityPost post = new CommunityPost(1L, "Post 1", "Content 1");

        when(communityPostService.getAllPosts()).thenReturn(List.of(post));
        when(generateETagService.generateETagForPost(post)).thenReturn("etag1");

        mockMvc.perform(get("/api/community")
                        .header(HttpHeaders.IF_NONE_MATCH, "etag1"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testGetPostById_WithIfNoneMatchHeader_ReturnsNotModified() throws Exception {
        CommunityPost post = new CommunityPost(1L, "Post 1", "Content 1");

        when(communityPostService.getPostById(1L)).thenReturn(post);
        when(generateETagService.generateETagForPost(post)).thenReturn("etag1");

        mockMvc.perform(get("/api/community/{id}", 1L)
                        .header(HttpHeaders.IF_NONE_MATCH, "etag1"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testCreatePost_ReturnsCreatedPostWithETag() throws Exception {
        CommunityPost post = new CommunityPost(null, "New Post", "New Content");
        CommunityPost createdPost = new CommunityPost(1L, "New Post", "New Content");

        when(communityPostService.createPost(post)).thenReturn(createdPost);
        when(generateETagService.generateETagForPost(createdPost)).thenReturn("etag1");

        mockMvc.perform(post("/api/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"New Post\", \"content\": \"New Content\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "etag1"))
                .andExpect(jsonPath("$.title").value("New Post"));
    }

    @Test
    void testUpdatePost_ReturnsUpdatedPostWithETag() throws Exception {
        CommunityPost postDetails = new CommunityPost(null, "Updated Post", "Updated Content");
        CommunityPost updatedPost = new CommunityPost(1L, "Updated Post", "Updated Content");

        when(communityPostService.updatePost(1L, postDetails)).thenReturn(updatedPost);
        when(generateETagService.generateETagForPost(updatedPost)).thenReturn("etag1");

        mockMvc.perform(put("/api/community/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Post\", \"content\": \"Updated Content\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "etag1"))
                .andExpect(jsonPath("$.title").value("Updated Post"));
    }

    @Test
    void testDeletePost_ReturnsOkStatus() throws Exception {
        mockMvc.perform(delete("/api/community/{id}", 1L))
                .andExpect(status().isOk());

        verify(communityPostService, times(1)).deletePost(1L);
    }
}
