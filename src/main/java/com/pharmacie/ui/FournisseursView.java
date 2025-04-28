package com.pharmacie.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import com.pharmacie.model.Fournisseur;

public class FournisseursView extends VBox {
    private TableView<Fournisseur> fournisseursTable;
    private ObservableList<Fournisseur> fournisseurs;

    public FournisseursView() {
        setPadding(new Insets(10));
        setSpacing(10);
        initializeComponents();
    }

    private void initializeComponents() {
        ToolBar toolbar = new ToolBar();
        Button addButton = new Button("Ajouter");
        Button editButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un fournisseur...");

        toolbar.getItems().addAll(addButton, editButton, deleteButton, new Separator(), searchField);

        fournisseursTable = new TableView<>();
        fournisseurs = FXCollections.observableArrayList();

        TableColumn<Fournisseur, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));

        TableColumn<Fournisseur, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAdresse()));

        TableColumn<Fournisseur, String> telCol = new TableColumn<>("Téléphone");
        telCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumeroTelephone()));

        fournisseursTable.getColumns().addAll(nomCol, adresseCol, telCol);
        fournisseursTable.setItems(fournisseurs);

        addButton.setOnAction(e -> showAddFournisseurDialog());
        editButton.setOnAction(e -> {
            Fournisseur selectedFournisseur = fournisseursTable.getSelectionModel().getSelectedItem();
            if (selectedFournisseur != null) {
                showEditFournisseurDialog(selectedFournisseur);
            }
        });
        deleteButton.setOnAction(e -> {
            Fournisseur selectedFournisseur = fournisseursTable.getSelectionModel().getSelectedItem();
            if (selectedFournisseur != null) {
                showDeleteConfirmation(selectedFournisseur);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterFournisseurs(newValue);
        });

        getChildren().addAll(toolbar, fournisseursTable);
    }

    private void showAddFournisseurDialog() {
        Dialog<Fournisseur> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un fournisseur");
        dialog.setHeaderText("Entrez les informations du fournisseur");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        TextField adresseField = new TextField();
        TextField telField = new TextField();

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(adresseField, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(telField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Fournisseur(nomField.getText(), adresseField.getText(), telField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(fournisseur -> {
            fournisseurs.add(fournisseur);
        });
    }

    private void showEditFournisseurDialog(Fournisseur fournisseur) {
        Dialog<Fournisseur> dialog = new Dialog<>();
        dialog.setTitle("Modifier un fournisseur");
        dialog.setHeaderText("Modifiez les informations du fournisseur");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField(fournisseur.getNom());
        TextField adresseField = new TextField(fournisseur.getAdresse());
        TextField telField = new TextField(fournisseur.getNumeroTelephone());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(adresseField, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(telField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                fournisseur.setNom(nomField.getText());
                fournisseur.setAdresse(adresseField.getText());
                fournisseur.setNumeroTelephone(telField.getText());
                return fournisseur;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedFournisseur -> {
            fournisseursTable.refresh();
        });
    }

    private void showDeleteConfirmation(Fournisseur fournisseur) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le fournisseur");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + fournisseur.getNom() + " ?");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                fournisseurs.remove(fournisseur);
            }
        });
    }

    private void filterFournisseurs(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            fournisseursTable.setItems(fournisseurs);
        } else {
            ObservableList<Fournisseur> filteredList = FXCollections.observableArrayList();
            for (Fournisseur fournisseur : fournisseurs) {
                if (fournisseur.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                        fournisseur.getAdresse().toLowerCase().contains(searchText.toLowerCase()) ||
                        fournisseur.getNumeroTelephone().contains(searchText)) {
                    filteredList.add(fournisseur);
                }
            }
            fournisseursTable.setItems(filteredList);
        }
    }
}
