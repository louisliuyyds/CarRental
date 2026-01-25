package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Dashboard für Mitarbeiter.
 * Zeigt Fahrzeugverwaltung, Vertragsverwaltung und Statistiken.
 */
public class MitarbeiterDashboard extends JPanel {

    private final CarRentalSystem system;
    private final AuthController authController;
    
    private JTabbedPane tabbedPane;
    private DefaultTableModel fahrzeugTableModel;
    private JTable fahrzeugTable;
    private DefaultTableModel vertragTableModel;
    private JTable vertragTable;
    private FahrzeugPanel fahrzeugPanel;
    
    /**
     * Konstruktor für das Mitarbeiter-Dashboard.
     */
    public MitarbeiterDashboard(CarRentalSystem system, AuthController authController) {
        this.system = system;
        this.authController = authController;
        
        initializeUI();
        loadData();
    }

    /**
     * Initialisiert die Benutzeroberfläche.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        Mitarbeiter mitarbeiter = authController.getCurrentMitarbeiter();
        String willkommensText = mitarbeiter != null ? 
            "Mitarbeiter-Bereich: " + mitarbeiter.getVorname() + " " + mitarbeiter.getNachname() :
            "Mitarbeiter-Bereich";
        
        JLabel headerLabel = new JLabel(willkommensText);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        JButton logoutButton = new JButton("Abmelden");
        logoutButton.addActionListener(e -> {
            authController.logout();
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame instanceof MainFrame) {
                ((MainFrame) frame).showLoginPanel();
            }
        });
        headerPanel.add(Box.createHorizontalStrut(20));
        headerPanel.add(logoutButton);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        
        // Tab 1: Fahrzeugverwaltung
        fahrzeugPanel = new FahrzeugPanel(system);
        tabbedPane.addTab("Fahrzeugverwaltung", createFahrzeugIcon(), 
            fahrzeugPanel, "Fahrzeuge und Fahrzeugtypen verwalten");
        
        // Tab 2: Vertragsverwaltung
        JPanel vertragPanel = createVertragPanel();
        tabbedPane.addTab("Vertragsverwaltung", createVertragIcon(), 
            vertragPanel, "Alle Mietverträge anzeigen und verwalten");
        
        // Tab 3: Statistiken
        JPanel statistikPanel = createStatistikPanel();
        tabbedPane.addTab("Statistiken", createStatistikIcon(), 
            statistikPanel, "Statistiken und Auswertungen");
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Erstellt das Fahrzeugverwaltungs-Panel.
     * (Wird nicht verwendet, da FahrzeugPanel direkt integriert ist)
     */
    @SuppressWarnings("unused")
    private JPanel createFahrzeugverwaltungPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.addActionListener(e -> loadFahrzeuge());
        toolbar.add(refreshButton);
        
        JButton fahrzeugHinzufuegenButton = new JButton("Fahrzeug hinzufügen");
        fahrzeugHinzufuegenButton.addActionListener(e -> fahrzeugHinzufuegen());
        toolbar.add(fahrzeugHinzufuegenButton);
        
        JButton fahrzeugBearbeitenButton = new JButton("Bearbeiten");
        fahrzeugBearbeitenButton.addActionListener(e -> fahrzeugBearbeiten());
        toolbar.add(fahrzeugBearbeitenButton);
        
        JButton fahrzeugLoeschenButton = new JButton("Löschen");
        fahrzeugLoeschenButton.addActionListener(e -> fahrzeugLoeschen());
        toolbar.add(fahrzeugLoeschenButton);
        
        panel.add(toolbar, BorderLayout.NORTH);
        
        // Tabelle
        String[] columnNames = {"ID", "Kennzeichen", "Hersteller", "Modell", 
                                "Kategorie", "Zustand", "Tagespreis"};
        fahrzeugTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        fahrzeugTable = new JTable(fahrzeugTableModel);
        fahrzeugTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fahrzeugTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(fahrzeugTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Erstellt das Vertragsverwaltungs-Panel.
     */
    private JPanel createVertragPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.addActionListener(e -> loadVertraege());
        toolbar.add(refreshButton);
        
        JButton detailsButton = new JButton("Details anzeigen");
        detailsButton.addActionListener(e -> showVertragDetails());
        toolbar.add(detailsButton);
        
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> filterVertraege());
        toolbar.add(filterButton);
        
        panel.add(toolbar, BorderLayout.NORTH);
        
        // Tabelle
        String[] columnNames = {"Mietnummer", "Kunde", "Fahrzeug", "Startdatum", 
                                "Enddatum", "Gesamtpreis", "Status"};
        vertragTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        vertragTable = new JTable(vertragTableModel);
        vertragTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vertragTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(vertragTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Erstellt das Statistik-Panel.
     */
    private JPanel createStatistikPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Statistikkarten
        panel.add(createStatistikKarte("Gesamte Fahrzeuge", "0", new Color(52, 152, 219)));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createStatistikKarte("Aktive Verträge", "0", new Color(46, 204, 113)));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createStatistikKarte("Verfügbare Fahrzeuge", "0", new Color(241, 196, 15)));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createStatistikKarte("Registrierte Kunden", "0", new Color(155, 89, 182)));
        panel.add(Box.createVerticalGlue());
        
        // Aktualisieren-Button
        JButton refreshButton = new JButton("Statistiken aktualisieren");
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.addActionListener(e -> loadStatistiken());
        panel.add(refreshButton);
        
        return panel;
    }

    /**
     * Erstellt eine Statistikkarte.
     */
    private JPanel createStatistikKarte(String titel, String wert, Color farbe) {
        JPanel karte = new JPanel();
        karte.setLayout(new BorderLayout());
        karte.setBorder(BorderFactory.createLineBorder(farbe, 2));
        karte.setBackground(Color.WHITE);
        karte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel titelLabel = new JLabel(titel);
        titelLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titelLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        karte.add(titelLabel, BorderLayout.NORTH);
        
        JLabel wertLabel = new JLabel(wert);
        wertLabel.setFont(new Font("Arial", Font.BOLD, 28));
        wertLabel.setForeground(farbe);
        wertLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        karte.add(wertLabel, BorderLayout.CENTER);
        
        return karte;
    }

    /**
     * Lädt alle Daten.
     */
    private void loadData() {
        loadFahrzeuge();
        loadVertraege();
        loadStatistiken();
    }

    /**
     * Lädt die Fahrzeugliste.
     */
    private void loadFahrzeuge() {
        fahrzeugTableModel.setRowCount(0);
        
        try {
            List<Fahrzeug> fahrzeuge = system.getFahrzeugDao().findAll();
            
            for (Fahrzeug f : fahrzeuge) {
                Object[] row = {
                    f.getId(),
                    f.getKennzeichen(),
                    f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getHersteller() : "-",
                    f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getModellBezeichnung() : "-",
                    f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getKategorie() : "-",
                    f.getZustand(),
                    f.getFahrzeugtyp() != null ? 
                        String.format("%.2f €", f.getFahrzeugtyp().getStandardTagesPreis()) : "-"
                };
                fahrzeugTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Fahrzeuge: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lädt die Vertragsliste.
     */
    private void loadVertraege() {
        vertragTableModel.setRowCount(0);
        
        try {
            List<Mietvertrag> vertraege = system.getMietvertragDao().findAll();
            
            for (Mietvertrag v : vertraege) {
                String kundenname = v.getKunde() != null ? 
                    v.getKunde().getVorname() + " " + v.getKunde().getNachname() : "-";
                String fahrzeuginfo = v.getFahrzeug() != null ? v.getFahrzeug().getKennzeichen() : "-";
                
                Object[] row = {
                    v.getMietnummer(),
                    kundenname,
                    fahrzeuginfo,
                    v.getStartDatum(),
                    v.getEndDatum(),
                    String.format("%.2f €", v.getGesamtPreis()),
                    v.getStatus()
                };
                vertragTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Verträge: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lädt Statistiken.
     */
    private void loadStatistiken() {
        try {
            List<Fahrzeug> fahrzeuge = system.getFahrzeugDao().findAll();
            List<Mietvertrag> vertraege = system.getMietvertragDao().findAll();
            List<Kunde> kunden = system.getKundeDao().findAll();
            
            long verfuegbareFahrzeuge = fahrzeuge.stream()
                .filter(f -> f.getZustand() == FahrzeugZustand.VERFUEGBAR)
                .count();
            
            long aktiveVertraege = vertraege.stream()
                .filter(v -> v.getStatus() == VertragsStatus.LAUFEND || 
                             v.getStatus() == VertragsStatus.BESTAETIGT)
                .count();
            
            // Statistikkarten aktualisieren
            Component[] components = ((JPanel) tabbedPane.getComponentAt(2)).getComponents();
            updateStatistikKarte((JPanel) components[0], String.valueOf(fahrzeuge.size()));
            updateStatistikKarte((JPanel) components[2], String.valueOf(aktiveVertraege));
            updateStatistikKarte((JPanel) components[4], String.valueOf(verfuegbareFahrzeuge));
            updateStatistikKarte((JPanel) components[6], String.valueOf(kunden.size()));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Statistiken: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aktualisiert eine Statistikkarte.
     */
    private void updateStatistikKarte(JPanel karte, String neuerWert) {
        JLabel wertLabel = (JLabel) karte.getComponent(1);
        wertLabel.setText(neuerWert);
    }

    /**
     * Fügt ein neues Fahrzeug hinzu.
     */
    private void fahrzeugHinzufuegen() {
        // Wird durch FahrzeugPanel gehandhabt
        tabbedPane.setSelectedIndex(0);
    }

    /**
     * Bearbeitet das ausgewählte Fahrzeug.
     */
    private void fahrzeugBearbeiten() {
        int selectedRow = fahrzeugTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie ein Fahrzeug aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Wird durch FahrzeugPanel gehandhabt
        tabbedPane.setSelectedIndex(0);
    }

    /**
     * Löscht das ausgewählte Fahrzeug.
     */
    private void fahrzeugLoeschen() {
        int selectedRow = fahrzeugTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie ein Fahrzeug aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Möchten Sie dieses Fahrzeug wirklich löschen?",
            "Löschen bestätigen",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long) fahrzeugTableModel.getValueAt(selectedRow, 0);
                system.getFahrzeugDao().delete(id.intValue());
                loadFahrzeuge();
                
                JOptionPane.showMessageDialog(this,
                    "Fahrzeug erfolgreich gelöscht.",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Fehler beim Löschen: " + e.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Zeigt Details eines Vertrags.
     */
    private void showVertragDetails() {
        int selectedRow = vertragTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie einen Vertrag aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String mietnummer = (String) vertragTableModel.getValueAt(selectedRow, 0);
        
        try {
            Optional<Mietvertrag> vertragOpt = system.getMietvertragDao().findByMietnummer(mietnummer);
            
            if (vertragOpt.isPresent()) {
                Mietvertrag vertrag = vertragOpt.get();
                StringBuilder details = new StringBuilder();
                details.append("Mietnummer: ").append(vertrag.getMietnummer()).append("\n");
                details.append("Kunde: ").append(vertrag.getKunde().getVorname())
                       .append(" ").append(vertrag.getKunde().getNachname()).append("\n");
                details.append("Fahrzeug: ").append(vertrag.getFahrzeug().getKennzeichen()).append("\n");
                details.append("Zeitraum: ").append(vertrag.getStartDatum())
                       .append(" bis ").append(vertrag.getEndDatum()).append("\n");
                details.append("Gesamtpreis: ").append(String.format("%.2f €", vertrag.getGesamtPreis())).append("\n");
                details.append("Status: ").append(vertrag.getStatus()).append("\n");
                
                if (!vertrag.getZusatzoptionen().isEmpty()) {
                    details.append("\nZusatzoptionen:\n");
                    for (Zusatzoption opt : vertrag.getZusatzoptionen()) {
                        details.append("  - ").append(opt.getBezeichnung())
                               .append(" (").append(String.format("%.2f €", opt.getAufpreis())).append(")\n");
                    }
                }
                
                JOptionPane.showMessageDialog(this,
                    details.toString(),
                    "Vertragsdetails",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Details: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filtert Verträge nach Status.
     */
    private void filterVertraege() {
        String[] options = {"Alle", "ANGELEGT", "BESTAETIGT", "LAUFEND", "ABGESCHLOSSEN", "STORNIERT"};
        String selected = (String) JOptionPane.showInputDialog(this,
            "Status auswählen:",
            "Filter",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (selected != null) {
            if (selected.equals("Alle")) {
                loadVertraege();
            } else {
                try {
                    VertragsStatus status = VertragsStatus.valueOf(selected);
                    vertragTableModel.setRowCount(0);
                    
                    List<Mietvertrag> vertraege = system.getMietvertragDao().findAll();
                    for (Mietvertrag v : vertraege) {
                        if (v.getStatus() == status) {
                            String kundenname = v.getKunde() != null ? 
                                v.getKunde().getVorname() + " " + v.getKunde().getNachname() : "-";
                            String fahrzeuginfo = v.getFahrzeug() != null ? v.getFahrzeug().getKennzeichen() : "-";
                            
                            Object[] row = {
                                v.getMietnummer(),
                                kundenname,
                                fahrzeuginfo,
                                v.getStartDatum(),
                                v.getEndDatum(),
                                String.format("%.2f €", v.getGesamtPreis()),
                                v.getStatus()
                            };
                            vertragTableModel.addRow(row);
                        }
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                        "Fehler beim Filtern: " + e.getMessage(),
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Erstellt ein Fahrzeug-Icon.
     */
    private Icon createFahrzeugIcon() {
        return UIManager.getIcon("FileView.fileIcon");
    }

    /**
     * Erstellt ein Vertrag-Icon.
     */
    private Icon createVertragIcon() {
        return UIManager.getIcon("FileView.computerIcon");
    }

    /**
     * Erstellt ein Statistik-Icon.
     */
    private Icon createStatistikIcon() {
        return UIManager.getIcon("FileView.hardDriveIcon");
    }
}
