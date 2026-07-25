package com.vibz.service;

import com.vibz.model.User;
import com.vibz.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUser {

  private final UserRepository users;

  public CurrentUser(UserRepository users) {
    this.users = users;
  }

  public User get(String id) {
    if (id == null || id.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in first");
    }
    return users.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
  }

  public User admin(String id) {
    User user = get(id);
    if (!user.isAdmin()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access required");
    }
    return user;
  }
}
