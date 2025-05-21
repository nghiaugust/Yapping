package com.yapping.service.impl;

import com.yapping.dto.media.MediaDTO;
import com.yapping.entity.Media;
import com.yapping.entity.Post;
import com.yapping.entity.User;
import com.yapping.repository.MediaRepository;
import com.yapping.repository.PostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.FileStorageService;
import com.yapping.service.MediaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    private MediaRepository mediaRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional
    public MediaDTO createMedia(Long postId, Long userId, MultipartFile file) throws IOException {
        // Kiểm tra user tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        
        // Kiểm tra post tồn tại
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));
        
        // Xác định loại media dựa trên contentType
        Media.MediaType mediaType;
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new RuntimeException("Không thể xác định loại file");
        } else if (contentType.startsWith("image/gif")) {
            mediaType = Media.MediaType.GIF;
        } else if (contentType.startsWith("image/")) {
            mediaType = Media.MediaType.IMAGE;
        } else if (contentType.startsWith("video/")) {
            mediaType = Media.MediaType.VIDEO;
        } else {
            throw new RuntimeException("Loại file không được hỗ trợ");
        }
        
        // Tạo tên file theo định dạng: userId_originalFileName_timestamp
        String userIdStr = userId.toString();
        String originalFileName = file.getOriginalFilename();
        String fileName = userIdStr + "_" + System.currentTimeMillis()+ "_" + originalFileName ;
        
        // Lưu file vào thư mục media
        String mediaUrl = fileStorageService.storeMediaFile(file, fileName);
        
        // Tạo đối tượng Media mới
        Media media = new Media();
        media.setUser(user);
        media.setPost(post);
        media.setMediaUrl(mediaUrl);
        media.setMediaType(mediaType);
        media.setSortOrder(0); // Mặc định
        media.setCreatedAt(Instant.now());
        
        // Lưu vào database
        media = mediaRepository.save(media);
        
        // Chuyển đổi và trả về DTO
        return convertToDTO(media);
    }

    @Override
    public MediaDTO getMediaById(Long mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy media với ID: " + mediaId));
        return convertToDTO(media);
    }

    @Override
    public List<MediaDTO> getMediaByPostId(Long postId) {
        return mediaRepository.findByPostId(postId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MediaDTO> getMediaByUserId(Long userId) {
        return mediaRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MediaDTO updateMedia(Long mediaId, MediaDTO mediaDTO) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy media với ID: " + mediaId));
        
        // Chỉ cho phép cập nhật một số trường nhất định
        if (mediaDTO.getMediaType() != null) {
            media.setMediaType(mediaDTO.getMediaType());
        }
        if (mediaDTO.getSortOrder() != null) {
            media.setSortOrder(mediaDTO.getSortOrder());
        }
        
        // Lưu các thay đổi
        media = mediaRepository.save(media);
        return convertToDTO(media);
    }

    @Override
    @Transactional
    public void deleteMedia(Long mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy media với ID: " + mediaId));
        
        // Xóa file khỏi hệ thống file
        try {
            fileStorageService.deleteFile(media.getMediaUrl());
        } catch (IOException e) {
            // Log lỗi nhưng vẫn tiếp tục xóa khỏi database
            System.err.println("Không thể xóa file: " + e.getMessage());
        }
        
        // Xóa khỏi database
        mediaRepository.delete(media);
    }
    
    private MediaDTO convertToDTO(Media media) {
        MediaDTO mediaDTO = new MediaDTO();
        BeanUtils.copyProperties(media, mediaDTO);
        mediaDTO.setUserId(media.getUser().getId());
        mediaDTO.setPostId(media.getPost().getId());
        return mediaDTO;
    }
}
