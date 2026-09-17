package com.tax.vat.repository;

import com.tax.vat.entity.CompanyBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyBranchRepository extends JpaRepository<CompanyBranch, Long> {
    List<CompanyBranch> findByCompanyIdAndDeletedAtIsNull(Long companyId);
    Optional<CompanyBranch> findByCompanyIdAndIsMainTrue(Long companyId);
    Optional<CompanyBranch> findBySlug(String slug);
    Optional<CompanyBranch> findByCompanyIdAndUsername(Long companyId, String username);
}
