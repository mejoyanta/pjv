package com.tax.vat.repository;

import com.tax.vat.entity.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long>, JpaSpecificationExecutor<Designation> {

    @Query("SELECT d FROM Designation d WHERE (:companyId IS NULL OR d.companyId = :companyId)")
    Page<Designation> findDesignations(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT d FROM Designation d WHERE (:companyId IS NULL OR d.companyId = :companyId) AND LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Designation> searchDesignations(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT d FROM Designation d WHERE (:companyId IS NULL OR d.companyId = :companyId) ORDER BY d.name ASC")
    List<Designation> findAllByCompany(@Param("companyId") Long companyId);

    List<Designation> findByCompanyIdOrCompanyIdIsNull(Long companyId);
}
