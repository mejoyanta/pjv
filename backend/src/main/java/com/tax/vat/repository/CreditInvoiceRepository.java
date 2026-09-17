package com.tax.vat.repository;

import com.tax.vat.entity.CreditInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditInvoiceRepository extends JpaRepository<CreditInvoice, Long>, JpaSpecificationExecutor<CreditInvoice> {

    @Query("SELECT c FROM CreditInvoice c WHERE c.deletedAt IS NULL AND (:companyId IS NULL OR c.companyId = :companyId)")
    Page<CreditInvoice> findCreditInvoices(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT c FROM CreditInvoice c WHERE c.deletedAt IS NULL AND (:companyId IS NULL OR c.companyId = :companyId) AND (LOWER(c.creditInvoiceNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.viNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.paymentStatus) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CreditInvoice> searchCreditInvoices(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM CreditInvoice c WHERE c.deletedAt IS NULL AND (:companyId IS NULL OR c.companyId = :companyId) ORDER BY c.id DESC")
    List<CreditInvoice> findAllByCompany(@Param("companyId") Long companyId);
}
