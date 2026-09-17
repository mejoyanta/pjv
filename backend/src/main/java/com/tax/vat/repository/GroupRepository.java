package com.tax.vat.repository;

import com.tax.vat.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    List<Group> findByDeletedAtIsNullOrderByNameAsc();
    List<Group> findByDeletedAtIsNullAndIsAdminFalseOrderByNameAsc();

    @Query("SELECT g FROM Group g WHERE g.deletedAt IS NULL AND " +
            "(:search IS NULL OR :search = '' OR LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Group> findActiveGroups(@Param("search") String search, Pageable pageable);

    @Query("SELECT g FROM Group g WHERE g.deletedAt IS NOT NULL AND " +
            "(:search IS NULL OR :search = '' OR LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Group> findArchivedGroups(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.group.id = :groupId AND u.deletedAt IS NULL")
    long countActiveUsers(@Param("groupId") Long groupId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.group.id = :groupId")
    long countAssignedUsers(@Param("groupId") Long groupId);

    @Query("SELECT COUNT(g) FROM Group g WHERE g.parent.id = :groupId AND g.deletedAt IS NULL")
    long countActiveChildren(@Param("groupId") Long groupId);

    @Query("SELECT COUNT(g) FROM Group g WHERE g.parent.id = :groupId")
    long countAssignedChildren(@Param("groupId") Long groupId);

    @Query("SELECT COUNT(g) FROM Group g WHERE g.deletedAt IS NULL")
    long countActive();

    @Query("SELECT COUNT(g) FROM Group g WHERE g.deletedAt IS NOT NULL")
    long countArchived();
}
