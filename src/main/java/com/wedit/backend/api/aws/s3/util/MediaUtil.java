package com.wedit.backend.api.aws.s3.util;

import java.util.List;

public class MediaUtil {

    // 허용 이미지 파일 크기 - 20MB
    private static final long MAX_IMAGE_FILE_SIZE = 20 * 1024 * 1024;
    // 허용 동영상 파일 크기 - 150MB
    private static final long MAX_VIDEO_FILE_SIZE = 150 * 1024 * 1024;
    // 허용 음원 파일 크기 - 15MB
    private static final long MAX_AUDIO_FILE_SIZE = 15 * 1024 * 1024;

    // 이미지 허용 Content-Type 목록
    private static final List<String> ALLOWED_IMAGE_CONTENT_TYPES = List.of(
            "image/jpeg",       // jpeg
            "image/jpg",        // jpg (일부 브라우저)
            "image/png",        // png
            "image/gif",        // gif
            "image/webp"        // webp
    );

    // 동영상 허용 Content-Type 목록
    private static final List<String> ALLOWED_VIDEO_CONTENT_TYPES = List.of(
            "video/mp4",        // mp4
            "video/quicktime",  // mov
            "video/x-matroska", // mkv
            "video/webm"        // webm
    );

    // 음원 허용 Content-Type 목록
    private static final List<String> ALLOWED_AUDIO_CONTENT_TYPES = List.of(
            "audio/mpeg",       // mp3
            "audio/wav",        // wav
            "audio/flac"        // flac
    );

    /// 이미지 파일 크기 검증
    public static boolean isValidImageSize(Long size) {

        return size != null && size > 0 && size <= MAX_IMAGE_FILE_SIZE;
    }
    
    /// 이미지 Content-Type 검증
    public static boolean isValidImageType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }

        return ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType.toLowerCase());
    }

    /// 동영상 파일 크기 검증
    public static boolean isValidVideoSize(Long size) {
        return size != null && size > 0 && size <= MAX_VIDEO_FILE_SIZE;
    }
    
    /// 동영상 Content-Type 검증
    public static boolean isValidVideoType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }

        return ALLOWED_VIDEO_CONTENT_TYPES.contains(contentType.toLowerCase());
    }

    /// 음원 파일 크기 검증
    public static boolean isValidAudioSize(Long size) {
        return size != null && size > 0 && size <= MAX_AUDIO_FILE_SIZE;
    }

    /// 음원 파일 Content-Type 검증
    public static boolean isValidAudioType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }

        return ALLOWED_AUDIO_CONTENT_TYPES.contains(contentType.toLowerCase());
    }

    /// 허용 Content-Length 및 Content-Type 검증
    public static boolean isValidFileSize(String contentType, Long size) {
        if (isValidContentType(contentType)) {
            return isValidImageSize(size);
        }
        if (isValidVideoType(contentType)) {
            return isValidVideoSize(size);
        }
        if (isValidAudioType(contentType)) {
            return isValidAudioSize(size);
        }
        return false;
    }

    /// 허용 Content-Type 검증
    public static boolean isValidContentType(String contentType) {
        return isValidImageType(contentType) || isValidVideoType(contentType) || isValidAudioType(contentType);
    }
}
