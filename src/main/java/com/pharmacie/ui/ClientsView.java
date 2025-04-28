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
        ToolBar toolbar = new ToolBar();
        Button addButton = new Button("Ajouter");
        Button editButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un client...");

        toolbar.getItems().addAll(addButton, editButton, deleteButton, new Separator(), searchField);

        clientsTable = new TableView<>();
        clients = FXCollections.observableArrayList();

        TableColumn<Client, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));

        TableColumn<Client, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));

        TableColumn<Client, String> telCol = new TableColumn<>("Téléphone");
        telCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumeroTelephone()));

        clientsTable.getColumns().addAll(nomCol, prenomCol, telCol);
        clientsTable.setItems(clients);

        addButton.setOnAction(e -> showAddClientDialog());
        editButton.setOnAction(e -> {
            Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();
            if (selectedClient != null) {
                showEditClientDialog(selectedClient);
            }
        });
        deleteButton.setOnAction(e -> {
            Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();
            if (selectedClient != null) {
                showDeleteConfirmation(selectedClient);
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

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(telField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Client(nomField.getText(), prenomField.getText(), telField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(client -> {
            clients.add(client);
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

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(telField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                client.setNom(nomField.getText());
                client.setPrenom(prenomField.getText());
                client.setNumeroTelephone(telField.getText());
                return client;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedClient -> {
            clientsTable.refresh();
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
}