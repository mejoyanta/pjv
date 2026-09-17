package com.tax.vat.repository;

import com.tax.vat.entity.Port;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortRepository extends JpaRepository<Port, Long>, JpaSpecificationExecutor<Port> {
    Optional<Port> findBySlug(String slug);

    @Query("SELECT p FROM Port p WHERE p.deletedAt IS NULL")
    Page<Port> findActivePorts(Pageable pageable);

    @Query("SELECT p FROM Port p WHERE p.deletedAt IS NULL AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Port> searchActivePorts(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Port p WHERE p.deletedAt IS NULL ORDER BY p.id DESC")
    List<Port> findAllActivePorts();
}
