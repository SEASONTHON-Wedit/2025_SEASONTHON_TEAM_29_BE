package com.wedit.backend.api.vendor.repository;

import com.wedit.backend.api.vendor.dto.search.WeddingHallSearchConditions;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VendorRepositoryImpl implements VendorRepositoryCustom {

    private final EntityManager em;

    @Override
    public Page<Vendor> searchWeddingHalls(WeddingHallSearchConditions conditions, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vendor> cq = cb.createQuery(Vendor.class);
        Root<Vendor> vendor = cq.from(Vendor.class);

        Predicate predicate = VendorSpecification.searchWeddingHalls(conditions)
                .toPredicate(vendor, cq, cb);
        cq.where(predicate);

        cq.orderBy(cb.asc(vendor.get("minAmount")));

        TypedQuery<Vendor> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Vendor> content = query.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

        countQuery.select(cb.count(countQuery.from(Vendor.class))).where(predicate);
        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
