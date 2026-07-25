package com.vibz.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final String uploadDir;
  private final String corsOrigin;

  public WebConfig(
      @Value("${app.upload-dir:uploads}") String uploadDir,
      @Value("${app.cors-origin:http://localhost:3000}") String corsOrigin) {
    this.uploadDir = uploadDir;
    this.corsOrigin = corsOrigin;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(corsOrigin)
        .allowedMethods("*")
        .allowedHeaders("*");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations(Path.of(uploadDir).toAbsolutePath().normalize().toUri().toString());
  }
}
