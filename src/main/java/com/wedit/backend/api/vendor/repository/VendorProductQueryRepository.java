package com.wedit.backend.api.vendor.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wedit.backend.api.vendor.entity.QDressProduct;
import com.wedit.backend.api.vendor.entity.QMakeupProduct;
import com.wedit.backend.api.vendor.entity.QStudioProduct;
import com.wedit.backend.api.vendor.entity.QVendor;
import com.wedit.backend.api.vendor.entity.QWeddingHallProduct;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.enums.DressOrigin;
import com.wedit.backend.api.vendor.entity.enums.DressStyle;
import com.wedit.backend.api.vendor.entity.enums.HallMeal;
import com.wedit.backend.api.vendor.entity.enums.HallStyle;
import com.wedit.backend.api.vendor.entity.enums.MakeupStyle;
import com.wedit.backend.api.vendor.entity.enums.StudioSpecialShot;
import com.wedit.backend.api.vendor.entity.enums.StudioStyle;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VendorProductQueryRepository {
	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;

	// 최저가와 함께 업체 정보를 담을 DTO 클래스
	public static class VendorWithMinPrice {
		public final Vendor vendor;
		public final Long minPrice;

		public VendorWithMinPrice(Vendor vendor, Long minPrice) {
			this.vendor = vendor;
			this.minPrice = minPrice;
		}
	}

	public List<VendorWithMinPrice> searchWeddingHallVendors(
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

		// 가격 조건 (기본가 이하) - null이거나 최대값(1000만원)인 경우 조건 제외
		if (price != null && price != 10000000) {
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
			.select(Projections.constructor(VendorWithMinPrice.class,
				vendor,
				weddingHall.basePrice.min()
			))
			.from(weddingHall)
			.join(weddingHall.vendor, vendor)
			.join(vendor.region)
			.leftJoin(vendor.logoMedia)
			.where(builder)
			.groupBy(vendor.id)
			.orderBy(weddingHall.basePrice.min().asc())
			.fetch();
	}

	/**
	 * 순수 JPA를 사용한 웨딩홀 검색 (성능 비교용)
	 */
	public List<VendorWithMinPrice> searchWeddingHallVendorsWithJPA(
		List<String> regionCodes,
		Integer price,
		List<HallStyle> hallStyles,
		List<HallMeal> hallMeals,
		Integer capacity,
		Boolean hasParking) {

		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT v, MIN(wh.basePrice) ")
			.append("FROM WeddingHallProduct wh ")
			.append("JOIN wh.vendor v ")
			.append("JOIN v.region r ")
			.append("LEFT JOIN v.logoMedia ")
			.append("WHERE 1=1 ");

		// 동적 조건 추가
		if (regionCodes != null && !regionCodes.isEmpty()) {
			jpql.append("AND r.code IN :regionCodes ");
		}

		if (price != null && price != 10000000) {
			jpql.append("AND wh.basePrice <= :price ");
		}

		if (hallStyles != null && !hallStyles.isEmpty()) {
			jpql.append("AND wh.hallStyle IN :hallStyles ");
		}

		if (hallMeals != null && !hallMeals.isEmpty()) {
			jpql.append("AND wh.hallMeal IN :hallMeals ");
		}

		if (capacity != null) {
			jpql.append("AND wh.capacity >= :capacity ");
		}

		if (hasParking != null) {
			jpql.append("AND wh.hasParking = :hasParking ");
		}

		jpql.append("GROUP BY v.id ")
			.append("ORDER BY MIN(wh.basePrice) ASC");

		TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);

		// 파라미터 바인딩
		if (regionCodes != null && !regionCodes.isEmpty()) {
			query.setParameter("regionCodes", regionCodes);
		}

		if (price != null && price != 10000000) {
			query.setParameter("price", price.longValue());
		}

		if (hallStyles != null && !hallStyles.isEmpty()) {
			query.setParameter("hallStyles", hallStyles);
		}

		if (hallMeals != null && !hallMeals.isEmpty()) {
			query.setParameter("hallMeals", hallMeals);
		}

		if (capacity != null) {
			query.setParameter("capacity", capacity);
		}

		if (hasParking != null) {
			query.setParameter("hasParking", hasParking);
		}

		List<Object[]> results = query.getResultList();

		// Object[] 결과를 VendorWithMinPrice로 변환
		return results.stream()
			.map(result -> new VendorWithMinPrice(
				(Vendor)result[0],
				(Long)result[1]
			))
			.toList();
	}

	public List<VendorWithMinPrice> searchStudioVendors(
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

		// 가격 조건 (기본가 이하) - null인 경우 조건 제외
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
			.select(Projections.constructor(VendorWithMinPrice.class,
				vendor,
				studioProduct.basePrice.min()
			))
			.from(studioProduct)
			.join(studioProduct.vendor, vendor)
			.join(vendor.region)
			.leftJoin(vendor.logoMedia)
			.where(builder)
			.groupBy(vendor.id)
			.orderBy(studioProduct.basePrice.min().asc())
			.fetch();
	}

	public List<VendorWithMinPrice> searchMakeupVendors(
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

		// 가격 조건 (기본가 이하) - null인 경우 조건 제외
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
			.select(Projections.constructor(VendorWithMinPrice.class,
				vendor,
				makeupProduct.basePrice.min()
			))
			.from(makeupProduct)
			.join(makeupProduct.vendor, vendor)
			.join(vendor.region)
			.leftJoin(vendor.logoMedia)
			.where(builder)
			.groupBy(vendor.id)
			.orderBy(makeupProduct.basePrice.min().asc())
			.fetch();
	}

	public List<VendorWithMinPrice> searchDressVendors(
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

		// 가격 조건 (기본가 이하) - null인 경우 조건 제외
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
			.select(Projections.constructor(VendorWithMinPrice.class,
				vendor,
				dressProduct.basePrice.min()
			))
			.from(dressProduct)
			.join(dressProduct.vendor, vendor)
			.join(vendor.region)
			.leftJoin(vendor.logoMedia)
			.where(builder)
			.groupBy(vendor.id)
			.orderBy(dressProduct.basePrice.min().asc())
			.fetch();
	}
}
