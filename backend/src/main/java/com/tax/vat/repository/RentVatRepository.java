package com.tax.vat.repository;

import com.tax.vat.entity.RentVat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentVatRepository extends JpaRepository<RentVat, Long>, JpaSpecificationExecutor<RentVat> {

    @Query("SELECT r FROM RentVat r WHERE r.deletedAt IS NULL AND (:companyId IS NULL OR r.companyId = :companyId)")
    Page<RentVat> findRentVats(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT r FROM RentVat r WHERE r.deletedAt IS NULL AND (:companyId IS NULL OR r.companyId = :companyId) AND (LOWER(r.challanNo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.note) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<RentVat> searchRentVats(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT r FROM RentVat r WHERE r.deletedAt IS NULL AND (:companyId IS NULL OR r.companyId = :companyId) ORDER BY r.id DESC")
    List<RentVat> findAllByCompany(@Param("companyId") Long companyId);
}
