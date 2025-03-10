package com.personal.imp.controller;

import com.personal.imp.model.MyAreaPost;
import com.personal.imp.service.GenerateETagService;
import com.personal.imp.service.MyAreaPostService;
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

@WebMvcTest(MyAreaPostController.class)
public class MyAreaPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyAreaPostService myAreaPostService;

    @MockBean
    private GenerateETagService generateETagService;

    @Test
    void testGetAllPosts_NoIfNoneMatchHeader_ReturnsAllPosts() throws Exception {
        MyAreaPost post1 = new MyAreaPost(1L, "Title1", "Content1");
        MyAreaPost post2 = new MyAreaPost(2L, "Title2", "Content2");
        List<MyAreaPost> posts = List.of(post1, post2);

        when(myAreaPostService.getAllPosts()).thenReturn(posts);
        when(generateETagService.generateETagForPost(post1)).thenReturn("etag1");
        when(generateETagService.generateETagForPost(post2)).thenReturn("etag2");

        mockMvc.perform(get("/api/myarea"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag-1", "etag1"))
                .andExpect(header().string("ETag-2", "etag2"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAllPosts_WithIfNoneMatchHeader_ReturnsNotModified() throws Exception {
        MyAreaPost post = new MyAreaPost(1L, "Title1", "Content1");

        when(myAreaPostService.getAllPosts()).thenReturn(List.of(post));
        when(generateETagService.generateETagForPost(post)).thenReturn("etag1");

        mockMvc.perform(get("/api/myarea")
                        .header(HttpHeaders.IF_NONE_MATCH, "etag1"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testGetPostById_WithIfNoneMatchHeader_ReturnsNotModified() throws Exception {
        MyAreaPost post = new MyAreaPost(1L, "Title1", "Content1");

        when(myAreaPostService.getPostById(1L)).thenReturn(post);
        when(generateETagService.generateETagForPost(post)).thenReturn("etag1");

        mockMvc.perform(get("/api/myarea/{id}", 1L)
                        .header(HttpHeaders.IF_NONE_MATCH, "etag1"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testCreatePost_ReturnsCreatedPostWithETag() throws Exception {
        MyAreaPost post = new MyAreaPost(null, "New Post", "Content");
        MyAreaPost createdPost = new MyAreaPost(1L, "New Post", "Content");

        when(myAreaPostService.createPost(post)).thenReturn(createdPost);
        when(generateETagService.generateETagForPost(createdPost)).thenReturn("etag1");

        mockMvc.perform(post("/api/myarea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Post\",\"content\":\"Content\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "etag1"))
                .andExpect(jsonPath("$.title").value("New Post"));
    }

    @Test
    void testUpdatePost_ReturnsUpdatedPostWithETag() throws Exception {
        MyAreaPost postDetails = new MyAreaPost(null, "Updated Post", "Updated Content");
        MyAreaPost updatedPost = new MyAreaPost(1L, "Updated Post", "Updated Content");

        when(myAreaPostService.updatePost(1L, postDetails)).thenReturn(updatedPost);
        when(generateETagService.generateETagForPost(updatedPost)).thenReturn("etag1");

        mockMvc.perform(put("/api/myarea/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Post\", \"content\": \"Updated Content\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "etag1"))
                .andExpect(jsonPath("$.title").value("Updated Post"));
    }

    @Test
    void testDeletePost_ReturnsOkStatus() throws Exception {
        mockMvc.perform(delete("/api/myarea/{id}", 1L))
                .andExpect(status().isOk());

        verify(myAreaPostService, times(1)).deletePost(1L);
    }
}
