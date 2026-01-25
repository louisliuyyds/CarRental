package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.CarRentalSystem;

import javax.swing.*;
import java.awt.*;

/**
 * Hauptfenster der Autovermietungsanwendung.
 * Verwaltet die verschiedenen Panels und den Navigationszustand.
 */
public class MainFrame extends JFrame {

    private final CarRentalSystem system;
    private final AuthController authController;
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Panel-Namen für CardLayout
    private static final String LOGIN_PANEL = "login";
    private static final String REGISTER_PANEL = "register";
    private static final String KUNDE_DASHBOARD = "kundeDashboard";
    private static final String MITARBEITER_DASHBOARD = "mitarbeiterDashboard";

    /**
     * Konstruktor für das Hauptfenster.
     */
    public MainFrame() {
        // System initialisieren
        this.system = CarRentalSystem.getInstance();
        this.authController = new AuthController(system);
        
        initialize();
    }

    /**
     * Konstruktor mit vorhandenen System- und Controller-Instanzen.
     * 
     * @param system Das CarRentalSystem
     * @param authController Der AuthController
     */
    public MainFrame(CarRentalSystem system, AuthController authController) {
        this.system = system;
        this.authController = authController;
        
        initialize();
    }

    /**
     * Initialisiert das Fenster.
     */
    private void initialize() {
        // Fenster konfigurieren
        setTitle("CarRental - Autovermietungssystem");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Zentriert das Fenster
        
        // Layout initialisieren
        initializeLayout();
        
        // Startseite anzeigen (Login)
        showLoginPanel();
    }

    /**
     * Initialisiert das CardLayout für Panel-Wechsel.
     */
    private void initializeLayout() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Panels hinzufügen
        contentPanel.add(new LoginPanel(this, authController), LOGIN_PANEL);
        contentPanel.add(new RegisterPanel(this, authController), REGISTER_PANEL);
        
        // Dashboards werden bei Bedarf erstellt
        
        add(contentPanel);
    }

    /**
     * Zeigt das Login-Panel an.
     */
    public void showLoginPanel() {
        cardLayout.show(contentPanel, LOGIN_PANEL);
    }

    /**
     * Zeigt das Registrierungs-Panel an.
     */
    public void showRegisterPanel() {
        cardLayout.show(contentPanel, REGISTER_PANEL);
    }

    /**
     * Zeigt das Kunden-Dashboard an.
     */
    public void showKundeDashboard() {
        if (authController.getCurrentKunde() == null) {
            JOptionPane.showMessageDialog(this, 
                "Kein Kunde angemeldet.", 
                "Fehler", 
                JOptionPane.ERROR_MESSAGE);
            showLoginPanel();
            return;
        }
        
        // Dashboard neu erstellen bei jedem Login
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals(KUNDE_DASHBOARD)) {
                contentPanel.remove(comp);
                break;
            }
        }
        
        KundeDashboard dashboard = new KundeDashboard(this, system, authController);
        dashboard.setName(KUNDE_DASHBOARD);
        contentPanel.add(dashboard, KUNDE_DASHBOARD);
        
        cardLayout.show(contentPanel, KUNDE_DASHBOARD);
    }

    /**
     * Zeigt das Mitarbeiter-Dashboard an.
     */
    public void showMitarbeiterDashboard() {
        if (!authController.isMitarbeiter()) {
            JOptionPane.showMessageDialog(this, 
                "Kein Mitarbeiter angemeldet.", 
                "Fehler", 
                JOptionPane.ERROR_MESSAGE);
            showLoginPanel();
            return;
        }
        
        // Dashboard neu erstellen
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals(MITARBEITER_DASHBOARD)) {
                contentPanel.remove(comp);
                break;
            }
        }
        
        MitarbeiterDashboard dashboard = new MitarbeiterDashboard(system, authController);
        dashboard.setName(MITARBEITER_DASHBOARD);
        contentPanel.add(dashboard, MITARBEITER_DASHBOARD);
        
        cardLayout.show(contentPanel, MITARBEITER_DASHBOARD);
    }

    /**
     * Meldet den Benutzer ab und kehrt zum Login zurück.
     */
    public void logout() {
        authController.logout();
        showLoginPanel();
    }

    /**
     * Gibt die AuthController-Instanz zurück.
     */
    public AuthController getAuthController() {
        return authController;
    }

    /**
     * Gibt die CarRentalSystem-Instanz zurück.
     */
    public CarRentalSystem getSystem() {
        return system;
    }

    /**
     * Hauptmethode zum Starten der Anwendung.
     */
    public static void main(String[] args) {
        // Swing auf Event Dispatch Thread ausführen
        SwingUtilities.invokeLater(() -> {
            try {
                // System Look and Feel verwenden
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
