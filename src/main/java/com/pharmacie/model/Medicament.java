package com.pharmacie.model;

import java.time.LocalDate;

public abstract class Medicament {
    private String nom;
    private String reference;
    private int quantiteStock;
    private LocalDate datePeremption;
    private boolean estGenerique;
    private double prix;

    public Medicament(String nom, String reference, int quantiteStock, LocalDate datePeremption, boolean estGenerique,
            double prix) {
        this.nom = nom;
        this.reference = reference;
        this.quantiteStock = quantiteStock;
        this.datePeremption = datePeremption;
        this.estGenerique = estGenerique;
        this.prix = prix;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public LocalDate getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(LocalDate datePeremption) {
        this.datePeremption = datePeremption;
    }

    public boolean isEstGenerique() {
        return estGenerique;
    }

    public void setEstGenerique(boolean estGenerique) {
        this.estGenerique = estGenerique;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public boolean estProcheDeLaPeremption() {
        return LocalDate.now().plusMonths(3).isAfter(datePeremption);
    }

    public abstract String getTypeMedicament();
}