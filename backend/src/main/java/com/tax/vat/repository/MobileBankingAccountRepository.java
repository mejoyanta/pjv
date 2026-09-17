package com.tax.vat.repository;

import com.tax.vat.entity.MobileBankingAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobileBankingAccountRepository extends JpaRepository<MobileBankingAccount, Long>, JpaSpecificationExecutor<MobileBankingAccount> {

    @Query("SELECT m FROM MobileBankingAccount m WHERE m.deletedAt IS NULL AND (:companyId IS NULL OR m.companyId = :companyId)")
    Page<MobileBankingAccount> findAccounts(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT m FROM MobileBankingAccount m WHERE m.deletedAt IS NULL AND (:companyId IS NULL OR m.companyId = :companyId) AND (LOWER(m.accountName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.accountNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.accountType) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MobileBankingAccount> searchAccounts(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT m FROM MobileBankingAccount m WHERE m.deletedAt IS NULL AND (:companyId IS NULL OR m.companyId = :companyId) ORDER BY m.id DESC")
    List<MobileBankingAccount> findAllByCompany(@Param("companyId") Long companyId);
}
