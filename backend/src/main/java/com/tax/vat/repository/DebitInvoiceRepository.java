package com.tax.vat.repository;

import com.tax.vat.entity.DebitInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebitInvoiceRepository extends JpaRepository<DebitInvoice, Long>, JpaSpecificationExecutor<DebitInvoice> {

    @Query("SELECT d FROM DebitInvoice d WHERE d.deletedAt IS NULL AND (:companyId IS NULL OR d.companyId = :companyId)")
    Page<DebitInvoice> findDebitInvoices(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT d FROM DebitInvoice d WHERE d.deletedAt IS NULL AND (:companyId IS NULL OR d.companyId = :companyId) AND (LOWER(d.debitInvoiceNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.seller) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.billOfEntry) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.paymentStatus) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<DebitInvoice> searchDebitInvoices(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT d FROM DebitInvoice d WHERE d.deletedAt IS NULL AND (:companyId IS NULL OR d.companyId = :companyId) ORDER BY d.id DESC")
    List<DebitInvoice> findAllByCompany(@Param("companyId") Long companyId);
}
