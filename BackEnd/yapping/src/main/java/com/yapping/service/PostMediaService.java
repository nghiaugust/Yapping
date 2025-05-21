package com.yapping.service;

import com.yapping.dto.media.MediaDTO;
import com.yapping.dto.post.PostDTO;
import com.yapping.dto.post.PostWithMediaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostMediaService {
    /**
     * Tạo bài đăng và media đính kèm
     * 
     * @param postWithMediaDTO thông tin bài đăng
     * @param files danh sách file media đính kèm
     * @param userId người dùng tạo bài đăng
     * @return PostDTO bài đăng đã tạo với danh sách media đính kèm
     */
    PostDTO createPostWithMedia(PostWithMediaDTO postWithMediaDTO, List<MultipartFile> files, Long userId) throws IOException;
}
