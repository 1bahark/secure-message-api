package com.gulbahar.secure_message_api.service;

import com.gulbahar.secure_message_api.dto.MessageResponse;
import com.gulbahar.secure_message_api.dto.SendMessageRequest;
import com.gulbahar.secure_message_api.exception.ResourceNotFoundException;
import com.gulbahar.secure_message_api.model.Message;
import com.gulbahar.secure_message_api.model.User;
import com.gulbahar.secure_message_api.repository.MessageRepository;
import com.gulbahar.secure_message_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    private Message createMessage(User sender, User receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }

    @Test
    void send_savesMessageAndReturnsResponse() {
        User bahar = createUser("bahar");
        User ali = createUser("ali");
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverUsername("ali");
        request.setContent("Merhaba Ali!");

        when(userRepository.findByUsername("bahar")).thenReturn(Optional.of(bahar));
        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(ali));
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        MessageResponse response = messageService.send("bahar", request);

        assertEquals("bahar", response.getSender());
        assertEquals("ali", response.getReceiver());
        assertEquals("Merhaba Ali!", response.getContent());
    }

    @Test
    void send_throwsNotFound_whenReceiverDoesNotExist() {
        User bahar = createUser("bahar");
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverUsername("kimseyok");
        request.setContent("Merhaba");

        when(userRepository.findByUsername("bahar")).thenReturn(Optional.of(bahar));
        when(userRepository.findByUsername("kimseyok")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> messageService.send("bahar", request));
        verify(messageRepository, never()).save(any());
    }

    @Test
    void inbox_returnsMessagesSentToUser() {
        User bahar = createUser("bahar");
        User ali = createUser("ali");

        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(ali));
        when(messageRepository.findByReceiverOrderByCreatedAtDesc(ali))
                .thenReturn(List.of(createMessage(bahar, ali, "Merhaba Ali!")));

        List<MessageResponse> inbox = messageService.inbox("ali");

        assertEquals(1, inbox.size());
        assertEquals("bahar", inbox.get(0).getSender());
        assertEquals("Merhaba Ali!", inbox.get(0).getContent());
    }

    @Test
    void sent_returnsMessagesSentByUser() {
        User bahar = createUser("bahar");
        User ali = createUser("ali");

        when(userRepository.findByUsername("bahar")).thenReturn(Optional.of(bahar));
        when(messageRepository.findBySenderOrderByCreatedAtDesc(bahar))
                .thenReturn(List.of(createMessage(bahar, ali, "Selam")));

        List<MessageResponse> sent = messageService.sent("bahar");

        assertEquals(1, sent.size());
        assertEquals("ali", sent.get(0).getReceiver());
    }
}