package com.gulbahar.secure_message_api.service;

import com.gulbahar.secure_message_api.dto.MessageResponse;
import com.gulbahar.secure_message_api.dto.SendMessageRequest;
import com.gulbahar.secure_message_api.model.Message;
import com.gulbahar.secure_message_api.model.User;
import com.gulbahar.secure_message_api.repository.MessageRepository;
import com.gulbahar.secure_message_api.repository.UserRepository;
import com.gulbahar.secure_message_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageResponse send(String senderUsername, SendMessageRequest request) {
        User sender = findUser(senderUsername);
        User receiver = findUser(request.getReceiverUsername());

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setCreatedAt(LocalDateTime.now());

        return toResponse(messageRepository.save(message));
    }

    public List<MessageResponse> inbox(String username) {
        User user = findUser(username);
        return messageRepository.findByReceiverOrderByCreatedAtDesc(user)
                .stream().map(this::toResponse).toList();
    }

    public List<MessageResponse> sent(String username) {
        User user = findUser(username);
        return messageRepository.findBySenderOrderByCreatedAtDesc(user)
                .stream().map(this::toResponse).toList();
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private MessageResponse toResponse(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getSender().getUsername(),
                m.getReceiver().getUsername(),
                m.getContent(),
                m.getCreatedAt());
    }
}