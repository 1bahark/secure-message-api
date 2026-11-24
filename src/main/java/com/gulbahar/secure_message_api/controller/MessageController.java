package com.gulbahar.secure_message_api.controller;

import com.gulbahar.secure_message_api.dto.MessageResponse;
import com.gulbahar.secure_message_api.dto.SendMessageRequest;
import com.gulbahar.secure_message_api.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> send(Authentication authentication,
                                                @Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = messageService.send(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/inbox")
    public List<MessageResponse> inbox(Authentication authentication) {
        return messageService.inbox(authentication.getName());
    }

    @GetMapping("/sent")
    public List<MessageResponse> sent(Authentication authentication) {
        return messageService.sent(authentication.getName());
    }
}