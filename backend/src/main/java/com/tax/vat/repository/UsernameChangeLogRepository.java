package com.tax.vat.repository;

import com.tax.vat.entity.UsernameChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsernameChangeLogRepository extends JpaRepository<UsernameChangeLog, Long> {

    List<UsernameChangeLog> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    @Query("SELECT l FROM UsernameChangeLog l WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            " LOWER(l.company.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(l.oldUsername) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(l.newUsername) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(l.reason) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UsernameChangeLog> findAllWithSearch(@Param("search") String search, Pageable pageable);
}
