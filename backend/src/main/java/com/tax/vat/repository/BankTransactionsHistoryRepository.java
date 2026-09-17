package com.tax.vat.repository;

import com.tax.vat.entity.BankTransactionsHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankTransactionsHistoryRepository extends JpaRepository<BankTransactionsHistory, Long>, JpaSpecificationExecutor<BankTransactionsHistory> {

    @Query("SELECT b FROM BankTransactionsHistory b WHERE b.deletedAt IS NULL AND (:companyId IS NULL OR b.companyId = :companyId)")
    Page<BankTransactionsHistory> findBankTransactions(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT b FROM BankTransactionsHistory b WHERE b.deletedAt IS NULL AND (:companyId IS NULL OR b.companyId = :companyId) AND (LOWER(b.purposeOf) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BankTransactionsHistory> searchBankTransactions(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT b FROM BankTransactionsHistory b WHERE b.deletedAt IS NULL AND (:companyId IS NULL OR b.companyId = :companyId) ORDER BY b.id DESC")
    List<BankTransactionsHistory> findAllByCompany(@Param("companyId") Long companyId);
}
