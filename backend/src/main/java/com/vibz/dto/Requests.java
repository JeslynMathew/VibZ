package com.vibz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class Requests {

  private Requests() {
  }

  public record Register(
      @NotBlank(message = "Please enter your name") @Size(max = 60, message = "Name is too long") String fullName,
      @NotBlank(message = "Please enter an email") @Email(message = "Please enter a valid email address")
      @Size(max = 254) String email,
      @NotBlank(message = "Please enter a password")
      @Size(min = 6, max = 72, message = "Password must be at least 6 characters") String password,
      String imageUrl) {
  }

  public record Login(
      @NotBlank(message = "Please enter an email") @Email(message = "Please enter a valid email address") String email,
      @NotBlank(message = "Please enter your password") String password) {
  }

  public record AlbumRequest(
      @NotBlank String title,
      @NotBlank String artist,
      @NotBlank String imageUrl,
      @Min(1900) int releaseYear) {
  }

  public record SongRequest(
      @NotBlank String title,
      @NotBlank String artist,
      @NotBlank String imageUrl,
      @NotBlank String audioUrl,
      @Min(0) int duration,
      String albumId) {
  }

  public record MessageRequest(
      @NotBlank String receiverId,
      @NotBlank @Size(max = 1000) String content) {
  }
}
