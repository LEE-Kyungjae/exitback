package com.personal.imp.service;

import com.personal.imp.model.MediaFile;
import com.personal.imp.repository.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class MediaService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    // 파일을 스토리지에 저장하고, URL을 DB에 저장하는 기능
    public MediaFile saveMediaFile(MultipartFile file, Long chatMessageId) throws IOException {
        // 파일을 스토리지에 저장하는 로직 추가 (예: AWS S3, 로컬 파일 시스템 등)

        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileName(file.getOriginalFilename());
        mediaFile.setFileType(file.getContentType());
        mediaFile.setChatMessageId(chatMessageId);

        // URL 생성 로직 (스토리지의 파일 위치에 따라 다름)
        String url = "https://storage-service.com/files/" + file.getOriginalFilename();
        mediaFile.setUrl(url);

        return mediaFileRepository.save(mediaFile);
    }
}
