package com.dev.drinksback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String quantity;

    @ManyToOne
    @JoinColumn(name = "drink_id")
    @JsonIgnore
    private Drink drink;
}
