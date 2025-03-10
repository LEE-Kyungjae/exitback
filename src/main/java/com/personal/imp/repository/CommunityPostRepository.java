package com.personal.imp.repository;

import com.personal.imp.model.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost,Long> {
}
