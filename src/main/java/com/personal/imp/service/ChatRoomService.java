package com.personal.imp.service;

import com.personal.imp.model.ChatRoom;
import com.personal.imp.model.User;
import com.personal.imp.repository.ChatRoomRepository;
import com.personal.imp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;  // UserRepository 추가

    public ChatRoom createChatRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomName(roomName);
        return chatRoomRepository.save(chatRoom);
    }

    public Optional<ChatRoom> findChatRoomById(Long id) {
        return chatRoomRepository.findById(id);
    }

    public void deleteChatRoom(Long id) {
        chatRoomRepository.deleteById(id);
    }

    public List<ChatRoom> getUserChatRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatRoomRepository.findByUsersContains(user); // 사용자 기반으로 채팅방 필터링
    }

    public void inviteUserToChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        chatRoom.getUsers().add(user);
        chatRoomRepository.save(chatRoom);
    }

    public ChatRoom updateChatRoomInfo(Long chatRoomId, String newRoomName, String newProfilePictureUrl) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        chatRoom.setRoomName(newRoomName);
        chatRoom.setProfilePictureUrl(newProfilePictureUrl);
        return chatRoomRepository.save(chatRoom);
    }
    public ChatRoom saveChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }
}
