package com.example.jpa.repository;

import com.example.jpa.model.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    // Recherche par nom (contient le texte recherché)
    List<Produit> findByNomContainingIgnoreCase(String nom);
    
    // Recherche par type de thé
    List<Produit> findByTypeThe(String typeThe);
    
    // Recherche par nom et type de thé
    List<Produit> findByNomContainingIgnoreCaseAndTypeThe(String nom, String typeThe);
    
    // Pagination avec recherche par nom
    Page<Produit> findByNomContainingIgnoreCase(String nom, Pageable pageable);
    
    // Pagination avec filtre par type
    Page<Produit> findByTypeThe(String typeThe, Pageable pageable);
    
    // Pagination avec recherche et filtre
    Page<Produit> findByNomContainingIgnoreCaseAndTypeThe(String nom, String typeThe, Pageable pageable);
}
