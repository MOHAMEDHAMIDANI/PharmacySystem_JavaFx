package com.pharmacie.model;

public class CarteAssurance {
    private String numeroSecuriteSociale;
    private boolean estActive;
    private double tauxReduction;

    public CarteAssurance(String numeroSecuriteSociale, boolean estActive, double tauxReduction) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
        this.estActive = estActive;
        this.tauxReduction = tauxReduction;
    }

    public String getNumeroSecuriteSociale() {
        return numeroSecuriteSociale;
    }

    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
    }

    public boolean isEstActive() {
        return estActive;
    }

    public void setEstActive(boolean estActive) {
        this.estActive = estActive;
    }

    public double getTauxReduction() {
        return tauxReduction;
    }

    public void setTauxReduction(double tauxReduction) {
        this.tauxReduction = tauxReduction;
    }

    public double appliquerReduction(double montant) {
        if (estActive) {
            return montant * (1 - tauxReduction);
        }
        return montant;
    }
}