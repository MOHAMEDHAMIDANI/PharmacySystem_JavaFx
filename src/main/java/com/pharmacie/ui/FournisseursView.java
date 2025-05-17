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
        setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);
        ToolBar toolbar = new ToolBar();
        Button addButton = new Button("Ajouter");
        Button editButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un fournisseur...");
        searchField.setPrefWidth(200);
        toolbar.getItems().addAll(addButton, editButton, deleteButton, new Separator(), searchField);
        fournisseursTable = new TableView<>();
        fournisseurs = FXCollections.observableArrayList();
        VBox.setVgrow(fournisseursTable, Priority.ALWAYS);
        fournisseursTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Fournisseur, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        nomCol.setPrefWidth(200);
        nomCol.setMinWidth(100);
        TableColumn<Fournisseur, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAdresse()));
        adresseCol.setPrefWidth(300);
        adresseCol.setMinWidth(150);
        TableColumn<Fournisseur, String> telCol = new TableColumn<>("Téléphone");
        telCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumeroTelephone()));
        telCol.setPrefWidth(150);
        telCol.setMinWidth(100);
        fournisseursTable.getColumns().addAll(nomCol, adresseCol, telCol);
        fournisseursTable.setItems(fournisseurs);
        addButton.setOnAction(e -> showAddFournisseurDialog());
        editButton.setOnAction(e -> {
            Fournisseur selectedFournisseur = fournisseursTable.getSelectionModel().getSelectedItem();
            if (selectedFournisseur != null) {
                showEditFournisseurDialog(selectedFournisseur);
            } else {
                showError("Aucun fournisseur sélectionné", "Veuillez sélectionner un fournisseur à modifier.");
            }
        });
        deleteButton.setOnAction(e -> {
            Fournisseur selectedFournisseur = fournisseursTable.getSelectionModel().getSelectedItem();
            if (selectedFournisseur != null) {
                showDeleteConfirmation(selectedFournisseur);
            } else {
                showError("Aucun fournisseur sélectionné", "Veuillez sélectionner un fournisseur à supprimer.");
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
        nomField.setPrefWidth(300);
        adresseField.setPrefWidth(300);
        telField.setPrefWidth(200);
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(adresseField, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(telField, 1, 2);
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane);
        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String nom = nomField.getText().trim();
                String adresse = adresseField.getText().trim();
                String tel = telField.getText().trim();
                if (nom.isEmpty() || adresse.isEmpty() || tel.isEmpty()) {
                    showError("Champs manquants", "Veuillez remplir tous les champs.");
                    return null;
                }
                if (!tel.matches("\\d{10}")) {
                    showError("Format invalide", "Le numéro de téléphone doit contenir 10 chiffres.");
                    return null;
                }
                return new Fournisseur(nom, adresse, tel);
            }
            return null;
        });
        dialog.showAndWait().ifPresent(fournisseur -> {
            if (fournisseur != null) {
                fournisseurs.add(fournisseur);
            }
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
        nomField.setPrefWidth(300);
        adresseField.setPrefWidth(300);
        telField.setPrefWidth(200);
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(adresseField, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(telField, 1, 2);
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane);
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String nom = nomField.getText().trim();
                String adresse = adresseField.getText().trim();
                String tel = telField.getText().trim();
                if (nom.isEmpty() || adresse.isEmpty() || tel.isEmpty()) {
                    showError("Champs manquants", "Veuillez remplir tous les champs.");
                    return null;
                }
                if (!tel.matches("\\d{10}")) {
                    showError("Format invalide", "Le numéro de téléphone doit contenir 10 chiffres.");
                    return null;
                }
                fournisseur.setNom(nom);
                fournisseur.setAdresse(adresse);
                fournisseur.setNumeroTelephone(tel);
                return fournisseur;
            }
            return null;
        });
        dialog.showAndWait().ifPresent(updatedFournisseur -> {
            if (updatedFournisseur != null) {
                fournisseursTable.refresh();
            }
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
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void setFournisseurs(ObservableList<Fournisseur> fournisseurs) {
        this.fournisseurs = fournisseurs;
        fournisseursTable.setItems(fournisseurs);
    }
}
