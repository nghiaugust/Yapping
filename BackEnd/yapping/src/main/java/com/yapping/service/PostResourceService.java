package com.yapping.service;

import com.yapping.dto.post.PostDTO;
import com.yapping.dto.post.PostWithResourceDTO;

public interface PostResourceService {
    
    /**
     * Tạo bài đăng và tài liệu đồng thời
     * 
     * @param postWithResourceDTO Thông tin bài đăng và tài liệu
     * @param userId ID của người dùng
     * @return Thông tin bài đăng đã tạo kèm thông tin tài liệu
     */
    PostDTO createPostWithResource(PostWithResourceDTO postWithResourceDTO, Long userId);
}
