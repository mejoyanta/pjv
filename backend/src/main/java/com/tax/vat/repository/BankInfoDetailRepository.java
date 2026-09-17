package com.tax.vat.repository;

import com.tax.vat.entity.BankInfoDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankInfoDetailRepository extends JpaRepository<BankInfoDetail, Long>, JpaSpecificationExecutor<BankInfoDetail> {

    @Query("SELECT b FROM BankInfoDetail b WHERE b.deletedAt IS NULL AND (:companyId IS NULL OR b.companyId = :companyId)")
    Page<BankInfoDetail> findBankInfoDetails(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT b FROM BankInfoDetail b WHERE b.deletedAt IS NULL AND (:companyId IS NULL OR b.companyId = :companyId) AND (LOWER(b.accName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.accNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.routingNo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BankInfoDetail> searchBankInfoDetails(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT b FROM BankInfoDetail b WHERE b.deletedAt IS NULL AND (:companyId IS NULL OR b.companyId = :companyId) ORDER BY b.id DESC")
    List<BankInfoDetail> findAllByCompany(@Param("companyId") Long companyId);
}
