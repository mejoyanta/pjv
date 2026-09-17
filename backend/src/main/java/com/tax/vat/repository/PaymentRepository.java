package com.tax.vat.repository;

import com.tax.vat.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    @Query("SELECT p FROM Payment p WHERE (:companyId IS NULL OR p.companyId = :companyId)")
    Page<Payment> findPayments(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE (:companyId IS NULL OR p.companyId = :companyId) AND (LOWER(p.tranId) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.paymentType) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.sendingNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Payment> searchPayments(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE (:companyId IS NULL OR p.companyId = :companyId) ORDER BY p.id DESC")
    List<Payment> findAllByCompany(@Param("companyId") Long companyId);
}
