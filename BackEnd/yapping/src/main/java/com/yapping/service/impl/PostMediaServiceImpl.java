package com.yapping.service.impl;

import com.yapping.dto.media.MediaDTO;
import com.yapping.dto.post.PostDTO;
import com.yapping.dto.post.PostWithMediaDTO;
import com.yapping.entity.Media;
import com.yapping.entity.Post;
import com.yapping.entity.User;
import com.yapping.repository.MediaRepository;
import com.yapping.repository.PostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.FileStorageService;
import com.yapping.service.PostMediaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostMediaServiceImpl implements PostMediaService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional
    public PostDTO createPostWithMedia(PostWithMediaDTO postWithMediaDTO, List<MultipartFile> files, Long userId) throws IOException {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        // 1. Tạo Post trước
        Post post = new Post();
        BeanUtils.copyProperties(postWithMediaDTO, post, "id", "user", "parentPostId", "createdAt", "updatedAt");
        post.setUser(user);
        post.setPost_type(Post.Type.TEXT); // Đặt loại bài viết là TEXT
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setRepostCount(0);
        post.setQuoteCount(0);
        
        // Xử lý parentPost nếu có
        if (postWithMediaDTO.getParentPostId() != null) {
            Post parentPost = postRepository.findById(postWithMediaDTO.getParentPostId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết gốc với ID: " + postWithMediaDTO.getParentPostId()));
            post.setParentPost(parentPost);
        }

        // Lưu post
        Post savedPost = postRepository.save(post);

        // 2. Tạo Media cho từng file
        List<Media> mediaList = new ArrayList<>();
        int sortOrder = 0;

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // Xác định loại media
                Media.MediaType mediaType;
                String contentType = file.getContentType();
                String originalFileName = file.getOriginalFilename();

                if (contentType == null || contentType.equals("application/octet-stream")) {
                    if (originalFileName != null) {
                        String lowercaseFileName = originalFileName.toLowerCase();
                        if (lowercaseFileName.endsWith(".gif")) {
                            mediaType = Media.MediaType.GIF;
                        } else if (lowercaseFileName.endsWith(".jpg") || lowercaseFileName.endsWith(".jpeg") ||
                                lowercaseFileName.endsWith(".png")) {
                            mediaType = Media.MediaType.IMAGE;
                        } else if (lowercaseFileName.endsWith(".mp4") || lowercaseFileName.endsWith(".mov")) {
                            mediaType = Media.MediaType.VIDEO;
                        } else {
                            throw new RuntimeException("Loại file không được hỗ trợ: " + originalFileName);
                        }
                    } else {
                        throw new RuntimeException("Không thể xác định loại file");
                    }
                } else if (contentType.startsWith("image/gif")) {
                    mediaType = Media.MediaType.GIF;
                } else if (contentType.startsWith("image/")) {
                    mediaType = Media.MediaType.IMAGE;
                } else if (contentType.startsWith("video/")) {
                    mediaType = Media.MediaType.VIDEO;
                } else {
                    throw new RuntimeException("Loại file không được hỗ trợ: " + contentType);
                }

                // Tạo tên file
                String userIdStr = userId.toString();
                String fileName = userIdStr + "_" + System.currentTimeMillis()+ "_" + originalFileName ;

                // Lưu file
                String mediaUrl = fileStorageService.storeMediaFile(file, fileName);

                // Tạo Media
                Media media = new Media();
                media.setUser(user);
                media.setPost(savedPost);
                media.setMediaUrl(mediaUrl);
                media.setMediaType(mediaType);
                media.setSortOrder(sortOrder++);
                media.setCreatedAt(Instant.now());

                mediaList.add(media);
            }

            // Lưu tất cả media
            mediaRepository.saveAll(mediaList);
        }

        // 3. Chuyển Post thành DTO
        PostDTO postDTO = convertToPostDTO(savedPost, mediaList);

        return postDTO;
    }

    private PostDTO convertToPostDTO(Post post, List<Media> mediaList) {
        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post, postDTO, "user", "parentPost");

        PostDTO.UserDTO userDTO = new PostDTO.UserDTO();
        BeanUtils.copyProperties(post.getUser(), userDTO);
        postDTO.setUser(userDTO);

        postDTO.setParentPostId(post.getParentPost() != null ? post.getParentPost().getId() : null);

        List<MediaDTO> mediaDTOs = new ArrayList<>();
        for (Media media : mediaList) {
            MediaDTO mediaDTO = new MediaDTO();
            BeanUtils.copyProperties(media, mediaDTO);
            mediaDTO.setUserId(media.getUser().getId());
            mediaDTO.setPostId(media.getPost().getId());
            mediaDTOs.add(mediaDTO);
        }
        postDTO.setMedia(mediaDTOs);

        return postDTO;
    }
}