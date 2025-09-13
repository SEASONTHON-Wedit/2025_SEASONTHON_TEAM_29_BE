package com.wedit.backend.api.media.service;

import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.media.entity.enums.MediaDomain;
import com.wedit.backend.api.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MediaService {

    private final MediaRepository mediaRepository;
    private final S3Service s3Service;

    /**
     * 특정 소유자에 속한 모든 미디어의 'CDN URL 목록'을 반환합니다.
     * VendorService, ReviewService 등에서 이 메서드를 호출하여 DTO에 이미지 목록을 채워넣습니다.
     * @param ownerDomain 소유자 도메인 (e.g., VENDOR, REVIEW)
     * @param ownerId 소유자 ID
     * @return S3 Key가 CDN URL로 변환된 문자열 리스트
     */
    public List<String> findMediaUrls(MediaDomain ownerDomain, Long ownerId) {

        List<Media> mediaList = mediaRepository
                .findByOwnerDomainAndOwnerIdOrderBySortOrderAsc(ownerDomain, ownerId);

        return mediaList.stream()
                .map(Media::getMediaKey)
                .map(s3Service::toCdnUrl)
                .collect(Collectors.toList());
    }

    public List<String> findMediaUrls(MediaDomain ownerDomain, Long ownerId, String groupTitle) {

        List<Media> mediaList = mediaRepository
                .findByOwnerDomainAndOwnerIdAndGroupTitleOrderBySortOrderAsc(ownerDomain, ownerId, groupTitle);

        return mediaList.stream()
                .map(Media::getMediaKey)
                .map(s3Service::toCdnUrl)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 Media 엔티티를 생성하고 DB에 저장합니다.
     * 파일 업로드 후, Vendor나 Review 등을 생성/수정할 때 호출됩니다.
     */
    @Transactional
    public Media save(Media media) {
        return mediaRepository.save(media);
    }

    /**
     * 여러 개의 Media 엔티티를 한 번에 저장합니다.
     */
    @Transactional
    public List<Media> saveAll(List<Media> mediaList) {

        return mediaRepository.saveAll(mediaList);
    }

    /**
     * S3 key를 CDN URL로 변환하는 책임을 S3Service에 위임합니다.
     */
    public String toCdnUrl(String key) {
        return s3Service.toCdnUrl(key);
    }

    /**
     * 특정 소유자와 관련된 모든 Media 정보 및 S3 파일을 삭제합니다.
     * 애플리케이션 레벨에서 참조 무결성을 보장
     * @param ownerDomain 삭제할 미디어의 소유자 도메인
     * @param ownerId 삭제할 미디어의 소유자 ID
     */
    @Transactional
    public void deleteAllByOwner(MediaDomain ownerDomain, Long ownerId) {

        // 1. DB에서 관련 미디어 목록을 조회
        List<Media> mediaList = mediaRepository.findByOwnerDomainAndOwnerIdOrderBySortOrderAsc(ownerDomain, ownerId);
        if (mediaList.isEmpty()) {
            return;
        }

        // 2. S3에서 실제 파일들을 삭제
        List<String> keysToDelete = mediaList.stream()
                .map(Media::getMediaKey)
                .collect(Collectors.toList());
        s3Service.deleteFiles(keysToDelete);

        // 3. DB에서 Media 데이터들을 삭제
        mediaRepository.deleteAllInBatch(mediaList);
    }

    /**
     * 여러 소유자(owner)에 속한 모든 미디어를 조회합니다. (N+1 문제 해결용)
     */
    public List<Media> findAllByOwnerDomainAndOwnerIds(MediaDomain ownerDomain, List<Long> ownerIds) {
        if (ownerIds == null || ownerIds.isEmpty()) {
            return Collections.emptyList();
        }
        return mediaRepository.findAllByOwnerDomainAndOwnerIdIn(ownerDomain, ownerIds);
    }

    /**
     * 특정 소유자의 특정 하위 타입 미디어를 조회합니다.
     */
    public Optional<Media> findByOwnerAndSubType(MediaDomain ownerDomain, Long ownerId, String mediaSubType) {
        return mediaRepository.findByOwnerDomainAndOwnerIdAndMediaSubType(ownerDomain, ownerId, mediaSubType);
    }
}
