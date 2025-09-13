package com.wedit.backend.api.calendar.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventCategory {
    INVITATION("청첩장"),          // 편지 아이콘
    STUDIO("스튜디오"),             // 카메라 아이콘
    WEDDING_HALL("웨딩홀"),        // 건물 아이콘
    DRESS("드레스"),               // 드레스 아이콘
    PARTY("파티/기념일"),           // 샴페인 잔 아이콘
    BRIDAL_SHOWER("브라이덜샤워"),  // 케이크 아이콘
    MAKEUP("메이크업"),             // 립스틱 아이콘
    ETC("기타");                  // 기타

    private final String description;
}
