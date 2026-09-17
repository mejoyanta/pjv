package com.tax.vat.repository;

import com.tax.vat.entity.MobileBankingTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobileBankingTransactionRepository extends JpaRepository<MobileBankingTransaction, Long>, JpaSpecificationExecutor<MobileBankingTransaction> {

    @Query("SELECT m FROM MobileBankingTransaction m WHERE m.deletedAt IS NULL AND (:companyId IS NULL OR m.companyId = :companyId)")
    Page<MobileBankingTransaction> findTransactions(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT m FROM MobileBankingTransaction m WHERE m.deletedAt IS NULL AND (:companyId IS NULL OR m.companyId = :companyId) AND (LOWER(m.transactionId) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.purposeOf) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MobileBankingTransaction> searchTransactions(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT m FROM MobileBankingTransaction m WHERE m.deletedAt IS NULL AND (:companyId IS NULL OR m.companyId = :companyId) ORDER BY m.id DESC")
    List<MobileBankingTransaction> findAllByCompany(@Param("companyId") Long companyId);
}
