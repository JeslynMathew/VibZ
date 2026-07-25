package com.vibz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("albums")
@Data
@NoArgsConstructor
public class Album {

  @Id
  @JsonProperty("_id")
  private String id;

  private String title;
  private String artist;
  private String imageUrl;
  private int releaseYear;
  private Instant createdAt = Instant.now();
}
