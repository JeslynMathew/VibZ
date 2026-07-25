package com.vibz.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MediaStorage {
  private final Path root;
  private final String baseUrl;

  public MediaStorage(@Value("${app.upload-dir:uploads}") String uploadDir,
                       @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
    this.root = Path.of(uploadDir).toAbsolutePath().normalize();
    // strip any trailing slash so we don't end up with a double slash when building URLs below
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
  }

  public String store(MultipartFile file, String category) {
    if (file == null || file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A file is required");
    String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
    String extension = original.contains(".") ? original.substring(original.lastIndexOf('.')).toLowerCase() : "";
    try {
      Path folder = root.resolve(category);
      Files.createDirectories(folder);
      String filename = UUID.randomUUID() + extension;
      Files.copy(file.getInputStream(), folder.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
      // Absolute URL: works no matter how/where the React app is served (dev server, static
      // build, different port, etc). A relative "/uploads/..." path used to resolve against
      // whatever origin the frontend happened to be on, which is why images broke and audio
      // wouldn't play whenever the frontend wasn't proxied straight through to this backend.
      return baseUrl + "/uploads/" + category + "/" + filename;
    } catch (IOException exception) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save uploaded file");
    }
  }
}
