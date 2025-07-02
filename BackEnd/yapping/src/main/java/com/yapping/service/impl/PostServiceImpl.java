package com.yapping.service.impl;

import com.yapping.dto.post.PatchPostDTO;
import com.yapping.dto.post.PostDTO;
import com.yapping.dto.media.MediaDTO;
import com.yapping.entity.FollowId;
import com.yapping.entity.Media;
import com.yapping.entity.Post;
import com.yapping.entity.User;
import com.yapping.repository.PostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.repository.MediaRepository;
import com.yapping.repository.FollowRepository;
import com.yapping.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MediaRepository mediaRepository;
      @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired 
    private FollowRepository followRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private MentionService mentionService;
    
    @Autowired
    private LikeService likeService;

    private PostDTO toPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post, postDTO, "user", "parentPost");

        // Ánh xạ User sang UserDTO
        PostDTO.UserDTO userDTO = new PostDTO.UserDTO();
        BeanUtils.copyProperties(post.getUser(), userDTO);
        postDTO.setUser(userDTO);

        // Ánh xạ parentPostId
        postDTO.setParentPostId(post.getParentPost() != null ? post.getParentPost().getId() : null);
        
        // Lấy danh sách media và thêm vào DTO
        List<Media> mediaList = mediaRepository.findByPostId(post.getId());
        if (mediaList != null && !mediaList.isEmpty()) {
            List<MediaDTO> mediaDTOList = mediaList.stream()
                .map(media -> {
                    MediaDTO mediaDTO = new MediaDTO();
                    BeanUtils.copyProperties(media, mediaDTO);
                    return mediaDTO;
                })
                .collect(java.util.stream.Collectors.toList());
            postDTO.setMedia(mediaDTOList);
        }

        return postDTO;
    }

    @Override
    public PostDTO createPost(PostDTO postDTO, Long userId) {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Chuyển DTO thành entity
        Post post = new Post();
        BeanUtils.copyProperties(postDTO, post, "id", "user", "parentPostId", "createdAt", "updatedAt");
        post.setUser(user);

        // Xử lý parentPost nếu có
        if (postDTO.getParentPostId() != null) {
            Post parentPost = postRepository.findById(postDTO.getParentPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent post not found with id: " + postDTO.getParentPostId()));
            post.setParentPost(parentPost);
        }

        // Lưu post
        Post savedPost = postRepository.save(post);
        
        // Xử lý mentions trong nội dung bài đăng
        if (postDTO.getContent() != null && !postDTO.getContent().isEmpty()) {
            mentionService.createMentionsFromText(
                    postDTO.getContent(), 
                    userId, 
                    savedPost.getId(), 
                    null
            );
        }
        
        // Tạo thông báo cho người theo dõi nếu bài đăng là PUBLIC hoặc FOLLOWERS_ONLY
        if (post.getVisibility() != Post.Visibility.PRIVATE) {
            // Lấy danh sách người theo dõi
            List<User> followers = userRepository.findFollowersByUserId(userId);
            
            // Tạo thông báo cho mỗi người theo dõi
            for (User follower : followers) {
                // Không tạo thông báo cho chính người đăng
                if (!follower.getId().equals(userId)) {
                    // Tạo thông báo về bài đăng mới
                    notificationService.createPostNotification(
                            follower.getId(),  // ID người nhận thông báo
                            userId,            // ID người đăng bài
                            savedPost.getId()  // ID bài đăng
                    );
                }
            }
        }
        
        // Chuyển entity thành DTO
        return toPostDTO(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByUserId(Long userId, Pageable pageable) {
        return postRepository.getPostsByUserId(userId, pageable)
                .map(this::toPostDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostDTO> getPostById(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        
        // Nếu người xem không phải là người tạo bài đăng
        if (!post.getUser().getId().equals(userId)) {
            // Kiểm tra visibility
            if (post.getVisibility() == Post.Visibility.PRIVATE) {
                // Nếu là PRIVATE, không cho phép truy cập
                throw new SecurityException("Người dùng không có quyền xem bài đăng riêng tư này");
            } 
            else if (post.getVisibility() == Post.Visibility.FOLLOWERS_ONLY) {
                // Nếu là FOLLOWERS_ONLY, kiểm tra xem người dùng có follow người tạo bài đăng không
                FollowId followId = new FollowId();
                followId.setFollowerId(userId);
                followId.setFollowedId(post.getUser().getId());
                
                // Sử dụng FollowRepository để kiểm tra
                if (!followRepository.existsById(followId)) {
                    throw new SecurityException("Người dùng phải follow người tạo bài đăng để xem nội dung này");
                }
            }
            // Nếu là PUBLIC thì cho phép xem mà không cần kiểm tra thêm
        }
        
        return Optional.of(toPostDTO(post));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPublicPosts(Pageable pageable) {
        return postRepository.findByVisibility(Post.Visibility.PUBLIC, pageable)
                .map(this::toPostDTO);
    }

    @Override
    public PostDTO updatePost(Long id, PostDTO postDTO, Long userId) {
        // Tìm post
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        // Kiểm tra quyền
        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this post");
        }

        // Cập nhật thông tin từ DTO
        BeanUtils.copyProperties(postDTO, post, "id", "user", "parentPostId", "createdAt", "updatedAt");

        // Xử lý parentPost nếu có thay đổi
        if (postDTO.getParentPostId() != null) {
            Post parentPost = postRepository.findById(postDTO.getParentPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent post not found with id: " + postDTO.getParentPostId()));
            post.setParentPost(parentPost);
        } else {
            post.setParentPost(null);
        }

        // Lưu post
        Post updatedPost = postRepository.save(post);

        // Xử lý mentions trong nội dung bài đăng cập nhật
        if (postDTO.getContent() != null && !postDTO.getContent().isEmpty()) {
            mentionService.createMentionsFromText(
                    postDTO.getContent(), 
                    userId, 
                    updatedPost.getId(), 
                    null
            );
        }

        // Chuyển entity thành DTO
        return toPostDTO(updatedPost);
    }

    @Override
    public PostDTO deletePost(Long id, Long userId) {
        // Tìm post
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        // Kiểm tra quyền
        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to delete this post");
        }
        
        // Lưu trữ thông tin post để trả về
        PostDTO postDTO = toPostDTO(post);
        
        // Xóa tất cả media files liên quan đến post
        List<Media> mediaList = mediaRepository.findByPostId(id);
        for (Media media : mediaList) {
            try {
                // Xóa file thực tế từ hệ thống lưu trữ
                if (media.getMediaUrl() != null && !media.getMediaUrl().isEmpty()) {
                    fileStorageService.deleteFile(media.getMediaUrl());
                }
            } catch (IOException e) {
                // Log lỗi nhưng không dừng quá trình xóa
                System.err.println("Không thể xóa media file: " + media.getMediaUrl() + " - Lỗi: " + e.getMessage());
            }
        }
        
        // Xóa post (Hibernate sẽ tự động xóa các media liên quan trong database do @OnDelete(action = OnDeleteAction.CASCADE))
        postRepository.delete(post);
        return postDTO;
    }

    @Override
    public PostDTO patchPost(Long id, PatchPostDTO patchPostDTO, Long userId) {
        // Tìm bài đăng
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        // Kiểm tra quyền
        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this post");
        }

        // Cập nhật các thuộc tính nếu được cung cấp
        if (patchPostDTO.getContent() != null) {
            post.setContent(patchPostDTO.getContent());
        }
        if (patchPostDTO.getVisibility() != null) {
            post.setVisibility(patchPostDTO.getVisibility());
        }
        if (patchPostDTO.getParentPostId() != null) {
            Post parentPost = postRepository.findById(patchPostDTO.getParentPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent post not found with id: " + patchPostDTO.getParentPostId()));
            post.setParentPost(parentPost);
        } else if (patchPostDTO.getParentPostId() == null && post.getParentPost() != null) {
            post.setParentPost(null); // Cho phép xóa parentPost
        }        
        // Lưu bài đăng
        Post updatedPost = postRepository.save(post);        
        
        // Xử lý mentions nếu nội dung đã được cập nhật
        if (patchPostDTO.getContent() != null && !patchPostDTO.getContent().isEmpty()) {
            mentionService.createMentionsFromText(
                    patchPostDTO.getContent(), 
                    userId, 
                    updatedPost.getId(), 
                    null
            );
        }
        
        // Chuyển thành DTO
        return toPostDTO(updatedPost);
    }
    
    @Override
    @Transactional
    public PostDTO likePost(Long postId, Long userId) {
        // Tìm bài đăng
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng với ID: " + postId));
        
        // Kiểm tra xem người dùng đã thích bài đăng chưa
        boolean hasLiked = likeService.hasUserLiked(userId, com.yapping.entity.Like.TargetType.POST, postId);
        
        if (!hasLiked) {
            // Tăng số lượt thích của bài đăng
            post.setLikeCount(post.getLikeCount() == null ? 1 : post.getLikeCount() + 1);
            postRepository.save(post);
            
            // Tạo thông báo cho chủ bài đăng nếu người thích không phải là chủ bài đăng
            if (!userId.equals(post.getUser().getId())) {
                notificationService.createLikePostNotification(
                        userId,
                        postId,
                        post.getUser().getId()
                );
            }
            
            // Tạo bản ghi like trong database
            likeService.likePost(userId, postId);
        }
        
        // Lấy bài đăng đã cập nhật
        Post updatedPost = postRepository.findById(postId).get();
        
        return toPostDTO(updatedPost);
    }

    // Admin methods implementation
    @Override
    public Page<PostDTO> getAllPostsForAdmin(Pageable pageable, Post.Visibility visibility, Post.Type postType) {
        Page<Post> posts;
        
        if (visibility != null && postType != null) {
            posts = postRepository.findByVisibilityAndPostType(visibility, postType, pageable);
        } else if (visibility != null) {
            posts = postRepository.findByVisibility(visibility, pageable);
        } else if (postType != null) {
            posts = postRepository.findByPostType(postType, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }
        
        return posts.map(this::toPostDTO);
    }

    @Override
    public Optional<PostDTO> getPostByIdForAdmin(Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.map(this::toPostDTO);
    }

    @Override
    public com.yapping.dto.post.PostDTO updatePostVisibilityByAdmin(Long id, Post.Visibility visibility) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng với ID: " + id));
        
        post.setVisibility(visibility);
        Post updatedPost = postRepository.save(post);
        
        return toPostDTO(updatedPost);
    }

    @Override
    public com.yapping.dto.post.PostDTO deletePostByAdmin(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng với ID: " + id));
        
        PostDTO postDTO = toPostDTO(post);
        
        // Xóa media files trước
        List<Media> mediaList = mediaRepository.findByPostId(id);
        for (Media media : mediaList) {
            try {
                fileStorageService.deleteFile(media.getMediaUrl());
            } catch (IOException e) {
                // Log error but continue with deletion
                System.err.println("Error deleting media file: " + e.getMessage());
            }
        }
        
        // Xóa bài đăng (cascade sẽ xóa media records)
        postRepository.delete(post);
        
        return postDTO;
    }

    @Override
    public com.yapping.dto.post.PostStatisticsDTO getPostStatistics() {
        Long totalPosts = postRepository.count();
        Long totalTextPosts = postRepository.countByPostType(Post.Type.TEXT);
        Long totalResourcePosts = postRepository.countByPostType(Post.Type.RESOURCE);
        
        Long publicPosts = postRepository.countByVisibility(Post.Visibility.PUBLIC);
        Long followersOnlyPosts = postRepository.countByVisibility(Post.Visibility.FOLLOWERS_ONLY);
        Long privatePosts = postRepository.countByVisibility(Post.Visibility.PRIVATE);
        
        Long postsWithMedia = postRepository.countPostsWithMedia();
        Long totalLikes = postRepository.sumAllLikes();
        Long totalComments = postRepository.sumAllComments();
        Long totalReposts = postRepository.sumAllReposts();

        Double averageInteraction = totalPosts > 0 ?
                ((double) (totalLikes + totalComments + totalReposts)) / totalPosts : 0.0;
        
        Double publicPercentage = totalPosts > 0 ? (publicPosts.doubleValue() / totalPosts) * 100 : 0.0;
        Double followersOnlyPercentage = totalPosts > 0 ? (followersOnlyPosts.doubleValue() / totalPosts) * 100 : 0.0;
        Double privatePercentage = totalPosts > 0 ? (privatePosts.doubleValue() / totalPosts) * 100 : 0.0;
        Double textPostPercentage = totalPosts > 0 ? (totalTextPosts.doubleValue() / totalPosts) * 100 : 0.0;
        Double resourcePostPercentage = totalPosts > 0 ? (totalResourcePosts.doubleValue() / totalPosts) * 100 : 0.0;
        
        // Tính thời gian
        java.time.Instant now = java.time.Instant.now();
        java.time.Instant startOfToday = now.truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        java.time.Instant startOfWeek = now.minus(7, java.time.temporal.ChronoUnit.DAYS);
        java.time.Instant startOfMonth = now.minus(30, java.time.temporal.ChronoUnit.DAYS);
        
        Long todayPosts = postRepository.countPostsToday(startOfToday);
        Long weekPosts = postRepository.countPostsThisWeek(startOfWeek);
        Long monthPosts = postRepository.countPostsThisMonth(startOfMonth);
        
        return new com.yapping.dto.post.PostStatisticsDTO(
            totalPosts, totalTextPosts, totalResourcePosts,
            publicPosts, followersOnlyPosts, privatePosts,
            postsWithMedia, totalLikes, totalComments, totalReposts,
            averageInteraction, publicPercentage, followersOnlyPercentage,
            privatePercentage, textPostPercentage, resourcePostPercentage,
            todayPosts, weekPosts, monthPosts
        );
    }
}