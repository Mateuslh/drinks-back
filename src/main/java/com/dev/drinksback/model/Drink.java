package com.dev.drinksback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("isValid")
    private boolean isValid;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonIgnore
    private Admin admin;


    @JsonProperty("briefDescription")
    private String briefDescription;

    @JsonProperty("drinkName")
    private String drinkName;

    @OneToMany(mappedBy = "drink", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("ingredients")
    private List<Ingredient> ingredients;

    @JsonProperty("preparationMode")
    @OneToMany(mappedBy = "drink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreparationStep> preparationMode;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String imageBase64;
}
