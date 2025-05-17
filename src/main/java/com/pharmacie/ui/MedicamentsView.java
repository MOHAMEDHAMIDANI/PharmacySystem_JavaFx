package com.pharmacie.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import com.pharmacie.model.*;
import java.time.LocalDate;

public class MedicamentsView extends VBox {
    private TableView<Medicament> medicamentsTable;
    private ObservableList<Medicament> medicaments;
    public MedicamentsView() {
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
        Button expiryButton = new Button("Médicaments proche péremption");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un médicament...");
        searchField.setPrefWidth(200);
        toolbar.getItems().addAll(addButton, editButton, deleteButton, new Separator(),
                expiryButton, new Separator(), searchField);
        medicamentsTable = new TableView<>();
        medicaments = FXCollections.observableArrayList();
        VBox.setVgrow(medicamentsTable, Priority.ALWAYS);
        medicamentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Medicament, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        nomCol.setPrefWidth(200);
        nomCol.setMinWidth(100);
        TableColumn<Medicament, String> refCol = new TableColumn<>("Référence");
        refCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReference()));
        refCol.setPrefWidth(150);
        refCol.setMinWidth(100);
        TableColumn<Medicament, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantiteStock()));
        stockCol.setPrefWidth(100);
        stockCol.setMinWidth(80);
        TableColumn<Medicament, LocalDate> dateCol = new TableColumn<>("Date Péremption");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDatePeremption()));
        dateCol.setPrefWidth(150);
        dateCol.setMinWidth(120);
        TableColumn<Medicament, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            Medicament med = cellData.getValue();
            String type = "Inconnu";
            if (med instanceof MedicamentComprime)
                type = "Comprimé";
            else if (med instanceof MedicamentSirop)
                type = "Sirop";
            else if (med instanceof MedicamentInjectable)
                type = "Injectable";
            return new SimpleStringProperty(type);
        });
        typeCol.setPrefWidth(120);
        typeCol.setMinWidth(100);
        medicamentsTable.getColumns().addAll(nomCol, refCol, stockCol, dateCol, typeCol);
        medicamentsTable.setItems(medicaments);
        addButton.setOnAction(e -> showAddMedicamentDialog());
        editButton.setOnAction(e -> {
            Medicament selectedMed = medicamentsTable.getSelectionModel().getSelectedItem();
            if (selectedMed != null) {
                showEditMedicamentDialog(selectedMed);
            } else {
                showError("Aucun médicament sélectionné", "Veuillez sélectionner un médicament à modifier.");
            }
        });
        deleteButton.setOnAction(e -> {
            Medicament selectedMed = medicamentsTable.getSelectionModel().getSelectedItem();
            if (selectedMed != null) {
                showDeleteConfirmation(selectedMed);
            } else {
                showError("Aucun médicament sélectionné", "Veuillez sélectionner un médicament à supprimer.");
            }
        });
        expiryButton.setOnAction(e -> showExpiryMedicaments());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterMedicaments(newValue);
        });
        getChildren().addAll(toolbar, medicamentsTable);
    }
    private void showAddMedicamentDialog() {
        Dialog<Medicament> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un médicament");
        dialog.setHeaderText("Entrez les informations du médicament");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField nomField = new TextField();
        TextField refField = new TextField();
        TextField stockField = new TextField();
        DatePicker datePeremption = new DatePicker();
        CheckBox generiqueCheck = new CheckBox("Médicament générique");
        TextField prixField = new TextField();
        nomField.setPrefWidth(250);
        refField.setPrefWidth(250);
        stockField.setPrefWidth(100);
        prixField.setPrefWidth(100);
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Comprimé", "Sirop", "Injectable");
        typeCombo.setValue("Comprimé");
        typeCombo.setPrefWidth(250);
        TextField unitsField = new TextField();
        TextField contenanceField = new TextField();
        Label specificLabel = new Label();
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Référence:"), 0, 1);
        grid.add(refField, 1, 1);
        grid.add(new Label("Stock:"), 0, 2);
        grid.add(stockField, 1, 2);
        grid.add(new Label("Date Péremption:"), 0, 3);
        grid.add(datePeremption, 1, 3);
        grid.add(generiqueCheck, 1, 4);
        grid.add(new Label("Prix:"), 0, 5);
        grid.add(prixField, 1, 5);
        grid.add(new Label("Type:"), 0, 6);
        grid.add(typeCombo, 1, 6);
        typeCombo.setOnAction(e -> {
            grid.getChildren().removeAll(specificLabel, unitsField, contenanceField);
            if (typeCombo.getValue().equals("Comprimé")) {
                specificLabel.setText("Unités par boîte:");
                grid.add(specificLabel, 0, 7);
                grid.add(unitsField, 1, 7);
                unitsField.setPrefWidth(100);
            } else if (typeCombo.getValue().equals("Sirop")) {
                specificLabel.setText("Contenance (ml):");
                grid.add(specificLabel, 0, 7);
                grid.add(contenanceField, 1, 7);
                contenanceField.setPrefWidth(100);
            }
        });
        typeCombo.getOnAction().handle(null);
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane);
        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String nom = nomField.getText().trim();
                    String ref = refField.getText().trim();
                    int stock = Integer.parseInt(stockField.getText().trim());
                    LocalDate date = datePeremption.getValue();
                    boolean generique = generiqueCheck.isSelected();
                    double prix = Double.parseDouble(prixField.getText().trim());
                    if (nom.isEmpty() || ref.isEmpty()) {
                        showError("Champs manquants", "Veuillez remplir tous les champs obligatoires.");
                        return null;
                    }
                    if (stock < 0 || prix < 0) {
                        showError("Valeurs invalides", "Le stock et le prix doivent être positifs.");
                        return null;
                    }
                    switch (typeCombo.getValue()) {
                        case "Comprimé":
                            int units = Integer.parseInt(unitsField.getText().trim());
                            if (units <= 0) {
                                showError("Valeur invalide", "Le nombre d'unités doit être positif.");
                                return null;
                            }
                            return new MedicamentComprime(nom, ref, stock, date, generique, prix, units);
                        case "Sirop":
                            int contenance = Integer.parseInt(contenanceField.getText().trim());
                            if (contenance <= 0) {
                                showError("Valeur invalide", "La contenance doit être positive.");
                                return null;
                            }
                            return new MedicamentSirop(nom, ref, stock, date, generique, prix, contenance);
                        case "Injectable":
                            return new MedicamentInjectable(nom, ref, stock, date, generique, prix);
                        default:
                            return null;
                    }
                } catch (NumberFormatException ex) {
                    showError("Erreur de saisie", "Veuillez vérifier les valeurs numériques");
                    return null;
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(medicament -> {
            if (medicament != null) {
                medicaments.add(medicament);
            }
        });
    }
    private void showEditMedicamentDialog(Medicament medicament) {
        Dialog<Medicament> dialog = new Dialog<>();
        dialog.setTitle("Modifier un médicament");
        dialog.setHeaderText("Modifiez les informations du médicament");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField nomField = new TextField(medicament.getNom());
        TextField refField = new TextField(medicament.getReference());
        TextField stockField = new TextField(String.valueOf(medicament.getQuantiteStock()));
        DatePicker datePeremption = new DatePicker(medicament.getDatePeremption());
        CheckBox generiqueCheck = new CheckBox("Médicament générique");
        generiqueCheck.setSelected(medicament.isEstGenerique());
        TextField prixField = new TextField(String.valueOf(medicament.getPrix()));
        nomField.setPrefWidth(250);
        refField.setPrefWidth(250);
        stockField.setPrefWidth(100);
        prixField.setPrefWidth(100);
        final TextField specificField;
        final Label specificLabel;
        if (medicament instanceof MedicamentComprime) {
            specificField = new TextField(String.valueOf(((MedicamentComprime) medicament).getNombreUnites()));
            specificLabel = new Label("Unités par boîte:");
            specificField.setPrefWidth(100);
        } else if (medicament instanceof MedicamentSirop) {
            specificField = new TextField(String.valueOf(((MedicamentSirop) medicament).getContenanceML()));
            specificLabel = new Label("Contenance (ml):");
            specificField.setPrefWidth(100);
        } else {
            specificField = null;
            specificLabel = null;
        }
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Référence:"), 0, 1);
        grid.add(refField, 1, 1);
        grid.add(new Label("Stock:"), 0, 2);
        grid.add(stockField, 1, 2);
        grid.add(new Label("Date Péremption:"), 0, 3);
        grid.add(datePeremption, 1, 3);
        grid.add(generiqueCheck, 1, 4);
        grid.add(new Label("Prix:"), 0, 5);
        grid.add(prixField, 1, 5);
        if (specificField != null && specificLabel != null) {
            grid.add(specificLabel, 0, 6);
            grid.add(specificField, 1, 6);
        }
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane);
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String nom = nomField.getText().trim();
                    String ref = refField.getText().trim();
                    int stock = Integer.parseInt(stockField.getText().trim());
                    LocalDate date = datePeremption.getValue();
                    boolean generique = generiqueCheck.isSelected();
                    double prix = Double.parseDouble(prixField.getText().trim());
                    if (nom.isEmpty() || ref.isEmpty()) {
                        showError("Champs manquants", "Veuillez remplir tous les champs obligatoires.");
                        return null;
                    }
                    if (stock < 0 || prix < 0) {
                        showError("Valeurs invalides", "Le stock et le prix doivent être positifs.");
                        return null;
                    }
                    if (medicament instanceof MedicamentComprime && specificField != null) {
                        int units = Integer.parseInt(specificField.getText().trim());
                        if (units <= 0) {
                            showError("Valeur invalide", "Le nombre d'unités doit être positif.");
                            return null;
                        }
                        ((MedicamentComprime) medicament).setNombreUnites(units);
                    } else if (medicament instanceof MedicamentSirop && specificField != null) {
                        int contenance = Integer.parseInt(specificField.getText().trim());
                        if (contenance <= 0) {
                            showError("Valeur invalide", "La contenance doit être positive.");
                            return null;
                        }
                        ((MedicamentSirop) medicament).setContenanceML(contenance);
                    }
                    medicament.setNom(nom);
                    medicament.setReference(ref);
                    medicament.setQuantiteStock(stock);
                    medicament.setDatePeremption(date);
                    medicament.setEstGenerique(generique);
                    medicament.setPrix(prix);
                    return medicament;
                } catch (NumberFormatException ex) {
                    showError("Erreur de saisie", "Veuillez vérifier les valeurs numériques");
                    return null;
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(updatedMedicament -> {
            if (updatedMedicament != null) {
                medicamentsTable.refresh();
            }
        });
    }
    private void showDeleteConfirmation(Medicament medicament) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le médicament");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + medicament.getNom() + " ?");
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                medicaments.remove(medicament);
            }
        });
    }
    private void showExpiryMedicaments() {
        ObservableList<Medicament> expiryList = medicaments.filtered(
                medicament -> medicament.estProcheDeLaPeremption());
        medicamentsTable.setItems(expiryList);
    }
    private void filterMedicaments(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            medicamentsTable.setItems(medicaments);
        } else {
            ObservableList<Medicament> filteredList = FXCollections.observableArrayList();
            for (Medicament med : medicaments) {
                if (med.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                        med.getReference().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(med);
                }
            }
            medicamentsTable.setItems(filteredList);
        }
    }
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void setMedicaments(ObservableList<Medicament> medicaments) {
        this.medicaments = medicaments;
        medicamentsTable.setItems(medicaments);
    }
}
