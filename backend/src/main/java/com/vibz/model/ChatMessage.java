package com.vibz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("messages")
@Data
@NoArgsConstructor
public class ChatMessage {

  @Id
  @JsonProperty("_id")
  private String id;

  private String senderId;
  private String receiverId;
  private String content;
  private Instant createdAt = Instant.now();
}
