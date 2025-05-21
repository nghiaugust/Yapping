package com.yapping.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${file.media-dir:${file.upload-dir}/media}")
    private String mediaDir;

    public String storeFile(MultipartFile file) throws IOException {
        // Tạo tên file sử dụng timestamp hiện tại để tránh trùng lặp
        long timestamp = System.currentTimeMillis();
        String originalFileName = file.getOriginalFilename();
        String fileName = timestamp + "_" + originalFileName;

        // Tạo đường dẫn đầy đủ
        Path targetLocation = Paths.get(uploadDir).resolve(fileName);

        // Đảm bảo thư mục tồn tại
        Files.createDirectories(targetLocation.getParent());
        
        // Sao chép file vào thư mục đích và ghi đè nếu đã tồn tại
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL relative để lưu vào database
        return "/uploads/profile-pictures/" + fileName;
    }
    
    public String storeMediaFile(MultipartFile file, String fileName) throws IOException {
        // Đảm bảo thư mục media tồn tại
        Path mediaDirPath = Paths.get(mediaDir);
        Files.createDirectories(mediaDirPath);
        
        // Tạo đường dẫn đầy đủ
        Path targetLocation = mediaDirPath.resolve(fileName);
        
        // Sao chép file vào thư mục đích và ghi đè nếu đã tồn tại
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Trả về URL relative để lưu vào database
        return "/uploads/media/" + fileName;
    }    public void deleteFile(String filePath) throws IOException {
        if (filePath != null && !filePath.isEmpty()) {
            // Lấy tên file từ đường dẫn
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            
            // Xác định đường dẫn của file dựa vào cấu trúc URL
            Path targetLocation;
            if (filePath.contains("/media/")) {
                // File media
                targetLocation = Paths.get(mediaDir).resolve(fileName);
            } else {
                // Mặc định là profile picture hoặc khác
                targetLocation = Paths.get(uploadDir).resolve(fileName);
            }

            // Xóa file nếu tồn tại
            boolean deleted = Files.deleteIfExists(targetLocation);
            if (!deleted) {
                System.err.println("Không tìm thấy file để xóa: " + filePath);
            }
        }
    }
}