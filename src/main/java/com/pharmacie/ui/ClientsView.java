package com.pharmacie.ui;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import com.pharmacie.model.Client;
public class ClientsView extends VBox {
    private TableView<Client> clientsTable;
    private ObservableList<Client> clients;
    public ClientsView() {
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
        searchField.setPromptText("Rechercher un client...");
        searchField.setPrefWidth(200);
        toolbar.getItems().addAll(addButton, editButton, deleteButton, new Separator(), searchField);
        clientsTable = new TableView<>();
        clients = FXCollections.observableArrayList();
        VBox.setVgrow(clientsTable, Priority.ALWAYS);
        clientsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Client, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        nomCol.setPrefWidth(200);
        nomCol.setMinWidth(100);
        TableColumn<Client, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        prenomCol.setPrefWidth(200);
        prenomCol.setMinWidth(100);
        TableColumn<Client, String> telCol = new TableColumn<>("Téléphone");
        telCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumeroTelephone()));
        telCol.setPrefWidth(150);
        telCol.setMinWidth(100);
        clientsTable.getColumns().addAll(nomCol, prenomCol, telCol);
        clientsTable.setItems(clients);
        addButton.setOnAction(e -> showAddClientDialog());
        editButton.setOnAction(e -> {
            Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();
            if (selectedClient != null) {
                showEditClientDialog(selectedClient);
            } else {
                showAlert("Aucun client sélectionné", "Veuillez sélectionner un client à modifier.");
            }
        });
        deleteButton.setOnAction(e -> {
            Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();
            if (selectedClient != null) {
                showDeleteConfirmation(selectedClient);
            } else {
                showAlert("Aucun client sélectionné", "Veuillez sélectionner un client à supprimer.");
            }
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterClients(newValue);
        });
        getChildren().addAll(toolbar, clientsTable);
    }
    private void showAddClientDialog() {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un client");
        dialog.setHeaderText("Entrez les informations du client");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField telField = new TextField();
        nomField.setPrefWidth(250);
        prenomField.setPrefWidth(250);
        telField.setPrefWidth(250);
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
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
                String prenom = prenomField.getText().trim();
                String tel = telField.getText().trim();
                if (nom.isEmpty() || prenom.isEmpty() || tel.isEmpty()) {
                    showAlert("Champs manquants", "Veuillez remplir tous les champs.");
                    return null;
                }
                if (!tel.matches("\\d{10}")) {
                    showAlert("Format invalide", "Le numéro de téléphone doit contenir 10 chiffres.");
                    return null;
                }
                return new Client(nom, prenom, tel);
            }
            return null;
        });
        dialog.showAndWait().ifPresent(client -> {
            if (client != null) {
                clients.add(client);
            }
        });
    }
    private void showEditClientDialog(Client client) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Modifier un client");
        dialog.setHeaderText("Modifiez les informations du client");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField nomField = new TextField(client.getNom());
        TextField prenomField = new TextField(client.getPrenom());
        TextField telField = new TextField(client.getNumeroTelephone());
        nomField.setPrefWidth(250);
        prenomField.setPrefWidth(250);
        telField.setPrefWidth(250);
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
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
                String prenom = prenomField.getText().trim();
                String tel = telField.getText().trim();
                if (nom.isEmpty() || prenom.isEmpty() || tel.isEmpty()) {
                    showAlert("Champs manquants", "Veuillez remplir tous les champs.");
                    return null;
                }
                if (!tel.matches("\\d{10}")) {
                    showAlert("Format invalide", "Le numéro de téléphone doit contenir 10 chiffres.");
                    return null;
                }
                client.setNom(nom);
                client.setPrenom(prenom);
                client.setNumeroTelephone(tel);
                return client;
            }
            return null;
        });
        dialog.showAndWait().ifPresent(updatedClient -> {
            if (updatedClient != null) {
                clientsTable.refresh();
            }
        });
    }
    private void showDeleteConfirmation(Client client) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le client");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + client.getNom() + " " + client.getPrenom() + " ?");
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                clients.remove(client);
            }
        });
    }
    private void filterClients(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            clientsTable.setItems(clients);
        } else {
            ObservableList<Client> filteredList = FXCollections.observableArrayList();
            for (Client client : clients) {
                if (client.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                        client.getPrenom().toLowerCase().contains(searchText.toLowerCase()) ||
                        client.getNumeroTelephone().contains(searchText)) {
                    filteredList.add(client);
                }
            }
            clientsTable.setItems(filteredList);
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void setClients(ObservableList<Client> clients) {
        this.clients = clients;
        clientsTable.setItems(clients);
    }
}