package com.pharmacie.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.pharmacie.model.*;

public class MainView {
    private TabPane tabPane;
    private VBox root;
    private ObservableList<Client> clients;
    private ObservableList<Fournisseur> fournisseurs;
    private ObservableList<Medicament> medicaments;

    public MainView() {
        clients = FXCollections.observableArrayList();
        fournisseurs = FXCollections.observableArrayList();
        medicaments = FXCollections.observableArrayList();
        initializeComponents();
    }

    private void initializeComponents() {
        root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        MenuBar menuBar = createMenuBar();

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        ClientsView clientsView = new ClientsView();
        clientsView.setClients(clients);
        
        MedicamentsView medicamentsView = new MedicamentsView();
        medicamentsView.setMedicaments(medicaments);
        
        FournisseursView fournisseursView = new FournisseursView();
        fournisseursView.setFournisseurs(fournisseurs);
        
        VentesView ventesView = new VentesView();
        ventesView.setClients(clients);
        ventesView.setMedicaments(medicaments);
        
        CommandesView commandesView = new CommandesView();
        commandesView.setFournisseurs(fournisseurs);
        commandesView.setMedicaments(medicaments);

        Tab clientsTab = new Tab("Clients", clientsView);
        Tab medicamentsTab = new Tab("Médicaments", medicamentsView);
        Tab fournisseursTab = new Tab("Fournisseurs", fournisseursView);
        Tab ventesTab = new Tab("Ventes", ventesView);
        Tab commandesTab = new Tab("Commandes", commandesView);

        tabPane.getTabs().addAll(clientsTab, medicamentsTab, fournisseursTab, ventesTab, commandesTab);

        root.getChildren().addAll(menuBar, tabPane);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("Fichier");
        MenuItem exitItem = new MenuItem("Quitter");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        Menu helpMenu = new Menu("Aide");
        MenuItem aboutItem = new MenuItem("À propos");
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    public VBox getRoot() {
        return root;
    }
}
