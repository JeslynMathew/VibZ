package com.vibz.config;

import com.vibz.model.Song;
import com.vibz.repo.SongRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedData {

  @Bean
  CommandLineRunner sampleSongs(SongRepository songs) {
    return args -> {
      if (songs.count() > 0) {
        return;
      }

      Song song = new Song();
      song.setTitle("Demo Track");
      song.setArtist("VibZ Studio");
      song.setDuration(30);
      song.setImageUrl("https://images.unsplash.com/photo-1516280440614-37939bbacd81?w=600");
      song.setAudioUrl("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
      songs.save(song);
    };
  }
}
