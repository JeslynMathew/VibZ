package com.vibz.controller;

import com.vibz.dto.Requests.MessageRequest;
import com.vibz.model.ChatMessage;
import com.vibz.model.User;
import com.vibz.repo.MessageRepository;
import com.vibz.repo.UserRepository;
import com.vibz.service.CurrentUser;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final CurrentUser current;
  private final UserRepository users;
  private final MessageRepository messages;

  public UserController(CurrentUser current, UserRepository users, MessageRepository messages) {
    this.current = current;
    this.users = users;
    this.messages = messages;
  }

  @GetMapping
  public List<User> users(@RequestHeader("X-User-Id") String id) {
    current.get(id);
    return users.findAll().stream()
        .filter(user -> !user.getId().equals(id))
        .toList();
  }

  @GetMapping("/messages/{other}")
  public List<ChatMessage> history(@RequestHeader("X-User-Id") String me, @PathVariable String other) {
    current.get(me);
    return messages.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtAsc(me, other, other, me);
  }

  @PostMapping("/messages")
  public ChatMessage message(@RequestHeader("X-User-Id") String me, @Valid @RequestBody MessageRequest body) {
    current.get(me);

    ChatMessage message = new ChatMessage();
    message.setSenderId(me);
    message.setReceiverId(body.receiverId());
    message.setContent(body.content().trim());
    return messages.save(message);
  }

  // Lightweight presence signal used by the chat UI to show who's online and what
  // they're currently listening to. Polled from the frontend every few seconds.
  @PostMapping("/heartbeat")
  public void heartbeat(@RequestHeader("X-User-Id") String me, @RequestBody(required = false) Map<String, String> body) {
    User user = current.get(me);
    user.setLastActiveAt(Instant.now());
    user.setActivity(body == null ? "Idle" : body.getOrDefault("activity", "Idle"));
    users.save(user);
  }
}
