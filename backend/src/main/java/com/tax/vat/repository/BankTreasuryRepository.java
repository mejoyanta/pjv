package com.tax.vat.repository;

import com.tax.vat.entity.BankTreasury;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankTreasuryRepository extends JpaRepository<BankTreasury, Long>, JpaSpecificationExecutor<BankTreasury> {

    @Query("SELECT b FROM BankTreasury b WHERE (:companyId IS NULL OR b.companyId = :companyId)")
    Page<BankTreasury> findBankTreasuries(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT b FROM BankTreasury b WHERE (:companyId IS NULL OR b.companyId = :companyId) AND (LOWER(b.challanNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.accountCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BankTreasury> searchBankTreasuries(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT b FROM BankTreasury b WHERE (:companyId IS NULL OR b.companyId = :companyId) ORDER BY b.id DESC")
    List<BankTreasury> findAllByCompany(@Param("companyId") Long companyId);
}
