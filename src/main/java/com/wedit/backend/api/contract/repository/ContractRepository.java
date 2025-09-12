package com.wedit.backend.api.contract.repository;

import com.wedit.backend.api.contract.entity.Contract;
import com.wedit.backend.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ContractRepository extends JpaRepository<Contract,Long> {

    @Query("SELECT c FROM Contract c " +
            "JOIN FETCH c.product p " +
            "JOIN FETCH p.vendor v " +
            "LEFT JOIN FETCH v.logoMedia " +
            "WHERE c.member = :member " +
            "ORDER BY c.executionDateTime DESC")
    List<Contract> findByMemberWithDetails(@Param("member") Member member);
}
