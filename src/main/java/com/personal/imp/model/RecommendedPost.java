package com.personal.imp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class RecommendedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    private String author;

    private String mediaUrl;

    private LocalDateTime createdAt;

    private int likeCount;

    @ElementCollection
    private List<String> tags;
}
