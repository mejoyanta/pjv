package com.tax.vat.repository;

import com.tax.vat.entity.BankBranch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankBranchRepository extends JpaRepository<BankBranch, Long>, JpaSpecificationExecutor<BankBranch> {

    @Query("SELECT b FROM BankBranch b WHERE (:bankId IS NULL OR b.bankId = :bankId)")
    Page<BankBranch> findBranches(@Param("bankId") Long bankId, Pageable pageable);

    @Query("SELECT b FROM BankBranch b WHERE (:bankId IS NULL OR b.bankId = :bankId) AND (LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.district) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BankBranch> searchBranches(@Param("bankId") Long bankId, @Param("search") String search, Pageable pageable);

    List<BankBranch> findByBankIdOrderByNameAsc(Long bankId);
}
