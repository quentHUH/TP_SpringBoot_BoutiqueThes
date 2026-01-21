package com.example.jpa.controller;

import com.example.jpa.model.Produit;
import com.example.jpa.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProduitController {
    
    private final ProduitService produitService;
    
    private static final String[] TYPES_THE = {"Vert", "Noir", "Oolong", "Blanc", "Pu-erh"};
    private static final String[] ORIGINES = {"Chine", "Japon", "Inde", "Sri Lanka", "Taiwan"};
    
    // Afficher la liste des produits
    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) String typeThe,
            @RequestParam(required = false, defaultValue = "nom") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model) {
        
        Page<Produit> produitsPage;
        
        // Recherche et filtre avec pagination
        if ((recherche != null && !recherche.trim().isEmpty()) || 
            (typeThe != null && !typeThe.trim().isEmpty() && !typeThe.equals("Tous"))) {
            produitsPage = produitService.searchAndFilter(recherche, typeThe, page, size, sortBy, direction);
        } else {
            produitsPage = produitService.getAllProduits(page, size, sortBy, direction);
        }
        
        model.addAttribute("produits", produitsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", produitsPage.getTotalPages());
        model.addAttribute("totalItems", produitsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("typesThe", TYPES_THE);
        model.addAttribute("recherche", recherche);
        model.addAttribute("typeTheFiltre", typeThe);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        
        return "index";
    }
    
    // Afficher le formulaire d'ajout
    @GetMapping("/nouveau")
    public String nouveauProduit(Model model) {
        model.addAttribute("produit", new Produit());
        model.addAttribute("typesThe", TYPES_THE);
        model.addAttribute("origines", ORIGINES);
        model.addAttribute("titre", "Ajouter un nouveau thé");
        return "formulaire-produit";
    }
    
    // Enregistrer un nouveau produit
    @PostMapping("/enregistrer")
    public String enregistrerProduit(
            @Valid @ModelAttribute("produit") Produit produit,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("typesThe", TYPES_THE);
            model.addAttribute("origines", ORIGINES);
            model.addAttribute("titre", "Ajouter un nouveau thé");
            return "formulaire-produit";
        }
        
        produitService.saveProduit(produit);
        redirectAttributes.addFlashAttribute("message", "Produit ajouté avec succès !");
        return "redirect:/";
    }
    
    // Afficher le formulaire de modification
    @GetMapping("/modifier/{id}")
    public String modifierProduit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return produitService.getProduitById(id)
                .map(produit -> {
                    model.addAttribute("produit", produit);
                    model.addAttribute("typesThe", TYPES_THE);
                    model.addAttribute("origines", ORIGINES);
                    model.addAttribute("titre", "Modifier le thé");
                    return "formulaire-produit";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Produit introuvable !");
                    return "redirect:/";
                });
    }
    
    // Mettre à jour un produit
    @PostMapping("/modifier/{id}")
    public String mettreAJourProduit(
            @PathVariable Long id,
            @Valid @ModelAttribute("produit") Produit produit,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("typesThe", TYPES_THE);
            model.addAttribute("origines", ORIGINES);
            model.addAttribute("titre", "Modifier le thé");
            return "formulaire-produit";
        }
        
        produit.setId(id);
        produitService.saveProduit(produit);
        redirectAttributes.addFlashAttribute("message", "Produit modifié avec succès !");
        return "redirect:/";
    }
    
    // Supprimer un produit
    @GetMapping("/supprimer/{id}")
    public String supprimerProduit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.deleteProduit(id);
            redirectAttributes.addFlashAttribute("message", "Produit supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du produit !");
        }
        return "redirect:/";
    }
    
    // Exporter en CSV
    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCSV(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) String typeThe) {
        
        List<Produit> produits = produitService.searchAndFilter(recherche, typeThe);
        byte[] csvData = produitService.exportToCSV(produits);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "produits.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
}
