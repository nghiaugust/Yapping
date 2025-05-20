package com.yapping.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;

@NamedEntityGraph(
        name = "Post.withUserAndParentPost",
        attributeNodes = {
                @NamedAttributeNode("user"), // cho phép user  tải cùng entity chính là post
                @NamedAttributeNode("parentPost")
        }
)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    public enum Visibility {
        PUBLIC, FOLLOWERS_ONLY, PRIVATE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "parent_post_id")
    private Post parentPost;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 50)
    @ColumnDefault("'PUBLIC'")
    private Visibility visibility;

    @ColumnDefault("0")
    @Column(name = "like_count")
    private Integer likeCount;

    @ColumnDefault("0")
    @Column(name = "comment_count")
    private Integer commentCount;

    @ColumnDefault("0")
    @Column(name = "repost_count")
    private Integer repostCount;

    @ColumnDefault("0")
    @Column(name = "quote_count")
    private Integer quoteCount;// trích dẫn

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum Type {
        TEXT, RESOURCE
    }
    @Column(name = "post_type")
    private Type post_type;

}