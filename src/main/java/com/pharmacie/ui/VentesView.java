package com.pharmacie.ui;
import javafx.geometry.Insets;
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
        initializeComponents();
    }

    private void initializeComponents() {
        ToolBar toolbar = new ToolBar();
        Button newSaleButton = new Button("Nouvelle Vente");
        Button viewHistoryButton = new Button("Historique Client");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher une vente...");

        toolbar.getItems().addAll(newSaleButton, viewHistoryButton, new Separator(), searchField);

        ordonnancesTable = new TableView<>();
        ordonnances = FXCollections.observableArrayList();

        TableColumn<Ordonnance, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDatePrescription()));

        TableColumn<Ordonnance, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getClient().getNom() + " " +
                        cellData.getValue().getClient().getPrenom()));

        TableColumn<Ordonnance, Number> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixTotal()));

        ordonnancesTable.getColumns().addAll(dateCol, clientCol, totalCol);
        ordonnancesTable.setItems(ordonnances);

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

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Client> clientCombo = new ComboBox<>(clients);
        clientCombo.setPromptText("Sélectionner un client");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        CheckBox hasInsuranceCheck = new CheckBox("Carte d'assurance");
        TextField insuranceNumberField = new TextField();
        insuranceNumberField.setPromptText("Numéro de sécurité sociale");
        TextField insuranceRateField = new TextField();
        insuranceRateField.setPromptText("Taux de réduction (%)");

        VBox medicationsBox = new VBox(10);
        Button addMedicationButton = new Button("Ajouter Médicament");

        grid.add(new Label("Client:"), 0, 0);
        grid.add(clientCombo, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(hasInsuranceCheck, 0, 2);
        grid.add(insuranceNumberField, 1, 2);
        grid.add(new Label("Taux:"), 0, 3);
        grid.add(insuranceRateField, 1, 3);
        grid.add(new Label("Médicaments:"), 0, 4);
        grid.add(medicationsBox, 1, 4);
        grid.add(addMedicationButton, 1, 5);

        insuranceNumberField.setDisable(true);
        insuranceRateField.setDisable(true);
        hasInsuranceCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            insuranceNumberField.setDisable(!newVal);
            insuranceRateField.setDisable(!newVal);
        });

        addMedicationButton.setOnAction(e -> {
            HBox medicationRow = createMedicationRow();
            medicationsBox.getChildren().add(medicationRow);
        });

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Client selectedClient = clientCombo.getValue();
                if (selectedClient != null) {
                    Ordonnance ordonnance = new Ordonnance(datePicker.getValue(), selectedClient);

                    if (hasInsuranceCheck.isSelected()) {
                        try {
                            String numero = insuranceNumberField.getText();
                            double taux = Double.parseDouble(insuranceRateField.getText()) / 100.0;
                            CarteAssurance carte = new CarteAssurance(numero, true, taux);
                            ordonnance.setCarteAssurance(carte);
                        } catch (NumberFormatException ex) {
                            showError("Erreur", "Taux de réduction invalide");
                            return null;
                        }
                    }

                    for (javafx.scene.Node node : medicationsBox.getChildren()) {
                        if (node instanceof HBox) {
                            HBox row = (HBox) node;
                            ComboBox<Medicament> medCombo = (ComboBox<Medicament>) row.getChildren().get(0);
                            TextField dosageField = (TextField) row.getChildren().get(2);
                            TextField frequenceField = (TextField) row.getChildren().get(4);
                            TextField dureeField = (TextField) row.getChildren().get(6);

                            try {
                                Medicament med = medCombo.getValue();
                                int dosage = Integer.parseInt(dosageField.getText());
                                int frequence = Integer.parseInt(frequenceField.getText());
                                int duree = Integer.parseInt(dureeField.getText());

                                ordonnance.ajouterMedicament(med, dosage, frequence, duree);
                            } catch (NumberFormatException ex) {
                                showError("Erreur", "Valeurs numériques invalides");
                                return null;
                            }
                        }
                    }

                    return ordonnance;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ordonnance -> {
            ordonnances.add(ordonnance);
            ordonnance.getClient().ajouterOrdonnance(ordonnance);
        });
    }

    private HBox createMedicationRow() {
        HBox row = new HBox(10);

        ComboBox<Medicament> medicamentCombo = new ComboBox<>(medicaments);
        medicamentCombo.setPromptText("Sélectionner un médicament");

        TextField dosageField = new TextField();
        dosageField.setPromptText("Dosage");
        dosageField.setPrefWidth(80);

        TextField frequenceField = new TextField();
        frequenceField.setPromptText("Fréquence");
        frequenceField.setPrefWidth(80);

        TextField dureeField = new TextField();
        dureeField.setPromptText("Durée (jours)");
        dureeField.setPrefWidth(80);

        Button removeButton = new Button("X");
        removeButton.setOnAction(e -> {
            ((VBox) row.getParent()).getChildren().remove(row);
        });

        row.getChildren().addAll(
                medicamentCombo,
                new Label("Dosage:"),
                dosageField,
                new Label("Fréquence:"),
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
        TableView<Ordonnance> historyTable = new TableView<>();

        TableColumn<Ordonnance, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDatePrescription()));

        TableColumn<Ordonnance, Number> totalCol = new TableColumn<>("Total");
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
