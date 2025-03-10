package com.personal.imp.repository;

import com.personal.imp.model.ChatRoom;
import com.personal.imp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUsersContains(User user);

}
