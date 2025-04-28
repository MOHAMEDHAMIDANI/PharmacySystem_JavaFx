package com.pharmacie.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.TabPane.TabClosingPolicy;

public class MainView {
    private TabPane tabPane;
    private VBox root;

    public MainView() {
        initializeComponents();
    }

    private void initializeComponents() {
        root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        MenuBar menuBar = createMenuBar();

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        Tab clientsTab = new Tab("Clients", new ClientsView());
        Tab medicamentsTab = new Tab("Médicaments", new MedicamentsView());
        Tab fournisseursTab = new Tab("Fournisseurs", new FournisseursView());
        Tab ventesTab = new Tab("Ventes", new VentesView());
        Tab commandesTab = new Tab("Commandes", new CommandesView());

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
