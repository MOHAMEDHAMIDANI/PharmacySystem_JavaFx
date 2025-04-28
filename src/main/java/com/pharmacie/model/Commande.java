package com.pharmacie.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    private Fournisseur fournisseur;
    private LocalDate dateCommande;
    private List<LigneCommande> lignesCommande;
    private String status;

    public Commande(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
        this.dateCommande = LocalDate.now();
        this.lignesCommande = new ArrayList<>();
        this.status = "En attente";
    }

    public void ajouterMedicament(Medicament medicament, int quantite) {
        lignesCommande.add(new LigneCommande(medicament, quantite));
    }
    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public List<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}