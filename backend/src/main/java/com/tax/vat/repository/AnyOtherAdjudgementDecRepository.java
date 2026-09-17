package com.tax.vat.repository;

import com.tax.vat.entity.AnyOtherAdjudgementDec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnyOtherAdjudgementDecRepository extends JpaRepository<AnyOtherAdjudgementDec, Long>, JpaSpecificationExecutor<AnyOtherAdjudgementDec> {

    @Query("SELECT a FROM AnyOtherAdjudgementDec a WHERE a.deletedAt IS NULL AND (:companyId IS NULL OR a.companyId = :companyId)")
    Page<AnyOtherAdjudgementDec> findAdjustments(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT a FROM AnyOtherAdjudgementDec a WHERE a.deletedAt IS NULL AND (:companyId IS NULL OR a.companyId = :companyId) AND (LOWER(a.challanNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.note) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<AnyOtherAdjudgementDec> searchAdjustments(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT a FROM AnyOtherAdjudgementDec a WHERE a.deletedAt IS NULL AND (:companyId IS NULL OR a.companyId = :companyId) ORDER BY a.id DESC")
    List<AnyOtherAdjudgementDec> findAllByCompany(@Param("companyId") Long companyId);
}
