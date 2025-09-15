package com.wedit.backend.api.contract.repository;

import com.wedit.backend.api.contract.entity.Contract;
import com.wedit.backend.api.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ContractRepository extends JpaRepository<Contract,Long> {

    // 마이페이지 계약건 탭 - 전체 계약을 이행일 오름차순으로 조회
    @Query(value = "SELECT c FROM Contract c " +
            "JOIN FETCH c.product p " +
            "JOIN FETCH p.vendor v " +
            "LEFT JOIN FETCH v.logoMedia " +
            "LEFT JOIN FETCH v.region " +
            "WHERE c.member = :member " +
            "ORDER BY c.executionDateTime ASC", // 다가오는 순 정렬
            countQuery = "SELECT count(c) FROM Contract c WHERE c.member = :member")
    Page<Contract> findAllContractsByMember(@Param("member") Member member, Pageable pageable);

    // 후기 작성하러 가기 페이지  - 지난 계약만 조회
    @Query(value = "SELECT c FROM Contract c " +
            "JOIN FETCH c.product p " +
            "JOIN FETCH p.vendor v " +
            "LEFT JOIN FETCH v.logoMedia " +
            "WHERE c.member = :member AND c.executionDateTime < CURRENT_TIMESTAMP " +
            "ORDER BY c.executionDateTime DESC",
            countQuery = "SELECT count(c) FROM Contract c WHERE c.member = :member AND c.executionDateTime < CURRENT_TIMESTAMP")
    Page<Contract> findPastContractsByMember(@Param("member") Member member, Pageable pageable);


    // 계약 단건 상세 조회를 위한 쿼리
    @Query("SELECT c FROM Contract c " +
            "JOIN FETCH c.product p " +
            "JOIN FETCH p.vendor v " +
            "LEFT JOIN FETCH v.repMedia " +
            "WHERE c.id = :contractId AND c.member.id = :memberId")
    Optional<Contract> findContractDetailsByIdAndMemberId(@Param("contractId") Long contractId, @Param("memberId") Long memberId);
}
