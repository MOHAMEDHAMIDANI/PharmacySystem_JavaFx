package com.pharmacie.model;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private String nom;
    private String prenom;
    private String numeroTelephone;
    private List<Ordonnance> historique;

    public Client(String nom, String prenom, String numeroTelephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.numeroTelephone = numeroTelephone;
        this.historique = new ArrayList<>();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public List<Ordonnance> getHistorique() {
        return historique;
    }

    public void ajouterOrdonnance(Ordonnance ordonnance) {
        historique.add(ordonnance);
    }
}
