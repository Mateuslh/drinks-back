package com.dev.drinksback.repository;

import com.dev.drinksback.model.Drink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long>, PagingAndSortingRepository<Drink, Long> {
    Page<Drink> findByAdminId(Long adminId, Pageable pageable);

    Page<Drink> findByAdminIdAndDrinkNameContaining(Long adminId, String drinkName, Pageable pageable);

    @Query("SELECT d FROM Drink d WHERE d.admin.id = :adminId AND LOWER(d.drinkName) LIKE LOWER(CONCAT('%', :drinkName, '%'))")
    Page<Drink> findByAdminIdAndDrinkNameSimilar(@Param("adminId") Long adminId, @Param("drinkName") String drinkName, Pageable pageable);

    Optional<Drink> findByAdminIdAndId(Long adminId, Long id);
}
