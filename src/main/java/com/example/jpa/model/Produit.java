package com.example.jpa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "produits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;
    
    @NotBlank(message = "Le type de thé est obligatoire")
    @Column(nullable = false)
    private String typeThe;
    
    @NotBlank(message = "L'origine est obligatoire")
    @Column(nullable = false)
    private String origine;
    
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "5.0", message = "Le prix doit être au minimum 5€")
    @DecimalMax(value = "100.0", message = "Le prix ne peut pas dépasser 100€")
    @Column(nullable = false)
    private Float prix;
    
    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    @Column(nullable = false)
    private Integer quantiteStock;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(length = 500)
    private String description;
    
    @NotNull(message = "La date de réception est obligatoire")
    @Column(nullable = false)
    private LocalDate dateReception;
}
