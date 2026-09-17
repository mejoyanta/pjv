package com.tax.vat.repository;

import com.tax.vat.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    @Query("SELECT c FROM Customer c WHERE (:companyId IS NULL OR c.companyId = :companyId)")
    Page<Customer> findCustomers(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE (:companyId IS NULL OR c.companyId = :companyId) AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.binTin) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Customer> searchCustomers(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE (:companyId IS NULL OR c.companyId = :companyId) ORDER BY c.id DESC")
    List<Customer> findAllByCompany(@Param("companyId") Long companyId);
}
