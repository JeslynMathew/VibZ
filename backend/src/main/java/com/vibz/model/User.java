package com.vibz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @JsonProperty("_id")
  private String id;

  private String clerkId;
  private String fullName;
  private String imageUrl;
  private boolean admin;
  private Instant createdAt = Instant.now();
  private Instant lastActiveAt;
  private String activity = "Idle";

  @JsonIgnore
  private String passwordHash;
}
