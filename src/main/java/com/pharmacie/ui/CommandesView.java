package com.pharmacie.ui;

import javafx.geometry.Insets;
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
        ToolBar toolbar = new ToolBar();
        Button newOrderButton = new Button("Nouvelle Commande");
        Button updateStatusButton = new Button("Mettre à jour le statut");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher une commande...");

        toolbar.getItems().addAll(newOrderButton, updateStatusButton, new Separator(), searchField);

        commandesTable = new TableView<>();
        commandesTable.setItems(commandes);

        TableColumn<Commande, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateCommande()));

        TableColumn<Commande, String> fournisseurCol = new TableColumn<>("Fournisseur");
        fournisseurCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getFournisseur().getNom()));

        TableColumn<Commande, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        commandesTable.getColumns().addAll(dateCol, fournisseurCol, statusCol);

        newOrderButton.setOnAction(e -> showNewOrderDialog());
        updateStatusButton.setOnAction(e -> {
            Commande selectedCommande = commandesTable.getSelectionModel().getSelectedItem();
            if (selectedCommande != null) {
                showUpdateStatusDialog(selectedCommande);
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

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Fournisseur> fournisseurCombo = new ComboBox<>(fournisseurs);
        fournisseurCombo.setPromptText("Sélectionner un fournisseur");
        fournisseurCombo.setPrefWidth(200);
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

        VBox medicationsBox = new VBox(10);
        Button addMedicationButton = new Button("Ajouter Médicament");

        grid.add(new Label("Fournisseur:"), 0, 0);
        grid.add(fournisseurCombo, 1, 0);
        grid.add(new Label("Médicaments:"), 0, 1);
        grid.add(medicationsBox, 1, 1);
        grid.add(addMedicationButton, 1, 2);

        addMedicationButton.setOnAction(e -> {
            HBox medicationRow = createMedicationRow();
            medicationsBox.getChildren().add(medicationRow);
        });

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setPrefSize(500, 400);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Fournisseur selectedFournisseur = fournisseurCombo.getValue();
                if (selectedFournisseur != null) {
                    Commande commande = new Commande(selectedFournisseur);

                    for (javafx.scene.Node node : medicationsBox.getChildren()) {
                        if (node instanceof HBox) {
                            HBox row = (HBox) node;
                            ComboBox<Medicament> medCombo = (ComboBox<Medicament>) row.getChildren().get(0);
                            TextField quantityField = (TextField) row.getChildren().get(2);

                            try {
                                Medicament med = medCombo.getValue();
                                int quantity = Integer.parseInt(quantityField.getText());

                                commande.ajouterMedicament(med, quantity);
                            } catch (NumberFormatException ex) {
                                showError("Erreur", "Quantité invalide");
                                return null;
                            }
                        }
                    }

                    return commande;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(commande -> {
            commandes.add(commande);
        });
    }

    private HBox createMedicationRow() {
        HBox row = new HBox(10);

        ComboBox<Medicament> medicamentCombo = new ComboBox<>(medicaments);
        medicamentCombo.setPromptText("Sélectionner un médicament");
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
        quantityField.setPrefWidth(80);

        Button removeButton = new Button("X");
        removeButton.setOnAction(e -> {
            ((VBox) row.getParent()).getChildren().remove(row);
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

        ScrollPane scrollPane2 = new ScrollPane(statusCombo);
        scrollPane2.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane2);

        ButtonType updateButtonType = new ButtonType("Mettre à jour", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return statusCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(status -> {
            commande.setStatus(status);
            commandesTable.refresh();
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
