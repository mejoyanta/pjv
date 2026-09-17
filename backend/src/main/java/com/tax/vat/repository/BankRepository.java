package com.tax.vat.repository;

import com.tax.vat.entity.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long>, JpaSpecificationExecutor<Bank> {

    @Query("SELECT b FROM Bank b ORDER BY b.name ASC")
    List<Bank> findAllOrdered();

    @Query("SELECT b FROM Bank b WHERE (LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.routingNo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Bank> searchBanks(@Param("search") String search, Pageable pageable);
}
