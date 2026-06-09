package com.finance.tracker.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByUserIdOrderByDateDescCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Transaction> findByUserIdAndAccountIdOrderByDateDesc(UUID userId, UUID accountId, Pageable pageable);

    Page<Transaction> findByUserIdAndCategoryIdOrderByDateDesc(UUID userId, UUID categoryId, Pageable pageable);

    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(UUID userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.date BETWEEN :start AND :end")
    BigDecimal sumByTypeAndDateBetween(@Param("userId") UUID userId, @Param("type") Transaction.TransactionType type, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId AND t.date BETWEEN :start AND :end")
    BigDecimal sumByCategoryAndDateBetween(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT t.category.id, COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.date BETWEEN :start AND :end GROUP BY t.category.id")
    List<Object[]> sumGroupedByCategory(@Param("userId") UUID userId, @Param("type") Transaction.TransactionType type, @Param("start") LocalDate start, @Param("end") LocalDate end);

    List<Transaction> findByUserIdAndIsRecurringTrue(UUID userId);
}
