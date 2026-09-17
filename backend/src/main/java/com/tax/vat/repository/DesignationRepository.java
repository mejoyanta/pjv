package com.tax.vat.repository;

import com.tax.vat.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {
    List<Designation> findByCompanyIdOrCompanyIdIsNull(Long companyId);
}
