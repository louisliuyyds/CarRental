package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
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
    private DefaultTableModel kundenTableModel;
    private JTable kundenTable;
    private FahrzeugPanel fahrzeugPanel;
    private final com.carrental.controller.BookingController bookingController;
    
    /**
     * Konstruktor für das Mitarbeiter-Dashboard.
     */
    public MitarbeiterDashboard(CarRentalSystem system, AuthController authController) {
        this.system = system;
        this.authController = authController;
        this.bookingController = new com.carrental.controller.BookingController(system);
        
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
        headerLabel.setFont(new Font("Arial", Font.BOLD, 40));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        JButton logoutButton = new JButton("Abmelden");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
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
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        
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

        // Tab 4: Nutzerverwaltung
        JPanel nutzerverwaltungPanel = createNutzerverwaltungPanel();
        tabbedPane.addTab("Nutzerverwaltung", createUserIcon(),
            nutzerverwaltungPanel, "Benutzerverwaltung - Kunden verwalten");

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
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.addActionListener(e -> loadVertraege());
        toolbar.add(refreshButton);
        
        JButton detailsButton = new JButton("Details anzeigen");
        detailsButton.setFont(new Font("Arial", Font.BOLD, 16));
        detailsButton.addActionListener(e -> showVertragDetails());
        toolbar.add(detailsButton);
        
        JButton filterButton = new JButton("Filter");
        filterButton.setFont(new Font("Arial", Font.BOLD, 16));
        filterButton.addActionListener(e -> filterVertraege());
        toolbar.add(filterButton);

        JButton statusButton = new JButton("Status ändern");
        statusButton.setFont(new Font("Arial", Font.BOLD, 16));
        statusButton.addActionListener(e -> changeVertragStatus());
        toolbar.add(statusButton);
        
        panel.add(toolbar, BorderLayout.NORTH);
        
        // Tabelle
        String[] columnNames = {"Kunden-ID", "Mietnummer", "Kunde", "Fahrzeug", "Startdatum",
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
        vertragTable.setRowHeight(35);
        vertragTable.setFont(new Font("Arial", Font.PLAIN, 14));
        vertragTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
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
        JPanel fahrzeugeKarte = createClickableStatistikKarte("Gesamte Fahrzeuge", "0", new Color(52, 152, 219));
        fahrzeugeKarte.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                tabbedPane.setSelectedIndex(0);
                fahrzeugPanel.selectFahrzeugeTab();
            }
        });
        panel.add(fahrzeugeKarte);
        panel.add(Box.createVerticalStrut(15));

        JPanel vertraegeKarte = createClickableStatistikKarte("Aktive Verträge", "0", new Color(46, 204, 113));
        vertraegeKarte.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                tabbedPane.setSelectedIndex(1);
                SwingUtilities.invokeLater(() -> applyAktiveVertraegeFilter());
            }
        });
        panel.add(vertraegeKarte);
        panel.add(Box.createVerticalStrut(15));

        JPanel verfuegbareKarte = createClickableStatistikKarte("Verfügbare Fahrzeuge", "0", new Color(241, 196, 15));
        verfuegbareKarte.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                tabbedPane.setSelectedIndex(0);
                fahrzeugPanel.selectFahrzeugeTab();
                SwingUtilities.invokeLater(() -> fahrzeugPanel.filterByVerfuegbar());
            }
        });
        panel.add(verfuegbareKarte);
        panel.add(Box.createVerticalStrut(15));

        JPanel kundenKarte = createClickableStatistikKarte("Registrierte Kunden", "0", new Color(155, 89, 182));
        kundenKarte.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                tabbedPane.setSelectedIndex(3);
            }
        });
        panel.add(kundenKarte);
        panel.add(Box.createVerticalGlue());

        // Aktualisieren-Button
        JButton refreshButton = new JButton("Statistiken aktualisieren");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.addActionListener(e -> loadStatistiken());
        panel.add(refreshButton);

        return panel;
    }

    /**
     * Erstellt das Nutzerverwaltungs-Panel.
     */
    private JPanel createNutzerverwaltungPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.addActionListener(e -> loadKunden());
        toolbar.add(refreshButton);

        JButton detailsButton = new JButton("Details anzeigen");
        detailsButton.setFont(new Font("Arial", Font.BOLD, 16));
        detailsButton.addActionListener(e -> showKundeDetails());
        toolbar.add(detailsButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Tabelle
        String[] columnNames = {"ID", "Kundennummer", "Vorname", "Nachname", "Email",
                                "Straße", "Hausnummer", "PLZ", "Ort", "Geburtstag",
                                "Führerschein", "Aktiv"};
        kundenTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        kundenTable = new JTable(kundenTableModel);
        kundenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        kundenTable.getTableHeader().setReorderingAllowed(false);
        kundenTable.setRowHeight(35);
        kundenTable.setFont(new Font("Arial", Font.PLAIN, 14));
        kundenTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(kundenTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Erstellt eine Statistikkarte.
     */
    private JPanel createStatistikKarte(String titel, String wert, Color farbe) {
        JPanel karte = new JPanel();
        karte.setLayout(new BorderLayout());
        karte.setBorder(BorderFactory.createLineBorder(farbe, 3));
        karte.setBackground(Color.WHITE);
        karte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JLabel titelLabel = new JLabel(titel);
        titelLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        titelLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        karte.add(titelLabel, BorderLayout.NORTH);

        JLabel wertLabel = new JLabel(wert);
        wertLabel.setFont(new Font("Arial", Font.BOLD, 56));
        wertLabel.setForeground(farbe);
        wertLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        karte.add(wertLabel, BorderLayout.CENTER);

        return karte;
    }

    /**
     * Erstellt eine klickbare Statistikkarte.
     */
    private JPanel createClickableStatistikKarte(String titel, String wert, Color farbe) {
        JPanel karte = createStatistikKarte(titel, wert, farbe);
        karte.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return karte;
    }

    /**
     * Filtert aktive Verträge.
     */
    private void applyAktiveVertraegeFilter() {
        applyFilter("LAUFEND");
    }

    /**
     * Lädt alle Daten.
     */
    private void loadData() {
        if (fahrzeugPanel != null) {
            fahrzeugPanel.refreshData();
        }
        loadVertraege();
        loadKunden();
        loadStatistiken();
    }

    /**
     * Lädt die Kundenliste.
     */
    private void loadKunden() {
        kundenTableModel.setRowCount(0);

        try {
            List<Kunde> kunden = system.getKundeDao().findAll();

            for (Kunde k : kunden) {
                Object[] row = {
                    k.getId(),
                    k.getKundennummer(),
                    k.getVorname(),
                    k.getNachname(),
                    k.getEmail(),
                    k.getStrasse(),
                    k.getHausnummer(),
                    k.getPlz(),
                    k.getOrt(),
                    k.getGeburtstag(),
                    k.getFuehrerscheinNummer(),
                    k.isIstAktiv() ? "Ja" : "Nein"
                };
                kundenTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Kunden: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Zeigt Details eines Kunden.
     */
    private void showKundeDetails() {
        int selectedRow = kundenTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie einen Kunden aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) kundenTableModel.getValueAt(selectedRow, 0);

        try {
            Optional<Kunde> kundeOpt = system.getKundeDao().findById(id);

            if (kundeOpt.isPresent()) {
                Kunde kunde = kundeOpt.get();
                StringBuilder details = new StringBuilder();
                details.append("Kunden-ID: ").append(kunde.getId()).append("\n");
                details.append("Kundennummer: ").append(kunde.getKundennummer()).append("\n");
                details.append("Name: ").append(kunde.getVorname()).append(" ").append(kunde.getNachname()).append("\n");
                details.append("Email: ").append(kunde.getEmail()).append("\n");
                details.append("\nAdresse:\n");
                details.append("  Straße:     ").append(kunde.getStrasse()).append("\n");
                details.append("  Hausnummer: ").append(kunde.getHausnummer()).append("\n");
                details.append("  PLZ:        ").append(kunde.getPlz()).append("\n");
                details.append("  Ort:        ").append(kunde.getOrt()).append("\n");
                details.append("\nGeburtstag:     ").append(kunde.getGeburtstag()).append("\n");
                details.append("Führerschein:  ").append(kunde.getFuehrerscheinNummer()).append("\n");
                details.append("Konto aktiv:    ").append(kunde.isIstAktiv() ? "Ja" : "Nein").append("\n");

                JOptionPane.showMessageDialog(this,
                    details.toString(),
                    "Kundendetails",
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
     * Lädt die Fahrzeugliste.
     */
    private void loadFahrzeuge() {
        if (fahrzeugTableModel == null) {
            return; // FahrzeugPanel handles its own tables; safeguard legacy call path
        }
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
                String kundenId = v.getKunde() != null ? String.valueOf(v.getKunde().getId()) : "-";
                String kundenname = v.getKunde() != null ? 
                    v.getKunde().getVorname() + " " + v.getKunde().getNachname() : "-";
                String fahrzeuginfo = v.getFahrzeug() != null ? v.getFahrzeug().getKennzeichen() : "-";
                
                Object[] row = {
                    kundenId,
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
        tabbedPane.setSelectedIndex(0);
        SwingUtilities.invokeLater(() -> fahrzeugPanel.editSelectedFahrzeug());
    }

    /**
     * Ändert den Zustand des ausgewählten Fahrzeugs.
     */
    private void fahrzeugZustandAendern() {
        tabbedPane.setSelectedIndex(0);
        SwingUtilities.invokeLater(() -> fahrzeugPanel.changeSelectedZustand());
    }

    /**
     * Löscht das ausgewählte Fahrzeug.
     */
    private void fahrzeugLoeschen() {
        tabbedPane.setSelectedIndex(0);
        SwingUtilities.invokeLater(() -> fahrzeugPanel.deleteSelectedFahrzeug());
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

        String mietnummer = (String) vertragTableModel.getValueAt(selectedRow, 1);

        try {
            Optional<Mietvertrag> vertragOpt = system.getMietvertragDao().findByMietnummer(mietnummer);

            if (vertragOpt.isPresent()) {
                Mietvertrag vertrag = vertragOpt.get();

                JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Vertragsdetails", true);
                dialog.setLayout(new BorderLayout(20, 20));
                dialog.setSize(1000, 800);
                dialog.setLocationRelativeTo(this);

                JPanel contentPanel = new JPanel();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

                StringBuilder details = new StringBuilder();
                details.append("<html><style>body{font-family:Arial,sans-serif;}</style>");

                details.append("<h1 style='margin-bottom:20px;'>Vertragsnummer: ").append(vertrag.getMietnummer()).append("</h1>");

                details.append("<h2 style='margin-top:20px; margin-bottom:10px;'>Kunde</h2>");
                details.append("<p style='margin:5px 0;'>Kunden-ID:     <b>").append(String.format("%20s", vertrag.getKunde().getId())).append("</b></p>");
                details.append("<p style='margin:5px 0;'>Vorname:       <b>").append(String.format("%20s", vertrag.getKunde().getVorname())).append("</b></p>");
                details.append("<p style='margin:5px 0;'>Nachname:      <b>").append(String.format("%20s", vertrag.getKunde().getNachname())).append("</b></p>");

                details.append("<h2 style='margin-top:20px; margin-bottom:10px;'>Fahrzeug</h2>");
                details.append("<p style='margin:5px 0;'>Kennzeichen:  <b>").append(String.format("%15s", vertrag.getFahrzeug().getKennzeichen())).append("</b></p>");
                if (vertrag.getFahrzeug().getFahrzeugtyp() != null) {
                    com.carrental.model.Fahrzeugtyp typ = vertrag.getFahrzeug().getFahrzeugtyp();
                    details.append("<p style='margin:5px 0;'>Hersteller:   <b>").append(String.format("%15s", typ.getHersteller())).append("</b></p>");
                    details.append("<p style='margin:5px 0;'>Modell:       <b>").append(String.format("%15s", typ.getModellBezeichnung())).append("</b></p>");
                    details.append("<p style='margin:5px 0;'>Kategorie:    <b>").append(String.format("%15s", typ.getKategorie())).append("</b></p>");
                    details.append("<p style='margin:5px 0;'>Antriebsart:  <b>").append(String.format("%15s", typ.getAntriebsart())).append("</b></p>");
                    details.append("<p style='margin:5px 0;'>Sitzplätze:   <b>").append(String.format("%15s", typ.getSitzplaetze())).append("</b></p>");
                    details.append("<p style='margin:5px 0;'>Tagespreis:   <b>").append(String.format("%15s", String.format("%.2f €", typ.getStandardTagesPreis()))).append("</b></p>");
                }

                details.append("<h2 style='margin-top:20px; margin-bottom:10px;'>Mietzeitraum</h2>");
                details.append("<p style='margin:5px 0;'>Startdatum:   <b>").append(String.format("%15s", vertrag.getStartDatum())).append("</b></p>");
                details.append("<p style='margin:5px 0;'>Enddatum:     <b>").append(String.format("%15s", vertrag.getEndDatum())).append("</b></p>");

                details.append("<h2 style='margin-top:20px; margin-bottom:10px;'>Zusatzoptionen</h2>");
                if (!vertrag.getZusatzoptionen().isEmpty()) {
                    for (Zusatzoption opt : vertrag.getZusatzoptionen()) {
                        details.append("<p style='margin:5px 0;'>- ").append(opt.getBezeichnung())
                               .append(" (").append(String.format("%.2f €", opt.getAufpreis())).append(")</p>");
                    }
                } else {
                    details.append("<p style='margin:5px 0;'>Keine Zusatzoptionen gewählt</p>");
                }

                details.append("<h2 style='margin-top:20px; margin-bottom:10px;'>Status</h2>");
                details.append("<p style='margin:5px 0; font-size: 24px;'>").append(vertrag.getStatus()).append("</p>");

                details.append("<h1 style='margin-top:30px; margin-bottom:10px; color: #462048;'>Gesamtpreis: ").append(String.format("%.2f €", vertrag.getGesamtPreis())).append("</h1>");

                details.append("</html>");

                JLabel label = new JLabel(details.toString());
                label.setFont(DETAIL_VALUE_FONT);

                JScrollPane scrollPane = new JScrollPane(label);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                contentPanel.add(scrollPane);

                JButton closeButton = new JButton("Schließen");
                closeButton.setFont(new Font("Arial", Font.BOLD, 18));
                closeButton.setPreferredSize(new Dimension(150, 50));
                closeButton.addActionListener(e -> dialog.dispose());

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.add(closeButton);

                dialog.add(contentPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);

                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Details: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final Font GROSSE_SCHRIFT = new Font("Arial", Font.PLAIN, 18);
    private static final Font GROSSE_LABEL_SCHRIFT = new Font("Arial", Font.BOLD, 20);
    private static final Font DETAIL_TITEL_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Font DETAIL_LABEL_FONT = new Font("Arial", Font.PLAIN, 32);
    private static final Font DETAIL_VALUE_FONT = new Font("Arial", Font.PLAIN, 32);
    
    /**
     * Filtert Verträge nach Status.
     */
    private void filterVertraege() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Verträge filtern", true);
        dialog.setLayout(new BorderLayout(20, 20));
        
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel statusLabel = new JLabel("Status auswählen:");
        statusLabel.setFont(GROSSE_LABEL_SCHRIFT);
        contentPanel.add(statusLabel, BorderLayout.NORTH);
        
        String[] options = {"Alle", "ANGELEGT", "BESTAETIGT", "LAUFEND", "ABGESCHLOSSEN", "STORNIERT"};
        JList<String> statusList = new JList<>(options);
        statusList.setFont(GROSSE_SCHRIFT);
        statusList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statusList.setSelectedIndex(0);
        statusList.setFixedCellHeight(35);
        
        JScrollPane listScroll = new JScrollPane(statusList);
        listScroll.setPreferredSize(new Dimension(250, 200));
        contentPanel.add(listScroll, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Dimension buttonSize = new Dimension(140, 45);
        
        JButton okButton = new JButton("OK");
        okButton.setFont(buttonFont);
        okButton.setPreferredSize(buttonSize);
        okButton.addActionListener(e -> {
            String selected = statusList.getSelectedValue();
            dialog.dispose();
            applyFilter(selected);
        });
        buttonPanel.add(okButton);
        
        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.setFont(buttonFont);
        cancelButton.setPreferredSize(buttonSize);
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void applyFilter(String selected) {
        if (selected == null) return;

        if (selected.equals("Alle")) {
            loadVertraege();
        } else {
            try {
                VertragsStatus status = VertragsStatus.valueOf(selected);
                vertragTableModel.setRowCount(0);

                List<Mietvertrag> vertraege = system.getMietvertragDao().findAll();
                for (Mietvertrag v : vertraege) {
                    if (v.getStatus() == status) {
                        String kundenId = v.getKunde() != null ? String.valueOf(v.getKunde().getId()) : "-";
                        String kundenname = v.getKunde() != null ?
                            v.getKunde().getVorname() + " " + v.getKunde().getNachname() : "-";
                        String fahrzeuginfo = v.getFahrzeug() != null ? v.getFahrzeug().getKennzeichen() : "-";

                        Object[] row = {
                            kundenId,
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

    /**
     * Ändert den Status des ausgewählten Mietvertrags.
     */
    private void changeVertragStatus() {
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
            if (vertragOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vertrag nicht gefunden.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Mietvertrag vertrag = vertragOpt.get();
            if (vertrag.getFahrzeug() == null) {
                JOptionPane.showMessageDialog(this,
                    "Dem Vertrag ist kein Fahrzeug zugeordnet.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            panel.add(new JLabel("Vertragsstatus:"), gbc);

            gbc.gridx = 1;
            VertragsStatus[] statusOptions = VertragsStatus.values();
            JComboBox<VertragsStatus> statusCombo = new JComboBox<>(statusOptions);
            statusCombo.setSelectedItem(vertrag.getStatus());
            panel.add(statusCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Fahrzeugstatus:"), gbc);

            gbc.gridx = 1;
            JComboBox<FahrzeugZustand> zustandCombo = new JComboBox<>(FahrzeugZustand.values());
            zustandCombo.setSelectedItem(vertrag.getFahrzeug().getZustand());
            panel.add(zustandCombo, gbc);

            int result = JOptionPane.showConfirmDialog(this,
                panel,
                "Status ändern",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            VertragsStatus selectedStatus = (VertragsStatus) statusCombo.getSelectedItem();
            FahrzeugZustand selectedZustand = (FahrzeugZustand) zustandCombo.getSelectedItem();
            if (selectedStatus == null || selectedZustand == null) {
                return;
            }

            if (isAktiverVertragStatus(selectedStatus)) {
                if (hasVertragskonflikt(vertrag)) {
                    JOptionPane.showMessageDialog(this,
                        "Der Vertrag überschneidet sich mit einem anderen aktiven Vertrag für dieses Fahrzeug.",
                        "Konflikt",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Aktiver Vertrag => Fahrzeug muss vermietet sein
                selectedZustand = FahrzeugZustand.VERMIETET;
            } else {
                // Inaktive Verträge: Fahrzeug nur dann verfügbar setzen, wenn keine aktiven Verträge existieren
                if (!hasAndereAktiveVertraege(vertrag)) {
                    if (selectedZustand != FahrzeugZustand.WARTUNG) {
                        selectedZustand = FahrzeugZustand.VERFUEGBAR;
                    }
                } else {
                    selectedZustand = FahrzeugZustand.VERMIETET;
                }
            }

            vertrag.setStatus(selectedStatus);
            system.getMietvertragDao().update(vertrag);

            vertrag.getFahrzeug().setZustand(selectedZustand);
            system.getFahrzeugDao().updateStatusAndKilometerstand(vertrag.getFahrzeug());

            // Optional: Benachrichtigung für Kunden
            if (vertrag.getKunde() != null) {
                system.addNotification(vertrag.getKunde().getId(),
                    "Status Ihres Mietvertrags " + vertrag.getMietnummer() + " wurde geändert zu: " + selectedStatus);
            }

            loadVertraege();
            if (fahrzeugPanel != null) {
                fahrzeugPanel.refreshFahrzeuge();
            }
            JOptionPane.showMessageDialog(this,
                "Status erfolgreich geändert.",
                "Erfolg",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Aktualisieren: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isAktiverVertragStatus(VertragsStatus status) {
        return status == VertragsStatus.BESTAETIGT || status == VertragsStatus.LAUFEND;
    }

    private boolean hasVertragskonflikt(Mietvertrag zielVertrag) throws SQLException {
        List<Mietvertrag> vertraege = system.getMietvertragDao().findAll();
        for (Mietvertrag v : vertraege) {
            if (v.getId() == zielVertrag.getId()) {
                continue;
            }
            if (v.getFahrzeug() == null || zielVertrag.getFahrzeug() == null) {
                continue;
            }
            if (v.getFahrzeug().getId() == zielVertrag.getFahrzeug().getId() &&
                isAktiverVertragStatus(v.getStatus())) {
                if (hasDateOverlap(zielVertrag.getStartDatum(), zielVertrag.getEndDatum(),
                                   v.getStartDatum(), v.getEndDatum())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAndereAktiveVertraege(Mietvertrag zielVertrag) throws SQLException {
        List<Mietvertrag> vertraege = system.getMietvertragDao().findAll();
        for (Mietvertrag v : vertraege) {
            if (v.getId() == zielVertrag.getId()) {
                continue;
            }
            if (v.getFahrzeug() == null || zielVertrag.getFahrzeug() == null) {
                continue;
            }
            if (v.getFahrzeug().getId() == zielVertrag.getFahrzeug().getId() &&
                isAktiverVertragStatus(v.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDateOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !end1.isBefore(start2);
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
     * Statistik-Icon.
     */
    private Icon createStatistikIcon() {
        return UIManager.getIcon("FileView.floppyDriveIcon");
    }

    /**
     * Benutzer-Icon.
     */
    private Icon createUserIcon() {
        return UIManager.getIcon("FileView.fileIcon");
    }

    /**
     * Creates a dialog to filter contracts by date range using calendar date choosers.
     */
    public void showDateFilterDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
            "Verträge nach Zeitraum filtern", true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Start date
        JLabel startLabel = new JLabel("Startdatum:");
        startLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(startLabel);
        CalendarDateChooser startChooser = new CalendarDateChooser(
            LocalDate.now().minusDays(30), null);
        contentPanel.add(startChooser);
        contentPanel.add(Box.createVerticalStrut(20));

        // End date
        JLabel endLabel = new JLabel("Enddatum:");
        endLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(endLabel);
        CalendarDateChooser endChooser = new CalendarDateChooser(
            LocalDate.now().plusDays(30), null);
        contentPanel.add(endChooser);
        contentPanel.add(Box.createVerticalStrut(20));

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton applyButton = new JButton("Anwenden");
        applyButton.setFont(new Font("Arial", Font.BOLD, 14));
        applyButton.addActionListener(e -> {
            // Filter logic can be implemented here
            dialog.dispose();
        });
        buttonPanel.add(applyButton);

        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(600, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
