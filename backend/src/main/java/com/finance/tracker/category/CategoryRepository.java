package com.finance.tracker.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByUserIdAndParentIsNullOrderByNameAsc(UUID userId);

    List<Category> findByUserIdOrderByNameAsc(UUID userId);

    List<Category> findByParentId(UUID parentId);

    boolean existsByUserIdAndNameIgnoreCaseAndParentId(UUID userId, String name, UUID parentId);
}
