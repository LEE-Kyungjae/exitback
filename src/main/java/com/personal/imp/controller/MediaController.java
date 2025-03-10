package com.personal.imp.controller;

import com.personal.imp.model.MediaFile;
import com.personal.imp.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PostMapping("/upload")
    public MediaFile uploadMedia(@RequestParam MultipartFile file, @RequestParam Long chatMessageId) throws IOException {
        return mediaService.saveMediaFile(file, chatMessageId);
    }
}
