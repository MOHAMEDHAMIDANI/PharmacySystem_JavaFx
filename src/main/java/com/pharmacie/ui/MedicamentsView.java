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
        ToolBar toolbar = new ToolBar();
        Button addButton = new Button("Ajouter");
        Button editButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
        Button expiryButton = new Button("Médicaments proche péremption");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un médicament...");

        toolbar.getItems().addAll(addButton, editButton, deleteButton, new Separator(),
                expiryButton, new Separator(), searchField);

        medicamentsTable = new TableView<>();
        medicaments = FXCollections.observableArrayList();

        TableColumn<Medicament, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));

        TableColumn<Medicament, String> refCol = new TableColumn<>("Référence");
        refCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReference()));

        TableColumn<Medicament, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantiteStock()));

        TableColumn<Medicament, LocalDate> dateCol = new TableColumn<>("Date Péremption");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDatePeremption()));

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

        medicamentsTable.getColumns().addAll(nomCol, refCol, stockCol, dateCol, typeCol);
        medicamentsTable.setItems(medicaments);

        addButton.setOnAction(e -> showAddMedicamentDialog());
        editButton.setOnAction(e -> {
            Medicament selectedMed = medicamentsTable.getSelectionModel().getSelectedItem();
            if (selectedMed != null) {
                showEditMedicamentDialog(selectedMed);
            }
        });
        deleteButton.setOnAction(e -> {
            Medicament selectedMed = medicamentsTable.getSelectionModel().getSelectedItem();
            if (selectedMed != null) {
                showDeleteConfirmation(selectedMed);
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

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Comprimé", "Sirop", "Injectable");
        typeCombo.setValue("Comprimé");

        TextField unitsField = new TextField();
        TextField contenanceField = new TextField();

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
            grid.getChildren().removeAll(unitsField, contenanceField);
            if (typeCombo.getValue().equals("Comprimé")) {
                grid.add(new Label("Unités par boîte:"), 0, 7);
                grid.add(unitsField, 1, 7);
            } else if (typeCombo.getValue().equals("Sirop")) {
                grid.add(new Label("Contenance (ml):"), 0, 7);
                grid.add(contenanceField, 1, 7);
            }
        });

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String nom = nomField.getText();
                    String ref = refField.getText();
                    int stock = Integer.parseInt(stockField.getText());
                    LocalDate date = datePeremption.getValue();
                    boolean generique = generiqueCheck.isSelected();
                    double prix = Double.parseDouble(prixField.getText());

                    switch (typeCombo.getValue()) {
                        case "Comprimé":
                            int units = Integer.parseInt(unitsField.getText());
                            return new MedicamentComprime(nom, ref, stock, date, generique, prix, units);
                        case "Sirop":
                            int contenance = Integer.parseInt(contenanceField.getText());
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
            medicaments.add(medicament);
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

        final TextField specificField;
        final Label specificLabel;

        if (medicament instanceof MedicamentComprime) {
            specificField = new TextField(String.valueOf(((MedicamentComprime) medicament).getNombreUnites()));
            specificLabel = new Label("Unités par boîte:");
        } else if (medicament instanceof MedicamentSirop) {
            specificField = new TextField(String.valueOf(((MedicamentSirop) medicament).getContenanceML()));
            specificLabel = new Label("Contenance (ml):");
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

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String nom = nomField.getText();
                    String ref = refField.getText();
                    int stock = Integer.parseInt(stockField.getText());
                    LocalDate date = datePeremption.getValue();
                    boolean generique = generiqueCheck.isSelected();
                    double prix = Double.parseDouble(prixField.getText());

                    if (medicament instanceof MedicamentComprime && specificField != null) {
                        int units = Integer.parseInt(specificField.getText());
                        ((MedicamentComprime) medicament).setNombreUnites(units);
                    } else if (medicament instanceof MedicamentSirop && specificField != null) {
                        int contenance = Integer.parseInt(specificField.getText());
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
            medicamentsTable.refresh();
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
}
