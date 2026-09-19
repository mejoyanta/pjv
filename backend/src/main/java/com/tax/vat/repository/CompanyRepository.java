package com.tax.vat.repository;

import com.tax.vat.dto.projection.CompanyTableProjection;
import com.tax.vat.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    Optional<Company> findBySlug(String slug);
    Optional<Company> findByUsername(String username);

    boolean existsByBin(String bin);
    boolean existsByBinAndIdNot(String bin, Long id);

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Long id);

    /**
     * Active companies without search filter (fast, indexed).
     */
    @Query("SELECT c.id as id, c.name as name, c.slug as slug, c.username as username, " +
            "c.email as email, c.phone as phone, c.bin as bin, cat.name as categoryName, " +
            "c.status as status, c.subscriptionExpireDate as subscriptionExpireDate, " +
            "c.createdAt as createdAt, c.deletedAt as deletedAt " +
            "FROM Company c LEFT JOIN c.category cat " +
            "WHERE c.deletedAt IS NULL")
    Page<CompanyTableProjection> findActiveCompanies(Pageable pageable);

    /**
     * Active companies with search filter.
     */
    @Query("SELECT c.id as id, c.name as name, c.slug as slug, c.username as username, " +
            "c.email as email, c.phone as phone, c.bin as bin, cat.name as categoryName, " +
            "c.status as status, c.subscriptionExpireDate as subscriptionExpireDate, " +
            "c.createdAt as createdAt, c.deletedAt as deletedAt " +
            "FROM Company c LEFT JOIN c.category cat " +
            "WHERE c.deletedAt IS NULL AND (" +
            " LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.bin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(cat.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CompanyTableProjection> searchActiveCompanies(@Param("search") String search, Pageable pageable);

    /**
     * Archived companies without search filter.
     */
    @Query("SELECT c.id as id, c.name as name, c.slug as slug, c.username as username, " +
            "c.email as email, c.phone as phone, c.bin as bin, cat.name as categoryName, " +
            "c.status as status, c.subscriptionExpireDate as subscriptionExpireDate, " +
            "c.createdAt as createdAt, c.deletedAt as deletedAt " +
            "FROM Company c LEFT JOIN c.category cat " +
            "WHERE c.deletedAt IS NOT NULL")
    Page<CompanyTableProjection> findArchivedCompanies(Pageable pageable);

    /**
     * Archived companies with search filter.
     */
    @Query("SELECT c.id as id, c.name as name, c.slug as slug, c.username as username, " +
            "c.email as email, c.phone as phone, c.bin as bin, cat.name as categoryName, " +
            "c.status as status, c.subscriptionExpireDate as subscriptionExpireDate, " +
            "c.createdAt as createdAt, c.deletedAt as deletedAt " +
            "FROM Company c LEFT JOIN c.category cat " +
            "WHERE c.deletedAt IS NOT NULL AND (" +
            " LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.bin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(cat.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CompanyTableProjection> searchArchivedCompanies(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Company c WHERE c.deletedAt IS NULL ORDER BY c.name ASC")
    java.util.List<Company> findAllActiveCompanies();

    @Query("SELECT COUNT(c) FROM Company c WHERE c.deletedAt IS NULL")
    long countActive();

    @Query("SELECT COUNT(c) FROM Company c WHERE c.deletedAt IS NOT NULL")
    long countArchived();
}
