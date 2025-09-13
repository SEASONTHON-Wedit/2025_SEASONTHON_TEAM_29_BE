package com.wedit.backend.api.vendor.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedit.backend.api.vendor.entity.QRegion;
import com.wedit.backend.api.vendor.entity.QVendor;
import com.wedit.backend.api.vendor.entity.QWeddingHallProduct;
import com.wedit.backend.api.vendor.entity.WeddingHallProduct;
import com.wedit.backend.api.vendor.entity.enums.HallMeal;
import com.wedit.backend.api.vendor.entity.enums.HallStyle;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VendorProductQueryRepository {
	private final JPAQueryFactory queryFactory;

	public List<WeddingHallProduct> searchWeddingHallProducts(
		List<String> regionCodes,
		Integer price,
		List<HallStyle> hallStyles,
		List<HallMeal> hallMeals,
		Integer capacity,
		Boolean hasParking) {

		QVendor vendor = QVendor.vendor;
		QWeddingHallProduct weddingHall = QWeddingHallProduct.weddingHallProduct;
		QRegion region = QRegion.region;

		BooleanBuilder builder = new BooleanBuilder();

		// 지역 코드 조건 (여러 개 선택 가능)
		if (regionCodes != null && !regionCodes.isEmpty()) {
			builder.and(vendor.region.code.in(regionCodes));
		}

		// 가격 조건 (기본가 이하)
		if (price != null) {
			builder.and(weddingHall.basePrice.loe(price.longValue()));
		}

		// 홀 스타일 조건 (여러 개 선택 가능)
		if (hallStyles != null && !hallStyles.isEmpty()) {
			builder.and(weddingHall.hallStyle.in(hallStyles));
		}

		// 식사 타입 조건 (여러 개 선택 가능)
		if (hallMeals != null && !hallMeals.isEmpty()) {
			builder.and(weddingHall.hallMeal.in(hallMeals));
		}

		// 하객수 조건 (최소 하객수)
		if (capacity != null) {
			builder.and(weddingHall.capacity.goe(capacity));
		}

		// 주차장 여부
		if (hasParking != null) {
			builder.and(weddingHall.hasParking.eq(hasParking));
		}

		return queryFactory
			.selectFrom(weddingHall)
			.leftJoin(weddingHall.vendor, vendor).fetchJoin()
			.leftJoin(vendor.region, region).fetchJoin()
			.leftJoin(vendor.logoMedia).fetchJoin()
			.where(builder)
			.orderBy(weddingHall.basePrice.asc())
			.fetch();
	}
}
