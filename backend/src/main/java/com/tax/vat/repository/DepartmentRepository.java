package com.tax.vat.repository;

import com.tax.vat.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

    @Query("SELECT d FROM Department d WHERE (:companyId IS NULL OR d.companyId = :companyId)")
    Page<Department> findDepartments(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT d FROM Department d WHERE (:companyId IS NULL OR d.companyId = :companyId) AND LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Department> searchDepartments(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT d FROM Department d WHERE (:companyId IS NULL OR d.companyId = :companyId) ORDER BY d.name ASC")
    List<Department> findAllByCompany(@Param("companyId") Long companyId);

    List<Department> findByCompanyIdOrCompanyIdIsNull(Long companyId);
}
