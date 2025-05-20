package com.yapping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ResourcetagrelationId implements java.io.Serializable {
    private static final long serialVersionUID = 7137565643069251889L;
    @NotNull
    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @NotNull
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ResourcetagrelationId entity = (ResourcetagrelationId) o;
        return Objects.equals(this.resourceId, entity.resourceId) &&
                Objects.equals(this.tagId, entity.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId, tagId);
    }

}