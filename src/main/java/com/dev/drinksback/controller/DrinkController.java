package com.dev.drinksback.controller;

import com.dev.drinksback.dto.DrinkRequestDTO;
import com.dev.drinksback.model.Admin;
import com.dev.drinksback.model.Drink;
import com.dev.drinksback.repository.DrinkRepository;
import com.dev.drinksback.service.AdminService;
import com.dev.drinksback.service.DrinkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drink")
@Tag(name = "Drink")
public class DrinkController {

    @Autowired
    DrinkService drinkService;
    @Autowired
    AdminService adminService;
    @Autowired
    private DrinkRepository drinkRepository;

    @PostMapping
    @Transactional
    public Drink receita(@RequestBody DrinkRequestDTO body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminService.findByUsername(username);
        return drinkService.generateDrink(body.category(), body.isAlcoholic(), body.needPhoto(), body.description(), admin);
    }

    @GetMapping
    @Transactional
    public ResponseEntity<Page<Drink>> getDrinks(
            @RequestParam(required = false) String drinkName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, Pageable pageable) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminService.findByUsername(username);

        Page<Drink> drinks = drinkService.findByAdminIdAndDrinkNameSimilar(admin, drinkName != null ? drinkName : "", pageable);
        return ResponseEntity.ok(drinks);
    }
}
