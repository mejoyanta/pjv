package com.tax.vat.repository;

import com.tax.vat.entity.PackageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<PackageEntity, Long>, JpaSpecificationExecutor<PackageEntity> {

    @Query("SELECT p FROM PackageEntity p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.packageType) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PackageEntity> searchPackages(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM PackageEntity p ORDER BY p.name ASC")
    List<PackageEntity> findAllOrdered();
}
