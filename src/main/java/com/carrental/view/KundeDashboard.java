package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.BookingController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.model.Fahrzeug;
import com.carrental.model.Kunde;
import com.carrental.model.Mietvertrag;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Dashboard für Kunden.
 * Zeigt verfügbare Fahrzeuge, eigene Buchungen und ermöglicht neue Buchungen.
 */
public class KundeDashboard extends JPanel {

    private final MainFrame mainFrame;
    private final CarRentalSystem system;
    private final AuthController authController;
    private final BookingController bookingController;
    
    private JTable fahrzeugTable;
    private DefaultTableModel fahrzeugTableModel;
    private JTable buchungTable;
    private DefaultTableModel buchungTableModel;
    private JButton neueBuchungButton;
    private JButton logoutButton;
    private JLabel welcomeLabel;

    /**
     * Konstruktor für das Kunden-Dashboard.
     */
    public KundeDashboard(MainFrame mainFrame, CarRentalSystem system, AuthController authController) {
        this.mainFrame = mainFrame;
        this.system = system;
        this.authController = authController;
        this.bookingController = new BookingController(system);
        
        initializeUI();
        loadData();
    }

    /**
     * Initialisiert die Benutzeroberfläche.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header-Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        Kunde kunde = authController.getCurrentKunde();
        String name = kunde != null ? kunde.getVorname() + " " + kunde.getNachname() : "Kunde";
        welcomeLabel = new JLabel("Willkommen, " + name);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        logoutButton = new JButton("Abmelden");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> mainFrame.logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Tab-Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Verfügbare Fahrzeuge
        tabbedPane.addTab("Verfügbare Fahrzeuge", createFahrzeugPanel());
        
        // Tab 2: Meine Buchungen
        tabbedPane.addTab("Meine Buchungen", createBuchungPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Erstellt das Panel für verfügbare Fahrzeuge.
     */
    private JPanel createFahrzeugPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabelle für Fahrzeuge
        String[] columns = {"ID", "Kennzeichen", "Hersteller", "Modell", "Kategorie", 
                           "Tagespreis (€)", "Zustand"};
        fahrzeugTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabelle nicht editierbar
            }
        };
        fahrzeugTable = new JTable(fahrzeugTableModel);
        fahrzeugTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fahrzeugTable.setFont(new Font("Arial", Font.PLAIN, 12));
        fahrzeugTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(fahrzeugTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        neueBuchungButton = new JButton("Fahrzeug buchen");
        neueBuchungButton.setFont(new Font("Arial", Font.BOLD, 14));
        neueBuchungButton.setBackground(new Color(70, 130, 180));
        neueBuchungButton.setForeground(Color.WHITE);
        neueBuchungButton.addActionListener(e -> openBookingDialog());
        buttonPanel.add(neueBuchungButton);
        
        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.addActionListener(e -> loadFahrzeuge());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Erstellt das Panel für Buchungen.
     */
    private JPanel createBuchungPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabelle für Buchungen
        String[] columns = {"Mietnummer", "Fahrzeug", "Start", "Ende", "Preis (€)", "Status"};
        buchungTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        buchungTable = new JTable(buchungTableModel);
        buchungTable.setFont(new Font("Arial", Font.PLAIN, 12));
        buchungTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(buchungTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.addActionListener(e -> loadBuchungen());
        buttonPanel.add(refreshButton);
        
        JButton stornButton = new JButton("Buchung stornieren");
        stornButton.setBackground(new Color(255, 99, 71)); // Tomato
        stornButton.setForeground(Color.WHITE);
        stornButton.addActionListener(e -> storniereBuchung());
        buttonPanel.add(stornButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Lädt alle Daten.
     */
    private void loadData() {
        loadFahrzeuge();
        loadBuchungen();
    }

    /**
     * Lädt verfügbare Fahrzeuge in die Tabelle.
     */
    private void loadFahrzeuge() {
        fahrzeugTableModel.setRowCount(0); // Tabelle leeren
        
        List<Fahrzeug> fahrzeuge = system.getVerfuegbareFahrzeuge();
        
        for (Fahrzeug f : fahrzeuge) {
            Object[] row = {
                f.getId(),
                f.getKennzeichen(),
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getHersteller() : "-",
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getModellBezeichnung() : "-",
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getKategorie() : "-",
                f.getFahrzeugtyp() != null ? String.format("%.2f", f.getFahrzeugtyp().getStandardTagesPreis()) : "-",
                f.getZustand()
            };
            fahrzeugTableModel.addRow(row);
        }
    }

    /**
     * Lädt die Buchungen des aktuellen Kunden.
     */
    private void loadBuchungen() {
        buchungTableModel.setRowCount(0);
        
        Kunde kunde = authController.getCurrentKunde();
        if (kunde == null) return;
        
        try {
            List<Mietvertrag> vertraege = system.getMietvertragDao().findByKunde(kunde.getKundennummer());
            
            for (Mietvertrag v : vertraege) {
                String fahrzeugInfo = v.getFahrzeug() != null ? v.getFahrzeug().getKennzeichen() : "-";
                Object[] row = {
                    v.getMietnummer(),
                    fahrzeugInfo,
                    v.getStartDatum(),
                    v.getEndDatum(),
                    String.format("%.2f", v.getGesamtPreis()),
                    v.getStatus()
                };
                buchungTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Buchungen: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Öffnet den Buchungsdialog für das ausgewählte Fahrzeug.
     */
    private void openBookingDialog() {
        int selectedRow = fahrzeugTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie ein Fahrzeug aus der Liste.",
                "Kein Fahrzeug ausgewählt",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int fahrzeugId = (int) fahrzeugTableModel.getValueAt(selectedRow, 0);
        
        try {
            Fahrzeug fahrzeug = system.getFahrzeugDao().findById(fahrzeugId).orElse(null);
            if (fahrzeug == null) {
                JOptionPane.showMessageDialog(this,
                    "Fahrzeug nicht gefunden.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            BookingDialog dialog = new BookingDialog(mainFrame, system, authController, 
                                                     bookingController, fahrzeug);
            dialog.setVisible(true);
            
            // Nach Dialog-Schließen Daten aktualisieren
            loadData();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden des Fahrzeugs: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Storniert die ausgewählte Buchung.
     */
    private void storniereBuchung() {
        int selectedRow = buchungTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie eine Buchung aus der Liste.",
                "Keine Buchung ausgewählt",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String mietnummer = (String) buchungTableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Möchten Sie die Buchung " + mietnummer + " wirklich stornieren?",
            "Buchung stornieren",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Mietvertrag vertrag = system.getMietvertragDao().findByMietnummer(mietnummer).orElse(null);
                if (vertrag != null) {
                    boolean success = bookingController.buchungStornieren(vertrag);
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "Buchung erfolgreich storniert.",
                            "Erfolg",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadBuchungen();
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Fehler beim Stornieren: " + e.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
