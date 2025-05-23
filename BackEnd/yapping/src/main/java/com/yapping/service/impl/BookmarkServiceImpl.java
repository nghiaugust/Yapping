package com.yapping.service.impl;

import com.yapping.dto.bookmark.BookmarkDTO;
import com.yapping.dto.bookmark.CreateBookmarkDTO;
import com.yapping.entity.Bookmark;
import com.yapping.entity.BookmarkId;
import com.yapping.entity.Post;
import com.yapping.entity.User;
import com.yapping.exception.ResourceNotFoundException;
import com.yapping.repository.BookmarkRepository;
import com.yapping.repository.PostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository, UserRepository userRepository, PostRepository postRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Page<BookmarkDTO> getBookmarksByUserId(Long userId, Pageable pageable) {
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId, pageable);
        return bookmarks.map(this::convertToDto);
    }

    @Override
    public Page<BookmarkDTO> getBookmarksByPostId(Long postId, Pageable pageable) {
        Page<Bookmark> bookmarks = bookmarkRepository.findByPostId(postId, pageable);
        return bookmarks.map(this::convertToDto);
    }

    @Override
    public BookmarkDTO getBookmark(Long userId, Long postId) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark không tồn tại"));
        return convertToDto(bookmark);
    }

    @Override
    public boolean isBookmarked(Long userId, Long postId) {
        return bookmarkRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    @Transactional
    public BookmarkDTO createBookmark(CreateBookmarkDTO createBookmarkDTO) {
        // Kiểm tra xem bookmark đã tồn tại chưa
        if (bookmarkRepository.existsByUserIdAndPostId(createBookmarkDTO.getUserId(), createBookmarkDTO.getPostId())) {
            throw new IllegalArgumentException("Bookmark đã tồn tại");
        }

        // Tìm user
        User user = userRepository.findById(createBookmarkDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + createBookmarkDTO.getUserId()));

        // Tìm post
        Post post = postRepository.findById(createBookmarkDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài đăng với ID: " + createBookmarkDTO.getPostId()));

        // Tạo BookmarkId
        BookmarkId bookmarkId = new BookmarkId();
        bookmarkId.setUserId(createBookmarkDTO.getUserId());
        bookmarkId.setPostId(createBookmarkDTO.getPostId());

        // Tạo Bookmark
        Bookmark bookmark = new Bookmark();
        bookmark.setId(bookmarkId);
        bookmark.setUser(user);
        bookmark.setPost(post);
        bookmark.setCreatedAt(Instant.now());

        // Lưu vào database
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        return convertToDto(savedBookmark);
    }

    @Override
    @Transactional
    public void deleteBookmark(Long userId, Long postId) {
        if (!bookmarkRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new ResourceNotFoundException("Bookmark không tồn tại");
        }
        bookmarkRepository.deleteByUserIdAndPostId(userId, postId);
    }

    @Override
    public List<BookmarkDTO> getLatestBookmarks(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
        return bookmarks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long countBookmarksByPostId(Long postId) {
        return bookmarkRepository.countByPostId(postId);
    }

    @Override
    public List<BookmarkDTO> getBookmarksByDateRange(Instant startDate, Instant endDate) {
        // Phương thức này cần được cài đặt nếu cần thiết
        throw new UnsupportedOperationException("Phương thức này chưa được cài đặt");
    }

    @Override
    public Page<BookmarkDTO> getBookmarksByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        // Phương thức này cần được cài đặt nếu cần thiết
        throw new UnsupportedOperationException("Phương thức này chưa được cài đặt");
    }

    // Chuyển đổi từ Entity sang DTO
    private BookmarkDTO convertToDto(Bookmark bookmark) {
        BookmarkDTO dto = new BookmarkDTO();
        dto.setUserId(bookmark.getUser().getId());
        dto.setPostId(bookmark.getPost().getId());
        dto.setUsername(bookmark.getUser().getUsername());
        dto.setUserFullName(bookmark.getUser().getFullName());
        dto.setUserProfilePicture(bookmark.getUser().getProfilePicture());
        dto.setPostContent(bookmark.getPost().getContent());
        dto.setCreatedAt(bookmark.getCreatedAt());
        return dto;
    }
}
