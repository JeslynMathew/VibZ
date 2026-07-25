package com.vibz.controller;

import com.vibz.model.Album;
import com.vibz.model.Song;
import com.vibz.repo.AlbumRepository;
import com.vibz.repo.SongRepository;
import com.vibz.repo.UserRepository;
import com.vibz.service.CurrentUser;
import com.vibz.service.MediaStorage;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final CurrentUser current;
  private final SongRepository songs;
  private final AlbumRepository albums;
  private final UserRepository users;
  private final MediaStorage media;

  public AdminController(
      CurrentUser current,
      SongRepository songs,
      AlbumRepository albums,
      UserRepository users,
      MediaStorage media) {
    this.current = current;
    this.songs = songs;
    this.albums = albums;
    this.users = users;
    this.media = media;
  }

  @GetMapping("/check")
  public Map<String, Boolean> check(@RequestHeader("X-User-Id") String id) {
    return Map.of("admin", current.get(id).isAdmin());
  }

  @PostMapping(value = "/albums", consumes = "multipart/form-data")
  public Album album(
      @RequestHeader("X-User-Id") String id,
      @RequestParam String title,
      @RequestParam String artist,
      @RequestParam int releaseYear,
      @RequestParam MultipartFile imageFile) {
    current.admin(id);

    Album album = new Album();
    album.setTitle(title);
    album.setArtist(artist);
    album.setReleaseYear(releaseYear);
    album.setImageUrl(media.store(imageFile, "images"));
    return albums.save(album);
  }

  @PostMapping(value = "/songs", consumes = "multipart/form-data")
  public Song song(
      @RequestHeader("X-User-Id") String id,
      @RequestParam String title,
      @RequestParam String artist,
      @RequestParam int duration,
      @RequestParam(required = false) String albumId,
      @RequestParam MultipartFile audioFile,
      @RequestParam MultipartFile imageFile) {
    current.admin(id);

    if (albumId != null && !albumId.isBlank() && !albums.existsById(albumId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Album does not exist");
    }

    Song song = new Song();
    song.setTitle(title);
    song.setArtist(artist);
    song.setDuration(duration);
    song.setAlbumId(albumId);
    song.setAudioUrl(media.store(audioFile, "audio"));
    song.setImageUrl(media.store(imageFile, "images"));
    return songs.save(song);
  }

  @DeleteMapping("/songs/{id}")
  public void deleteSong(@RequestHeader("X-User-Id") String user, @PathVariable String id) {
    current.admin(user);
    songs.deleteById(id);
  }

  @DeleteMapping("/albums/{id}")
  public void deleteAlbum(@RequestHeader("X-User-Id") String user, @PathVariable String id) {
    current.admin(user);
    songs.findByAlbumId(id).forEach(song -> {
      song.setAlbumId(null);
      songs.save(song);
    });
    albums.deleteById(id);
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
