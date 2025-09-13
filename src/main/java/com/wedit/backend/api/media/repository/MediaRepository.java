package com.wedit.backend.api.media.repository;

import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.media.entity.enums.MediaDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    // 특정 소유자(owner)에게 속한 모든 미디어를 정렬 순서에 따라 조회
    List<Media> findByOwnerDomainAndOwnerIdOrderBySortOrderAsc(MediaDomain ownerDomain, Long ownerId);

    // 특정 소유자(owner)에게 속한 모든 미디어를 정렬 순서에 따라 조회
    List<Media> findByOwnerDomainAndOwnerIdAndGroupTitleOrderBySortOrderAsc(MediaDomain ownerDomain, Long ownerId, String groupTitle);

    // 특정 소유자에게 속하면서, 특정 하위 타입(역할)을 가진 미디어 조회
    // Invitation의 BGM이나 LOGO 처럼
    Optional<Media> findByOwnerDomainAndOwnerIdAndMediaSubType(MediaDomain ownerDomain, Long ownerId, String mediaSubType);

    // 특정 소유자에게 속한 모든 미디어를 한 번에 삭제
    void deleteByOwnerDomainAndOwnerId(MediaDomain ownerDomain, Long ownerId);

    List<Media> findAllByOwnerDomainAndOwnerIdIn(MediaDomain ownerDomain, List<Long> ownerIds);
}
