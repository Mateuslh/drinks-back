package com.dev.drinksback.dto;

public record DrinkRequestDTO(String category, Boolean isAlcoholic, Boolean needPhoto, String description) {
}
