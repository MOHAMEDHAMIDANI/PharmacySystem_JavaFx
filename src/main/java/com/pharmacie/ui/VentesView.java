package com.pharmacie.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import com.pharmacie.model.*;
import java.time.LocalDate;

public class VentesView extends VBox {
    private TableView<Ordonnance> ordonnancesTable;
    private ObservableList<Ordonnance> ordonnances;
    private ObservableList<Client> clients;
    private ObservableList<Medicament> medicaments;

    public VentesView() {
        setPadding(new Insets(10));
        setSpacing(10);
        clients = FXCollections.observableArrayList();
        medicaments = FXCollections.observableArrayList();
        ordonnances = FXCollections.observableArrayList();
        initializeComponents();
    }

    private void initializeComponents() {
        setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);

        ToolBar toolbar = new ToolBar();
        Button newSaleButton = new Button("Nouvelle Vente");
        Button viewHistoryButton = new Button("Historique Client");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher une vente...");
        searchField.setPrefWidth(200);

        toolbar.getItems().addAll(newSaleButton, viewHistoryButton, new Separator(), searchField);

        ordonnancesTable = new TableView<>();
        ordonnancesTable.setItems(ordonnances);

        VBox.setVgrow(ordonnancesTable, Priority.ALWAYS);
        ordonnancesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordonnance, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDatePrescription()));
        dateCol.setPrefWidth(150);
        dateCol.setMinWidth(100);

        TableColumn<Ordonnance, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getClient().getNom() + " " +
                        cellData.getValue().getClient().getPrenom()));
        clientCol.setPrefWidth(250);
        clientCol.setMinWidth(150);

        TableColumn<Ordonnance, Number> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixTotal()));
        totalCol.setPrefWidth(150);
        totalCol.setMinWidth(100);

        ordonnancesTable.getColumns().addAll(dateCol, clientCol, totalCol);

        newSaleButton.setOnAction(e -> showNewSaleDialog());
        viewHistoryButton.setOnAction(e -> showClientHistoryDialog());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterOrdonnances(newValue);
        });

        getChildren().addAll(toolbar, ordonnancesTable);
    }

    private void showNewSaleDialog() {
        Dialog<Ordonnance> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Vente");
        dialog.setHeaderText("Créer une nouvelle ordonnance");

        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        ComboBox<Client> clientCombo = new ComboBox<>(clients);
        clientCombo.setPromptText("Sélectionner un client");
        clientCombo.setPrefWidth(300);
        clientCombo.setCellFactory(lv -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.getNom() + " " + item.getPrenom()));
            }
        });
        clientCombo.setButtonCell(new ListCell<Client>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.getNom() + " " + item.getPrenom()));
            }
        });

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(200);

        CheckBox hasInsuranceCheck = new CheckBox("Carte d'assurance");
        TextField insuranceNumberField = new TextField();
        insuranceNumberField.setPromptText("Numéro de sécurité sociale");
        insuranceNumberField.setPrefWidth(250);
        TextField insuranceRateField = new TextField();
        insuranceRateField.setPromptText("Taux de réduction (%)");
        insuranceRateField.setPrefWidth(100);

        VBox medicationsContainer = new VBox(5);
        medicationsContainer.setPadding(new Insets(5));
        ScrollPane medicationsScroll = new ScrollPane(medicationsContainer);
        medicationsScroll.setFitToWidth(true);
        medicationsScroll.setPrefHeight(200);
        medicationsScroll.setStyle("-fx-background-color: transparent;");

        Button addMedicationButton = new Button("Ajouter Médicament");
        addMedicationButton.setStyle("-fx-base: #4CAF50;");

        grid.add(new Label("Client:"), 0, 0);
        grid.add(clientCombo, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(hasInsuranceCheck, 0, 2);
        grid.add(insuranceNumberField, 1, 2);
        grid.add(new Label("Taux:"), 0, 3);
        grid.add(insuranceRateField, 1, 3);
        grid.add(new Label("Médicaments:"), 0, 4);
        grid.add(medicationsScroll, 1, 4);
        grid.add(addMedicationButton, 1, 5);
        GridPane.setColumnSpan(medicationsScroll, 1);

        mainContainer.getChildren().add(grid);

        insuranceNumberField.setDisable(true);
        insuranceRateField.setDisable(true);
        hasInsuranceCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            insuranceNumberField.setDisable(!newVal);
            insuranceRateField.setDisable(!newVal);
        });

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
                Client selectedClient = clientCombo.getValue();
                if (selectedClient != null && !medicationsContainer.getChildren().isEmpty()) {
                    Ordonnance ordonnance = new Ordonnance(datePicker.getValue(), selectedClient);

                    if (hasInsuranceCheck.isSelected()) {
                        try {
                            String numero = insuranceNumberField.getText().trim();
                            double taux = Double.parseDouble(insuranceRateField.getText().trim()) / 100.0;
                            if (numero.isEmpty()) {
                                showError("Erreur", "Le numéro de sécurité sociale est requis");
                                return null;
                            }
                            if (taux < 0 || taux > 1) {
                                showError("Erreur", "Le taux de réduction doit être entre 0 et 100");
                                return null;
                            }
                            CarteAssurance carte = new CarteAssurance(numero, true, taux);
                            ordonnance.setCarteAssurance(carte);
                        } catch (NumberFormatException ex) {
                            showError("Erreur", "Taux de réduction invalide");
                            return null;
                        }
                    }

                    for (javafx.scene.Node node : medicationsContainer.getChildren()) {
                        if (node instanceof HBox) {
                            HBox row = (HBox) node;
                            ComboBox<Medicament> medCombo = (ComboBox<Medicament>) row.getChildren().get(0);
                            TextField dosageField = (TextField) row.getChildren().get(2);
                            TextField frequenceField = (TextField) row.getChildren().get(4);
                            TextField dureeField = (TextField) row.getChildren().get(6);

                            try {
                                Medicament med = medCombo.getValue();
                                int dosage = Integer.parseInt(dosageField.getText().trim());
                                int frequence = Integer.parseInt(frequenceField.getText().trim());
                                int duree = Integer.parseInt(dureeField.getText().trim());

                                if (med == null) {
                                    showError("Erreur", "Veuillez sélectionner un médicament");
                                    return null;
                                }
                                if (dosage <= 0 || frequence <= 0 || duree <= 0) {
                                    showError("Erreur", "Les valeurs doivent être positives");
                                    return null;
                                }

                                ordonnance.ajouterMedicament(med, dosage, frequence, duree);
                            } catch (NumberFormatException ex) {
                                showError("Erreur", "Valeurs numériques invalides");
                                return null;
                            }
                        }
                    }

                    return ordonnance;
                } else {
                    showError("Erreur", "Veuillez sélectionner un client et ajouter au moins un médicament");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ordonnance -> {
            if (ordonnance != null) {
                ordonnances.add(ordonnance);
                ordonnance.getClient().ajouterOrdonnance(ordonnance);
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

        TextField dosageField = new TextField();
        dosageField.setPromptText("Dosage");
        dosageField.setPrefWidth(80);

        TextField frequenceField = new TextField();
        frequenceField.setPromptText("Fréq/j");
        frequenceField.setPrefWidth(80);

        TextField dureeField = new TextField();
        dureeField.setPromptText("Jours");
        dureeField.setPrefWidth(80);

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
                new Label("Dosage:"),
                dosageField,
                new Label("Fréq:"),
                frequenceField,
                new Label("Durée:"),
                dureeField,
                removeButton);

        return row;
    }

    private void showClientHistoryDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Historique Client");
        dialog.setHeaderText("Sélectionnez un client pour voir son historique");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        ComboBox<Client> clientCombo = new ComboBox<>(clients);
        clientCombo.setPromptText("Sélectionner un client");
        clientCombo.setPrefWidth(400);
        clientCombo.setCellFactory(lv -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.getNom() + " " + item.getPrenom()));
            }
        });
        clientCombo.setButtonCell(new ListCell<Client>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.getNom() + " " + item.getPrenom()));
            }
        });

        TableView<Ordonnance> historyTable = new TableView<>();
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ordonnance, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(200);
        dateCol.setMinWidth(150);

        TableColumn<Ordonnance, Number> totalCol = new TableColumn<>("Total");
        totalCol.setPrefWidth(200);
        totalCol.setMinWidth(150);

        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDatePrescription()));
        totalCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixTotal()));

        historyTable.getColumns().addAll(dateCol, totalCol);

        clientCombo.setOnAction(e -> {
            Client selectedClient = clientCombo.getValue();
            if (selectedClient != null) {
                historyTable.setItems(FXCollections.observableArrayList(selectedClient.getHistorique()));
            }
        });

        content.getChildren().addAll(clientCombo, historyTable);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(800, 600);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private void filterOrdonnances(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            ordonnancesTable.setItems(ordonnances);
        } else {
            ObservableList<Ordonnance> filteredList = FXCollections.observableArrayList();
            for (Ordonnance ordonnance : ordonnances) {
                Client client = ordonnance.getClient();
                if (client.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                        client.getPrenom().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(ordonnance);
                }
            }
            ordonnancesTable.setItems(filteredList);
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setClients(ObservableList<Client> clients) {
        this.clients = clients;
    }

    public void setMedicaments(ObservableList<Medicament> medicaments) {
        this.medicaments = medicaments;
    }
}