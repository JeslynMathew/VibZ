package com.vibz.repo;

import com.vibz.model.Song;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SongRepository extends MongoRepository<Song, String> {

  List<Song> findByAlbumId(String albumId);
}
