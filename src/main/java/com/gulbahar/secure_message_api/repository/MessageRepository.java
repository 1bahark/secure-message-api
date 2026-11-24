package com.gulbahar.secure_message_api.repository;

import com.gulbahar.secure_message_api.model.Message;
import com.gulbahar.secure_message_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverOrderByCreatedAtDesc(User receiver);

    List<Message> findBySenderOrderByCreatedAtDesc(User sender);
}