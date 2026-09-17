package com.tax.vat.repository;

import com.tax.vat.dto.projection.UserTableProjection;
import com.tax.vat.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Long id);

    @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND u.companyBranch IS NULL ORDER BY u.createdAt ASC, u.id ASC LIMIT 1")
    Optional<User> findMainCompanyUser(@Param("companyId") Long companyId);

    @Query("SELECT u.id as id, u.name as name, u.username as username, u.email as email, " +
            "u.contact as contact, c.name as companyName, cb.name as companyBranchName, " +
            "des.name as designationName, dep.name as departmentName, g.name as groupName, " +
            "u.isOnline as isOnline, u.status as status, u.createdAt as createdAt, u.deletedAt as deletedAt " +
            "FROM User u " +
            "LEFT JOIN u.company c " +
            "LEFT JOIN u.companyBranch cb " +
            "LEFT JOIN u.designation des " +
            "LEFT JOIN u.department dep " +
            "LEFT JOIN u.group g " +
            "WHERE u.deletedAt IS NULL AND (u.isAdmin IS NULL OR u.isAdmin = false) " +
            "AND (:companyId IS NULL OR u.company.id = :companyId)")
    Page<UserTableProjection> findActiveUsers(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT u.id as id, u.name as name, u.username as username, u.email as email, " +
            "u.contact as contact, c.name as companyName, cb.name as companyBranchName, " +
            "des.name as designationName, dep.name as departmentName, g.name as groupName, " +
            "u.isOnline as isOnline, u.status as status, u.createdAt as createdAt, u.deletedAt as deletedAt " +
            "FROM User u " +
            "LEFT JOIN u.company c " +
            "LEFT JOIN u.companyBranch cb " +
            "LEFT JOIN u.designation des " +
            "LEFT JOIN u.department dep " +
            "LEFT JOIN u.group g " +
            "WHERE u.deletedAt IS NULL AND (u.isAdmin IS NULL OR u.isAdmin = false) " +
            "AND (:companyId IS NULL OR u.company.id = :companyId) AND (" +
            " LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UserTableProjection> searchActiveUsers(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT u.id as id, u.name as name, u.username as username, u.email as email, " +
            "u.contact as contact, c.name as companyName, cb.name as companyBranchName, " +
            "des.name as designationName, dep.name as departmentName, g.name as groupName, " +
            "u.isOnline as isOnline, u.status as status, u.createdAt as createdAt, u.deletedAt as deletedAt " +
            "FROM User u " +
            "LEFT JOIN u.company c " +
            "LEFT JOIN u.companyBranch cb " +
            "LEFT JOIN u.designation des " +
            "LEFT JOIN u.department dep " +
            "LEFT JOIN u.group g " +
            "WHERE u.deletedAt IS NOT NULL AND (u.isAdmin IS NULL OR u.isAdmin = false) " +
            "AND (:companyId IS NULL OR u.company.id = :companyId)")
    Page<UserTableProjection> findArchivedUsers(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT u.id as id, u.name as name, u.username as username, u.email as email, " +
            "u.contact as contact, c.name as companyName, cb.name as companyBranchName, " +
            "des.name as designationName, dep.name as departmentName, g.name as groupName, " +
            "u.isOnline as isOnline, u.status as status, u.createdAt as createdAt, u.deletedAt as deletedAt " +
            "FROM User u " +
            "LEFT JOIN u.company c " +
            "LEFT JOIN u.companyBranch cb " +
            "LEFT JOIN u.designation des " +
            "LEFT JOIN u.department dep " +
            "LEFT JOIN u.group g " +
            "WHERE u.deletedAt IS NOT NULL AND (u.isAdmin IS NULL OR u.isAdmin = false) " +
            "AND (:companyId IS NULL OR u.company.id = :companyId) AND (" +
            " LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UserTableProjection> searchArchivedUsers(@Param("companyId") Long companyId, @Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL AND (u.isAdmin = false OR u.isAdmin IS NULL) AND (:companyId IS NULL OR u.company.id = :companyId)")
    long countTotalUsers(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL AND u.isOnline = true AND (u.isAdmin = false OR u.isAdmin IS NULL) AND (:companyId IS NULL OR u.company.id = :companyId)")
    long countOnlineUsers(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL AND u.status = com.tax.vat.enums.UserStatus.ACTIVE AND (u.isAdmin = false OR u.isAdmin IS NULL) AND (:companyId IS NULL OR u.company.id = :companyId)")
    long countActiveUsers(@Param("companyId") Long companyId);
}
