package com.yapping.service;

import com.yapping.dto.media.MediaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MediaService {
    MediaDTO createMedia(Long postId, Long userId, MultipartFile file) throws IOException;
    MediaDTO getMediaById(Long mediaId);
    List<MediaDTO> getMediaByPostId(Long postId);
    List<MediaDTO> getMediaByUserId(Long userId);
    MediaDTO updateMedia(Long mediaId, MediaDTO mediaDTO);
    void deleteMedia(Long mediaId);
}
