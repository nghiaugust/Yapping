package com.yapping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PosthashtagId implements java.io.Serializable {
    private static final long serialVersionUID = 609600277925769949L;
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "hashtag_id", nullable = false)
    private Long hashtagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PosthashtagId entity = (PosthashtagId) o;
        return Objects.equals(this.hashtagId, entity.hashtagId) &&
                Objects.equals(this.postId, entity.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashtagId, postId);
    }

}