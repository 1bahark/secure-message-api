package com.gulbahar.secure_message_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime createdAt;
}