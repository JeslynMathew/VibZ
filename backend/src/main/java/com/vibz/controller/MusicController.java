package com.vibz.controller;

import com.vibz.model.Album;
import com.vibz.model.Song;
import com.vibz.repo.AlbumRepository;
import com.vibz.repo.SongRepository;
import com.vibz.repo.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class MusicController {

  private final SongRepository songs;
  private final AlbumRepository albums;
  private final UserRepository users;

  public MusicController(SongRepository songs, AlbumRepository albums, UserRepository users) {
    this.songs = songs;
    this.albums = albums;
    this.users = users;
  }

  @GetMapping("/songs")
  public List<Song> songs() {
    return songs.findAll();
  }

  @GetMapping("/songs/{kind:featured|made-for-you|trending}")
  public List<Song> discovery() {
    List<Song> result = new ArrayList<>(songs.findAll());
    Collections.shuffle(result);
    return result.subList(0, Math.min(result.size(), 6));
  }

  @GetMapping("/albums")
  public List<Album> albums() {
    return albums.findAll();
  }

  @GetMapping("/albums/{id}")
  public Map<String, Object> album(@PathVariable String id) {
    Album album = albums.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));

    return Map.of(
        "_id", album.getId(),
        "title", album.getTitle(),
        "artist", album.getArtist(),
        "imageUrl", album.getImageUrl(),
        "releaseYear", album.getReleaseYear(),
        "songs", songs.findByAlbumId(id));
  }

  @GetMapping("/stats")
  public Map<String, Long> stats() {
    return Map.of(
        "totalSongs", songs.count(),
        "totalAlbums", albums.count(),
        "totalUsers", users.count(),
        "totalArtists", songs.findAll().stream()
            .map(Song::getArtist)
            .filter(Objects::nonNull)
            .distinct()
            .count());
  }
}
