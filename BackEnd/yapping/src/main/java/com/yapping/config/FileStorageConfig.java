package com.yapping.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.media-dir:${file.upload-dir}/media}")
    private String mediaDir;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Đăng ký thư mục lưu trữ file ảnh đại diện
        registry.addResourceHandler("/uploads/profile-pictures/**")
                .addResourceLocations("file:" + uploadDir + "/");

        // Đăng ký thư mục media
        registry.addResourceHandler("/uploads/media/**")
                .addResourceLocations("file:" + mediaDir + "/");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }      @Bean
    public String createUploadDirectoryIfNotExists() {
        // Tạo thư mục profile-pictures
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Tạo thư mục media
        File mediaDirectory = new File(mediaDir);
        if (!mediaDirectory.exists()) {
            mediaDirectory.mkdirs();
        }

        return uploadDir;
    }
}