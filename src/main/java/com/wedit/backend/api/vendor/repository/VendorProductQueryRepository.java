package com.wedit.backend.api.vendor.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedit.backend.api.vendor.entity.DressProduct;
import com.wedit.backend.api.vendor.entity.MakeupProduct;
import com.wedit.backend.api.vendor.entity.QDressProduct;
import com.wedit.backend.api.vendor.entity.QMakeupProduct;
import com.wedit.backend.api.vendor.entity.QStudioProduct;
import com.wedit.backend.api.vendor.entity.QVendor;
import com.wedit.backend.api.vendor.entity.QWeddingHallProduct;
import com.wedit.backend.api.vendor.entity.StudioProduct;
import com.wedit.backend.api.vendor.entity.WeddingHallProduct;
import com.wedit.backend.api.vendor.entity.enums.DressOrigin;
import com.wedit.backend.api.vendor.entity.enums.DressStyle;
import com.wedit.backend.api.vendor.entity.enums.HallMeal;
import com.wedit.backend.api.vendor.entity.enums.HallStyle;
import com.wedit.backend.api.vendor.entity.enums.MakeupStyle;
import com.wedit.backend.api.vendor.entity.enums.StudioSpecialShot;
import com.wedit.backend.api.vendor.entity.enums.StudioStyle;

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
			.leftJoin(vendor.region).fetchJoin()
			.leftJoin(vendor.logoMedia).fetchJoin()
			.where(builder)
			.orderBy(weddingHall.basePrice.asc())
			.fetch();
	}

	public List<StudioProduct> searchStudioProducts(
		List<String> regionCodes,
		Integer price,
		List<StudioStyle> studioStyles,
		List<StudioSpecialShot> studioSpecialShots,
		Boolean iphoneSnap
	) {
		QVendor vendor = QVendor.vendor;
		QStudioProduct studioProduct = QStudioProduct.studioProduct;

		BooleanBuilder builder = new BooleanBuilder();

		// 지역 코드 조건 (여러개 선택 가능)
		if (regionCodes != null && !regionCodes.isEmpty()) {
			builder.and(vendor.region.code.in(regionCodes));
		}

		// 가격 조건 (기본가 이하)
		if (price != null) {
			builder.and(studioProduct.basePrice.loe(price.longValue()));
		}

		// 스튜디오 스타일 조건 (여러 개 선택 가능)
		if (studioStyles != null && !studioStyles.isEmpty()) {
			builder.and(studioProduct.studioStyle.in(studioStyles));
		}

		// 스튜디오 특수 촬영 조건 (여러 개 선택 가능)
		if (studioSpecialShots != null && !studioSpecialShots.isEmpty()) {
			builder.and(studioProduct.specialShot.in(studioSpecialShots));
		}

		// 아이폰 스냅 여부
		if (iphoneSnap != null) {
			builder.and(studioProduct.iphoneSnap.eq(iphoneSnap));
		}

		return queryFactory
			.selectFrom(studioProduct)
			.leftJoin(studioProduct.vendor, vendor).fetchJoin()
			.leftJoin(vendor.region).fetchJoin()
			.leftJoin(vendor.logoMedia).fetchJoin()
			.where(builder)
			.orderBy(studioProduct.basePrice.asc())
			.fetch();
	}

	public List<MakeupProduct> searchMakeupProducts(
		List<String> regionCodes,
		Integer price,
		List<MakeupStyle> makeupStyles,
		Boolean isStylistDesignationAvailable,
		Boolean hasPrivateRoom
	) {
		QVendor vendor = QVendor.vendor;
		QMakeupProduct makeupProduct = QMakeupProduct.makeupProduct;

		BooleanBuilder builder = new BooleanBuilder();

		// 지역 코드 조건 (여러개 선택 가능)
		if (regionCodes != null && !regionCodes.isEmpty()) {
			builder.and(vendor.region.code.in(regionCodes));
		}

		// 가격 조건 (기본가 이하)
		if (price != null) {
			builder.and(makeupProduct.basePrice.loe(price.longValue()));
		}

		// 메이크업 스타일 조건
		if (makeupStyles != null && !makeupStyles.isEmpty()) {
			builder.and(makeupProduct.makeupStyle.in(makeupStyles));
		}

		// 담당 지정 조건
		if (isStylistDesignationAvailable != null) {
			builder.and(makeupProduct.isStylistDesignationAvailable.eq(isStylistDesignationAvailable));
		}

		// 단독룸 조건
		if (hasPrivateRoom != null) {
			builder.and(makeupProduct.hasPrivateRoom.eq(hasPrivateRoom));
		}

		return queryFactory
			.selectFrom(makeupProduct)
			.leftJoin(makeupProduct.vendor, vendor).fetchJoin()
			.leftJoin(vendor.region).fetchJoin()
			.leftJoin(vendor.logoMedia).fetchJoin()
			.where(builder)
			.orderBy(makeupProduct.basePrice.asc())
			.fetch();
	}

	public List<DressProduct> searchDressProducts(
		List<String> regionCodes,
		Integer price,
		List<DressStyle> dressStyles,
		List<DressOrigin> dressOrigins
	) {
		QVendor vendor = QVendor.vendor;
		QDressProduct dressProduct = QDressProduct.dressProduct;

		BooleanBuilder builder = new BooleanBuilder();

		// 지역 코드 조건 (여러개 선택 가능)
		if (regionCodes != null && !regionCodes.isEmpty()) {
			builder.and(vendor.region.code.in(regionCodes));
		}

		// 가격 조건 (기본가 이하)
		if (price != null) {
			builder.and(dressProduct.basePrice.loe(price.longValue()));
		}

		// 드레스 스타일 조건 (여러 개 선택 가능)
		if (dressStyles != null && !dressStyles.isEmpty()) {
			builder.and(dressProduct.dressStyle.in(dressStyles));
		}

		// 드레스 제작사 조건 (여러 개 선택 가능)
		if (dressOrigins != null && !dressOrigins.isEmpty()) {
			builder.and(dressProduct.dressOrigin.in(dressOrigins));
		}

		return queryFactory
			.selectFrom(dressProduct)
			.leftJoin(dressProduct.vendor, vendor).fetchJoin()
			.leftJoin(vendor.region).fetchJoin()
			.leftJoin(vendor.logoMedia).fetchJoin()
			.where(builder)
			.orderBy(dressProduct.basePrice.asc())
			.fetch();
	}

}
