package com.wedit.backend.api.media.entity;

import com.wedit.backend.api.media.entity.enums.MediaDomain;
import com.wedit.backend.api.media.entity.enums.MediaType;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "media", indexes = {
        @Index(name ="idx_media_owner", columnList = "ownerDomain, ownerId")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Media extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaDomain ownerDomain;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String mediaKey;        // S3 Key

    @Column(nullable = false)
    private String contentType;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;    // 미디어 타입 (이미지/비디오/오디오)

    private String mediaSubType;

    private String groupTitle;

    private String groupDescription;

    private int sortOrder;
}
