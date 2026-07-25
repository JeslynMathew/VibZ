package com.vibz.controller;

import com.vibz.dto.Requests.Login;
import com.vibz.dto.Requests.Register;
import com.vibz.model.User;
import com.vibz.repo.UserRepository;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserRepository users;
  private final String adminEmail;
  private final PasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthController(UserRepository users, @Value("${app.admin-email:}") String adminEmail) {
    this.users = users;
    this.adminEmail = adminEmail.trim();
  }

  @PostMapping("/register")
  public User register(@Valid @RequestBody Register body) {
    String id = body.email().trim().toLowerCase();
    if (users.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
    }

    User user = new User();
    user.setId(id);
    user.setClerkId(id);
    user.setFullName(body.fullName().trim());
    user.setImageUrl(body.imageUrl() == null || body.imageUrl().isBlank()
        ? "https://api.dicebear.com/9.x/initials/svg?seed=" + body.fullName().replace(" ", "+")
        : body.imageUrl());
    user.setPasswordHash(encoder.encode(body.password()));
    user.setAdmin(!adminEmail.isBlank() && adminEmail.equalsIgnoreCase(id));
    return users.save(user);
  }

  @PostMapping("/login")
  public User login(@Valid @RequestBody Login body) {
    String id = body.email().trim().toLowerCase();
    User user = users.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

    if (user.getPasswordHash() == null) {
      // Legacy account created before password auth existed (e.g. from earlier testing).
      // Rather than permanently locking it out, treat the first login attempt as claiming
      // the account: whatever password is typed here becomes its password going forward.
      user.setPasswordHash(encoder.encode(body.password()));
    } else if (!encoder.matches(body.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    // Keep the admin flag in sync in case ADMIN_EMAIL was changed since this account registered.
    user.setAdmin(!adminEmail.isBlank() && adminEmail.equalsIgnoreCase(id));
    return users.save(user);
  }

  @GetMapping("/me")
  public Map<String, Object> me(@RequestHeader("X-User-Id") String id) {
    User user = users.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    return Map.of("user", user, "admin", user.isAdmin());
  }
}
