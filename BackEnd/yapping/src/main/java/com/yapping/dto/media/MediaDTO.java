package com.yapping.dto.media;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yapping.entity.Media;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaDTO {

    private Long id;

    @NotNull
    private Long postId;

    @NotNull
    private Long userId;

    @NotBlank
    private String mediaUrl;

    @NotNull
    private Media.MediaType mediaType;

    private Integer sortOrder;

    private Instant createdAt;
}