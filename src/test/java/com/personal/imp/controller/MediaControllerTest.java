package com.personal.imp.controller;

import com.personal.imp.model.MediaFile;
import com.personal.imp.service.MediaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaController.class)
public class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    @Test
    void testUploadMedia() throws Exception {
        // Given: MockMultipartFile 생성
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "Test Image Content".getBytes()
        );
        Long chatMessageId = 123L;

        // 반환할 MediaFile 객체 정의
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(1L);
        mediaFile.setFileName("test-image.png");
        mediaFile.setFileType(MediaType.IMAGE_PNG_VALUE);
        mediaFile.setChatMessageId(chatMessageId);

        // When: mediaService.saveMediaFile 호출 시 mediaFile 반환하도록 설정
        when(mediaService.saveMediaFile(file, chatMessageId)).thenReturn(mediaFile);

        // Then: 파일 업로드 요청 및 응답 검증
        mockMvc.perform(multipart("/media/upload")
                        .file(file)
                        .param("chatMessageId", String.valueOf(chatMessageId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fileName").value("test-image.png"))
                .andExpect(jsonPath("$.fileType").value(MediaType.IMAGE_PNG_VALUE))
                .andExpect(jsonPath("$.chatMessageId").value(chatMessageId));

        verify(mediaService, times(1)).saveMediaFile(file, chatMessageId);
    }
}
