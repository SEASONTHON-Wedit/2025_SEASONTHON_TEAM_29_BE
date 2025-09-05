package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.api.vendor.dto.search.WeddingHallSearchConditions;
import com.wedit.backend.api.vendor.entity.enums.Category;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/***
 * 동적 쿼리 생성 로직 (JPA Specification)<br>
 * 추후 업체 타입별 엔티티 분리 고려 -> 데이터가 늘어날 시 성능 겁나 구림 (details가 JSON 이라)
 */
public class VendorSpecification {

    public static Specification<Vendor> searchWeddingHalls(WeddingHallSearchConditions conditions) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // --- 기본 조건: 카테고리가 'WEDDING_HALL'인 업체만 조회 ---
            predicates.add(cb.equal(root.get("category"), Category.WEDDING_HALL));

            // --- 공통 조건 (SearchConditions) ---
            if (!CollectionUtils.isEmpty(conditions.getDistricts())) {
                predicates.add(root.get("address").get("district").in(conditions.getDistricts()));
            }

            // --- 웨딩홀 조건 (WeddingHallSearchConditions) ---

            // 최소 가격 (minAmount)
            if (conditions.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("minimumAmount"), conditions.getMaxPrice()));
            }
            // 최대 객수 (maximumGuest)
            if (conditions.getRequiredGuests() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        cb.function("JSON_EXTRACT", Integer.class, root.get("details"), cb.literal("$.maximumGuest")),
                        conditions.getRequiredGuests()
                ));
            }
            // 스타일 (Style)
            if (!CollectionUtils.isEmpty(conditions.getStyles())) {
                predicates.add(cb.function("JSON_EXTRACT", String.class, root.get("details"), cb.literal("$.style"))
                        .in(conditions.getStyles().stream().map(Enum::name).toList()));
            }
            // 식사 타입 (Meal)
            if (!CollectionUtils.isEmpty(conditions.getMeals())) {
                predicates.add(cb.function("JSON_EXTRACT", String.class, root.get("details"), cb.literal("$.meal"))
                        .in(conditions.getMeals().stream().map(Enum::name).toList()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
