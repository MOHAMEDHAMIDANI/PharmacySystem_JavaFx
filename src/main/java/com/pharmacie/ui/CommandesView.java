package com.pharmacie.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import com.pharmacie.model.*;
import java.time.LocalDate;

public class CommandesView extends VBox {
    private TableView<Commande> commandesTable;
    private ObservableList<Commande> commandes;
    private ObservableList<Fournisseur> fournisseurs;
    private ObservableList<Medicament> medicaments;

    public CommandesView() {
        setPadding(new Insets(10));
        setSpacing(10);
        commandes = FXCollections.observableArrayList();
        fournisseurs = FXCollections.observableArrayList();
        medicaments = FXCollections.observableArrayList();
        initializeComponents();
    }

    private void initializeComponents() {
        setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);

        ToolBar toolbar = new ToolBar();
        Button newOrderButton = new Button("Nouvelle Commande");
        Button updateStatusButton = new Button("Mettre à jour le statut");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher une commande...");
        searchField.setPrefWidth(200);

        toolbar.getItems().addAll(newOrderButton, updateStatusButton, new Separator(), searchField);

        commandesTable = new TableView<>();
        commandesTable.setItems(commandes);

        VBox.setVgrow(commandesTable, Priority.ALWAYS);
        commandesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Commande, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateCommande()));
        dateCol.setPrefWidth(150);
        dateCol.setMinWidth(100);

        TableColumn<Commande, String> fournisseurCol = new TableColumn<>("Fournisseur");
        fournisseurCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getFournisseur().getNom()));
        fournisseurCol.setPrefWidth(250);
        fournisseurCol.setMinWidth(150);

        TableColumn<Commande, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setPrefWidth(150);
        statusCol.setMinWidth(100);

        commandesTable.getColumns().addAll(dateCol, fournisseurCol, statusCol);

        newOrderButton.setOnAction(e -> showNewOrderDialog());
        updateStatusButton.setOnAction(e -> {
            Commande selectedCommande = commandesTable.getSelectionModel().getSelectedItem();
            if (selectedCommande != null) {
                showUpdateStatusDialog(selectedCommande);
            } else {
                showError("Aucune commande sélectionnée", "Veuillez sélectionner une commande à mettre à jour.");
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCommandes(newValue);
        });

        getChildren().addAll(toolbar, commandesTable);
    }

    private void showNewOrderDialog() {
        Dialog<Commande> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Commande");
        dialog.setHeaderText("Créer une nouvelle commande");

        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        ComboBox<Fournisseur> fournisseurCombo = new ComboBox<>(fournisseurs);
        fournisseurCombo.setPromptText("Sélectionner un fournisseur");
        fournisseurCombo.setPrefWidth(300);
        fournisseurCombo.setCellFactory(lv -> new ListCell<Fournisseur>() {
            @Override
            protected void updateItem(Fournisseur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        fournisseurCombo.setButtonCell(new ListCell<Fournisseur>() {
            @Override
            protected void updateItem(Fournisseur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        VBox medicationsContainer = new VBox(5);
        medicationsContainer.setPadding(new Insets(5));
        ScrollPane medicationsScroll = new ScrollPane(medicationsContainer);
        medicationsScroll.setFitToWidth(true);
        medicationsScroll.setPrefHeight(200);
        medicationsScroll.setStyle("-fx-background-color: transparent;");

        Button addMedicationButton = new Button("Ajouter Médicament");
        addMedicationButton.setStyle("-fx-base: #4CAF50;");

        grid.add(new Label("Fournisseur:"), 0, 0);
        grid.add(fournisseurCombo, 1, 0);
        grid.add(new Label("Médicaments:"), 0, 1);
        grid.add(medicationsScroll, 1, 1);
        grid.add(addMedicationButton, 1, 2);
        GridPane.setColumnSpan(medicationsScroll, 1);

        mainContainer.getChildren().addAll(grid);
        medicationsContainer.getChildren().add(createMedicationRow());

        addMedicationButton.setOnAction(e -> {
            medicationsContainer.getChildren().add(createMedicationRow());
        });

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setPrefSize(1000, 800);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Fournisseur selectedFournisseur = fournisseurCombo.getValue();
                if (selectedFournisseur != null && !medicationsContainer.getChildren().isEmpty()) {
                    Commande commande = new Commande(selectedFournisseur);

                    for (javafx.scene.Node node : medicationsContainer.getChildren()) {
                        if (node instanceof HBox) {
                            HBox row = (HBox) node;
                            ComboBox<Medicament> medCombo = (ComboBox<Medicament>) row.getChildren().get(0);
                            TextField quantityField = (TextField) row.getChildren().get(2);

                            try {
                                Medicament med = medCombo.getValue();
                                int quantity = Integer.parseInt(quantityField.getText().trim());

                                if (med != null && quantity > 0) {
                                    commande.ajouterMedicament(med, quantity);
                                } else {
                                    showError("Erreur",
                                            "Veuillez sélectionner un médicament et entrer une quantité valide");
                                    return null;
                                }
                            } catch (NumberFormatException ex) {
                                showError("Erreur", "Quantité invalide");
                                return null;
                            }
                        }
                    }

                    return commande;
                } else {
                    showError("Erreur", "Veuillez sélectionner un fournisseur et ajouter au moins un médicament");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(commande -> {
            if (commande != null) {
                commandes.add(commande);
            }
        });
    }

    private HBox createMedicationRow() {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));

        ComboBox<Medicament> medicamentCombo = new ComboBox<>(medicaments);
        medicamentCombo.setPromptText("Sélectionner un médicament");
        medicamentCombo.setPrefWidth(250);
        medicamentCombo.setCellFactory(lv -> new ListCell<Medicament>() {
            @Override
            protected void updateItem(Medicament item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        medicamentCombo.setButtonCell(new ListCell<Medicament>() {
            @Override
            protected void updateItem(Medicament item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantité");
        quantityField.setPrefWidth(100);

        Button removeButton = new Button("X");
        removeButton.setStyle("-fx-base: #F44336;");
        removeButton.setOnAction(e -> {
            Pane parent = (Pane) row.getParent();
            parent.getChildren().remove(row);

            if (parent.getChildren().isEmpty()) {
                parent.getChildren().add(createMedicationRow());
            }
        });

        row.getChildren().addAll(
                medicamentCombo,
                new Label("Quantité:"),
                quantityField,
                removeButton);

        return row;
    }

    private void showUpdateStatusDialog(Commande commande) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Mettre à jour le statut");
        dialog.setHeaderText("Modifier le statut de la commande");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("En attente", "En cours", "Livrée", "Annulée");
        statusCombo.setValue(commande.getStatus());
        statusCombo.setPrefWidth(300);

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().add(statusCombo);

        dialog.getDialogPane().setContent(container);
        dialog.getDialogPane().setPrefSize(600, 400);

        ButtonType updateButtonType = new ButtonType("Mettre à jour", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return statusCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(status -> {
            if (status != null) {
                commande.setStatus(status);
                commandesTable.refresh();
            }
        });
    }

    private void filterCommandes(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            commandesTable.setItems(commandes);
        } else {
            ObservableList<Commande> filteredList = FXCollections.observableArrayList();
            for (Commande commande : commandes) {
                if (commande.getFournisseur().getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                        commande.getStatus().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(commande);
                }
            }
            commandesTable.setItems(filteredList);
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
    }

    public void setMedicaments(ObservableList<Medicament> medicaments) {
        this.medicaments = medicaments;
    }
}