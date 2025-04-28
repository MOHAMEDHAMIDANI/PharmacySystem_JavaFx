package com.pharmacie.model;

import java.time.LocalDate;

public class MedicamentInjectable extends Medicament {
    public MedicamentInjectable(String nom, String reference, int quantiteStock, 
                              LocalDate datePeremption, boolean estGenerique, 
                              double prix) {
        super(nom, reference, quantiteStock, datePeremption, estGenerique, prix);
    }

    @Override
    public String getTypeMedicament() {
        return "Injectable";
    }
}