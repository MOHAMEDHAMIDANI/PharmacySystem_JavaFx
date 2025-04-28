package com.pharmacie.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ordonnance {
    private LocalDate datePrescription;
    private List<LigneOrdonnance> medicaments;
    private Client client;
    private double prixTotal;
    private CarteAssurance carteAssurance;

    public Ordonnance(LocalDate datePrescription, Client client) {
        this.datePrescription = datePrescription;
        this.client = client;
        this.medicaments = new ArrayList<>();
        this.prixTotal = 0.0;
    }

    public void ajouterMedicament(Medicament medicament, int dosage, int frequence, int dureeTraitement) {
        medicaments.add(new LigneOrdonnance(medicament, dosage, frequence, dureeTraitement));
        calculerPrixTotal();
    }

    public void setCarteAssurance(CarteAssurance carteAssurance) {
        this.carteAssurance = carteAssurance;
        calculerPrixTotal();
    }

    private void calculerPrixTotal() {
        prixTotal = medicaments.stream()
                .mapToDouble(ligne -> ligne.getMedicament().getPrix() * ligne.getQuantiteTotale())
                .sum();

        if (carteAssurance != null) {
            prixTotal = carteAssurance.appliquerReduction(prixTotal);
        }
    }

    public LocalDate getDatePrescription() {
        return datePrescription;
    }

    public List<LigneOrdonnance> getMedicaments() {
        return medicaments;
    }

    public Client getClient() {
        return client;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public CarteAssurance getCarteAssurance() {
        return carteAssurance;
    }
}

class LigneOrdonnance {
    private Medicament medicament;
    private int dosage;
    private int frequence;
    private int dureeTraitement;

    public LigneOrdonnance(Medicament medicament, int dosage, int frequence, int dureeTraitement) {
        this.medicament = medicament;
        this.dosage = dosage;
        this.frequence = frequence;
        this.dureeTraitement = dureeTraitement;
    }

    public int getQuantiteTotale() {
        return dosage * frequence * dureeTraitement;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public int getDosage() {
        return dosage;
    }

    public int getFrequence() {
        return frequence;
    }

    public int getDureeTraitement() {
        return dureeTraitement;
    }
}