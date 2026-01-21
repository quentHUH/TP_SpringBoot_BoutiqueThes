package com.example.jpa.service;

import com.example.jpa.model.Produit;
import com.example.jpa.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProduitService {
    
    private final ProduitRepository produitRepository;
    
    // Récupérer tous les produits
    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }
    
    // Récupérer tous les produits avec tri
    public List<Produit> getAllProduits(String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        return produitRepository.findAll(sort);
    }
    
    // Récupérer tous les produits avec pagination
    public Page<Produit> getAllProduits(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return produitRepository.findAll(pageable);
    }
    
    // Sauvegarder un produit
    @Transactional
    public Produit saveProduit(Produit produit) {
        return produitRepository.save(produit);
    }
    
    // Trouver un produit par ID
    public Optional<Produit> getProduitById(Long id) {
        return produitRepository.findById(id);
    }
    
    // Supprimer un produit
    @Transactional
    public void deleteProduit(Long id) {
        produitRepository.deleteById(id);
    }
    
    // Rechercher des produits par nom
    public List<Produit> searchByNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return getAllProduits();
        }
        return produitRepository.findByNomContainingIgnoreCase(nom.trim());
    }
    
    // Rechercher avec pagination
    public Page<Produit> searchByNom(String nom, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (nom == null || nom.trim().isEmpty()) {
            return produitRepository.findAll(pageable);
        }
        return produitRepository.findByNomContainingIgnoreCase(nom.trim(), pageable);
    }
    
    // Filtrer par type de thé
    public List<Produit> filterByTypeThe(String typeThe) {
        if (typeThe == null || typeThe.trim().isEmpty() || typeThe.equals("Tous")) {
            return getAllProduits();
        }
        return produitRepository.findByTypeThe(typeThe);
    }
    
    // Filtrer avec pagination
    public Page<Produit> filterByTypeThe(String typeThe, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (typeThe == null || typeThe.trim().isEmpty() || typeThe.equals("Tous")) {
            return produitRepository.findAll(pageable);
        }
        return produitRepository.findByTypeThe(typeThe, pageable);
    }
    
    // Rechercher et filtrer en même temps
    public List<Produit> searchAndFilter(String nom, String typeThe) {
        boolean hasNom = nom != null && !nom.trim().isEmpty();
        boolean hasType = typeThe != null && !typeThe.trim().isEmpty() && !typeThe.equals("Tous");
        
        if (hasNom && hasType) {
            return produitRepository.findByNomContainingIgnoreCaseAndTypeThe(nom.trim(), typeThe);
        } else if (hasNom) {
            return searchByNom(nom);
        } else if (hasType) {
            return filterByTypeThe(typeThe);
        } else {
            return getAllProduits();
        }
    }
    
    // Rechercher et filtrer avec pagination
    public Page<Produit> searchAndFilter(String nom, String typeThe, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        boolean hasNom = nom != null && !nom.trim().isEmpty();
        boolean hasType = typeThe != null && !typeThe.trim().isEmpty() && !typeThe.equals("Tous");
        
        if (hasNom && hasType) {
            return produitRepository.findByNomContainingIgnoreCaseAndTypeThe(nom.trim(), typeThe, pageable);
        } else if (hasNom) {
            return searchByNom(nom, page, size, sortBy, direction);
        } else if (hasType) {
            return filterByTypeThe(typeThe, page, size, sortBy, direction);
        } else {
            return getAllProduits(page, size, sortBy, direction);
        }
    }
    
    // Exporter en CSV
    public byte[] exportToCSV(List<Produit> produits) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        
        // En-tête CSV
        writer.println("ID,Nom,Type,Origine,Prix,Quantité Stock,Description,Date Réception");
        
        // Données
        for (Produit produit : produits) {
            writer.printf("%d,\"%s\",\"%s\",\"%s\",%.2f,%d,\"%s\",%s%n",
                produit.getId(),
                produit.getNom(),
                produit.getTypeThe(),
                produit.getOrigine(),
                produit.getPrix(),
                produit.getQuantiteStock(),
                produit.getDescription() != null ? produit.getDescription().replace("\"", "\"\"") : "",
                produit.getDateReception()
            );
        }
        
        writer.flush();
        writer.close();
        return baos.toByteArray();
    }
}
