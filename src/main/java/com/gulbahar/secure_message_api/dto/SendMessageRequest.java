package com.gulbahar.secure_message_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank
    private String receiverUsername;

    @NotBlank
    @Size(max = 1000)
    private String content;
}