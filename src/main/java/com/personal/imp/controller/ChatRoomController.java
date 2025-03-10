package com.personal.imp.controller;

import com.personal.imp.model.ChatRoom;
import com.personal.imp.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chatroom")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ChatRoom createChatRoom(@RequestParam String roomName) {
        return chatRoomService.createChatRoom(roomName);
    }

    @GetMapping("/{id}")
    public Optional<ChatRoom> getChatRoom(@PathVariable Long id) {
        return chatRoomService.findChatRoomById(id);

    }

    @DeleteMapping("/delete/{id}")
    public void deleteChatRoom(@PathVariable Long id) {
        chatRoomService.deleteChatRoom(id);
    }

    @GetMapping("/user/{userId}")
    public List<ChatRoom> getUserChatRooms(@PathVariable Long userId) {
        return chatRoomService.getUserChatRooms(userId);
    }

    @PostMapping("/{chatRoomId}/invite/{userId}")
    public void inviteUser(@PathVariable Long chatRoomId, @PathVariable Long userId) {
        chatRoomService.inviteUserToChatRoom(chatRoomId, userId);
    }
    @PutMapping("/{chatRoomId}/update")
    public ChatRoom updateChatRoomInfo(@PathVariable Long chatRoomId,
                                       @RequestParam String newRoomName,
                                       @RequestParam String newProfilePictureUrl) {
        ChatRoom chatRoom = chatRoomService.findChatRoomById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        chatRoom.setRoomName(newRoomName);
        chatRoom.setProfilePictureUrl(newProfilePictureUrl);  // 프로필 이미지 URL 설정

        return chatRoomService.saveChatRoom(chatRoom);
    }



}

