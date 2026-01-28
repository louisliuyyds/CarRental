package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.BookingController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.model.Fahrzeug;
import com.carrental.model.FahrzeugZustand;
import com.carrental.model.Kunde;
import com.carrental.model.Mietvertrag;
import com.carrental.model.Zusatzoption;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private LocalDate startDate;
    private LocalDate endDate;
    private JButton startDateButton;
    private JButton endDateButton;
    private JButton verfuegbarkeitButton;
    private JTabbedPane tabbedPane;
    private JComboBox<String> filterValueCombo;
    private List<String> availableKategorien = new ArrayList<>();

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
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        tabbedPane.addTab("Autos suchen", createFahrzeugPanel());
        tabbedPane.addTab("Meine Buchungen", createBuchungPanel());
        tabbedPane.addTab("Meine Daten", createProfilPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Erstellt das Panel für verfügbare Fahrzeuge.
     */
    private JPanel createFahrzeugPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Initialize default dates
        startDate = LocalDate.now().plusDays(1);
        endDate = LocalDate.now().plusDays(8);
        
        // Create date filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        filterPanel.setPreferredSize(new Dimension(-1, 100));
        
        JLabel startLabel = new JLabel("Startdatum:");
        startLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterPanel.add(startLabel);
        
        startDateButton = new JButton(formatDate(startDate));
        startDateButton.setFont(new Font("Arial", Font.PLAIN, 13));
        startDateButton.setPreferredSize(new Dimension(150, 30));
        startDateButton.addActionListener(e -> showDatePickerDialog(true));
        filterPanel.add(startDateButton);
        
        filterPanel.add(Box.createHorizontalStrut(20));
        
        JLabel endLabel = new JLabel("Enddatum:");
        endLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterPanel.add(endLabel);
        
        endDateButton = new JButton(formatDate(endDate));
        endDateButton.setFont(new Font("Arial", Font.PLAIN, 13));
        endDateButton.setPreferredSize(new Dimension(150, 30));
        endDateButton.addActionListener(e -> showDatePickerDialog(false));
        filterPanel.add(endDateButton);

        filterPanel.add(Box.createHorizontalStrut(20));

        // Filter UI - simplified to only Kategorie
        JLabel filterLabel = new JLabel("Kategorie:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterPanel.add(filterLabel);

        filterValueCombo = new JComboBox<>();
        filterValueCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        filterValueCombo.setPreferredSize(new Dimension(150, 30));
        filterValueCombo.addActionListener(e -> applyVehicleFilter());
        filterPanel.add(filterValueCombo);

        filterPanel.add(Box.createHorizontalStrut(20));
        
        verfuegbarkeitButton = new JButton("Autos anzeigen");
        verfuegbarkeitButton.setFont(new Font("Arial", Font.BOLD, 13));
        verfuegbarkeitButton.setBackground(new Color(70, 130, 180));
        verfuegbarkeitButton.setForeground(Color.BLACK);
        verfuegbarkeitButton.setPreferredSize(new Dimension(200, 30));
        verfuegbarkeitButton.addActionListener(e -> performAvailabilitySearch());
        filterPanel.add(verfuegbarkeitButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Tabelle für Fahrzeuge
        String[] columns = {"ID", "Hersteller", "Modell", "Kategorie", "Sitzplätze", "Tagespreis (€)"};
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
        fahrzeugTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = fahrzeugTable.getSelectedRow() != -1;
                setBuchungButtonState(selected && isSelectedFahrzeugVerfuegbar());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(fahrzeugTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        neueBuchungButton = new JButton("Fahrzeug buchen");
        neueBuchungButton.setFont(new Font("Arial", Font.BOLD, 14));
        neueBuchungButton.setForeground(Color.BLACK);
        neueBuchungButton.setToolTipText("Bitte zuerst Zeitraum wählen und Fahrzeug markieren.");
        neueBuchungButton.addActionListener(e -> openBookingDialog());
        setBuchungButtonState(false);
        buttonPanel.add(neueBuchungButton);
        
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));

        Font grosserButtonFont = new Font("Arial", Font.BOLD, 18);
        Dimension grosserButtonSize = new Dimension(180, 50);

        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.setFont(grosserButtonFont);
        refreshButton.setPreferredSize(grosserButtonSize);
        refreshButton.addActionListener(e -> loadBuchungen());
        buttonPanel.add(refreshButton);

        JButton detailsButton = new JButton("Details");
        detailsButton.setFont(grosserButtonFont);
        detailsButton.setPreferredSize(grosserButtonSize);
        detailsButton.setBackground(new Color(70, 130, 180));
        detailsButton.setForeground(Color.BLACK);
        detailsButton.addActionListener(e -> showBuchungDetails());
        buttonPanel.add(detailsButton);

        JButton fortsetzenButton = new JButton("fortsetzen");
        fortsetzenButton.setFont(grosserButtonFont);
        fortsetzenButton.setPreferredSize(grosserButtonSize);
        fortsetzenButton.setBackground(new Color(70, 130, 180));
        fortsetzenButton.setForeground(Color.BLACK);
        fortsetzenButton.addActionListener(e -> fortsetzenBuchung());
        buttonPanel.add(fortsetzenButton);

        JButton stornButton = new JButton("Stornieren");
        stornButton.setFont(grosserButtonFont);
        stornButton.setPreferredSize(grosserButtonSize);
        stornButton.setBackground(new Color(255, 99, 71));
        stornButton.setForeground(Color.BLACK);
        stornButton.addActionListener(e -> storniereBuchung());
        buttonPanel.add(stornButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProfilPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Kunde kunde = authController.getCurrentKunde();
        if (kunde == null) {
            panel.add(new JLabel("Kein Kunde angemeldet."), BorderLayout.CENTER);
            return panel;
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 24);
        Font fieldFont = new Font("Arial", Font.PLAIN, 22);

        JTextField vornameField = new JTextField(kunde.getVorname());
        JTextField nachnameField = new JTextField(kunde.getNachname());
        JTextField emailField = new JTextField(kunde.getEmail());
        JTextField strasseField = new JTextField(kunde.getStrasse());
        JTextField hausnummerField = new JTextField(kunde.getHausnummer());
        JTextField plzField = new JTextField(kunde.getPlz());
        JTextField ortField = new JTextField(kunde.getOrt());
        JTextField fuehrerscheinField = new JTextField(kunde.getFuehrerscheinNummer());

        JTextField[] fields = {vornameField, nachnameField, emailField, strasseField, hausnummerField, plzField, ortField, fuehrerscheinField};
        for (JTextField tf : fields) {
            tf.setFont(fieldFont);
            tf.setColumns(18);
        }

        LocalDate geburtstag = kunde.getGeburtstag() != null ? kunde.getGeburtstag() : LocalDate.now().minusYears(20);
        CalendarPanel geburtstagCalendar = new CalendarPanel();
        geburtstagCalendar.setSelectedDate(geburtstag);

        int row = 0;
        row = addFormRow(form, gbc, row, "Benutzername", kunde.getAccountName(), labelFont, true);
        row = addFormRow(form, gbc, row, "Vorname", vornameField, labelFont);
        row = addFormRow(form, gbc, row, "Nachname", nachnameField, labelFont);
        row = addFormRow(form, gbc, row, "E-Mail", emailField, labelFont);
        row = addFormRow(form, gbc, row, "Straße", strasseField, labelFont);
        row = addFormRow(form, gbc, row, "Hausnummer", hausnummerField, labelFont);
        row = addFormRow(form, gbc, row, "PLZ", plzField, labelFont);
        row = addFormRow(form, gbc, row, "Ort", ortField, labelFont);
        row = addFormRow(form, gbc, row, "Führerschein", fuehrerscheinField, labelFont);

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel geburtLabel = new JLabel("Geburtstag");
        geburtLabel.setFont(labelFont);
        form.add(geburtLabel, gbc);
        gbc.gridx = 1;
        form.add(geburtstagCalendar, gbc);

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton changePasswordButton = new JButton("Passwort ändern");
        changePasswordButton.setFont(new Font("Arial", Font.BOLD, 18));
        changePasswordButton.addActionListener(e -> showPasswordChangeDialog());
        actionPanel.add(changePasswordButton);

        JButton saveButton = new JButton("Speichern");
        saveButton.setFont(new Font("Arial", Font.BOLD, 18));
        saveButton.addActionListener(e -> {
            try {
                kunde.setVorname(vornameField.getText().trim());
                kunde.setNachname(nachnameField.getText().trim());
                kunde.setEmail(emailField.getText().trim());
                kunde.setStrasse(strasseField.getText().trim());
                kunde.setHausnummer(hausnummerField.getText().trim());
                kunde.setPlz(plzField.getText().trim());
                kunde.setOrt(ortField.getText().trim());
                kunde.setFuehrerscheinNummer(fuehrerscheinField.getText().trim());
                kunde.setGeburtstag(geburtstagCalendar.getSelectedDate());

                system.getKundeDao().update(kunde);
                updateWelcomeLabel();

                JOptionPane.showMessageDialog(this,
                    "Daten wurden gespeichert.",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Speichern fehlgeschlagen: " + ex.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        actionPanel.add(saveButton);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Lädt alle Daten.
     */
    private void loadData() {
        loadBuchungen();
    }

    private void updateWelcomeLabel() {
        Kunde kunde = authController.getCurrentKunde();
        String name = kunde != null ? kunde.getVorname() + " " + kunde.getNachname() : "Kunde";
        welcomeLabel.setText("Willkommen, " + name);
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "Datum wählen";
        }
        return String.format("%02d.%02d.%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    private void showDatePickerDialog(boolean isStartDate) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                     isStartDate ? "Startdatum wählen" : "Enddatum wählen", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        CalendarPanel calendar = new CalendarPanel();
        calendar.setSelectedDate(isStartDate ? startDate : endDate);
        dialog.add(calendar, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 13));
        okButton.addActionListener(e -> {
            LocalDate selectedDate = calendar.getSelectedDate();
            if (isStartDate) {
                startDate = selectedDate;
                startDateButton.setText(formatDate(startDate));
            } else {
                endDate = selectedDate;
                endDateButton.setText(formatDate(endDate));
            }
            dialog.dispose();
        });
        buttonPanel.add(okButton);
        
        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Lädt verfügbare Fahrzeuge in die Tabelle.
     */
    private void performAvailabilitySearch() {
        LocalDate[] range = getValidSelectedDates();
        if (range == null) {
            return;
        }
        loadFahrzeuge(range[0], range[1]);
    }

    private LocalDate[] getValidSelectedDates() {
        LocalDate start = startDate;
        LocalDate end = endDate;

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this,
                "Bitte gültige Start- und Enddaten wählen.",
                "Zeitraum wählen",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (!end.isAfter(start)) {
            JOptionPane.showMessageDialog(this,
                "Enddatum muss nach dem Startdatum liegen.",
                "Zeitraum prüfen",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (start.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this,
                "Startdatum darf nicht in der Vergangenheit liegen.",
                "Zeitraum prüfen",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new LocalDate[] { start, end };
    }

    private void loadFahrzeuge(LocalDate start, LocalDate end) {
        fahrzeugTableModel.setRowCount(0);
        setBuchungButtonState(false);

        List<Fahrzeug> fahrzeuge = bookingController.getVerfuegbareFahrzeugeInZeitraum(start, end);

        for (Fahrzeug f : fahrzeuge) {
            Object[] row = {
                f.getId(),
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getHersteller() : "-",
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getModellBezeichnung() : "-",
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getKategorie() : "-",
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getSitzplaetze() : "-",
                f.getFahrzeugtyp() != null ? String.format("%.2f", f.getFahrzeugtyp().getStandardTagesPreis()) : "-"
            };
            fahrzeugTableModel.addRow(row);
        }

        updateAvailableFilterOptions();
    }

    private boolean isSelectedFahrzeugVerfuegbar() {
        int selectedRow = fahrzeugTable.getSelectedRow();
        if (selectedRow == -1) {
            return false;
        }
        LocalDate[] range = getValidSelectedDates();
        if (range == null) {
            return false;
        }
        int fahrzeugId = (int) fahrzeugTableModel.getValueAt(selectedRow, 0);
        try {
            Fahrzeug fahrzeug = system.getFahrzeugDao().findById(fahrzeugId).orElse(null);
            if (fahrzeug == null) {
                return false;
            }
            return bookingController.isFahrzeugVerfuegbar(fahrzeug, range[0], range[1]);
        } catch (SQLException e) {
            return false;
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

        LocalDate[] range = getValidSelectedDates();
        if (range == null) {
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

            if (!bookingController.isFahrzeugVerfuegbar(fahrzeug, range[0], range[1])) {
                JOptionPane.showMessageDialog(this,
                    "Das ausgewählte Fahrzeug ist im gewünschten Zeitraum nicht verfügbar.",
                    "Nicht verfügbar",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            BookingDialog dialog = new BookingDialog(mainFrame, system, authController, 
                                                     bookingController, fahrzeug);
            
            // Zeitraum nach dem Anzeigen setzen
            SwingUtilities.invokeLater(() -> {
                dialog.setZeitraum(range[0], range[1]);
            });
            
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
                    com.carrental.model.VertragsStatus status = vertrag.getStatus();
                    if (status == com.carrental.model.VertragsStatus.LAUFEND ||
                        status == com.carrental.model.VertragsStatus.BESTAETIGT) {
                        JOptionPane.showMessageDialog(this,
                            "Buchung kann nicht storniert werden.\nBitte wenden Sie sich für Änderungen an den Mitarbeiter.",
                            "Stornierung nicht möglich",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean success = bookingController.buchungStornieren(vertrag);
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "Buchung erfolgreich storniert.",
                            "Erfolg",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadData();
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

    /**
     * Setzt die ausgewählte Buchung (Entwurf) fort.
     */
    private void fortsetzenBuchung() {
        int selectedRow = buchungTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie eine Buchung aus der Liste.",
                "Keine Buchung ausgewählt",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mietnummer = (String) buchungTableModel.getValueAt(selectedRow, 0);

        try {
            Mietvertrag vertrag = system.getMietvertragDao().findByMietnummer(mietnummer).orElse(null);
            if (vertrag == null) {
                JOptionPane.showMessageDialog(this,
                    "Buchung nicht gefunden.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (vertrag.getStatus() != com.carrental.model.VertragsStatus.ANGELEGT) {
                JOptionPane.showMessageDialog(this,
                    "Nur Entwürfe können fortgesetzt werden.",
                    "Kein Entwurf",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            BookingDialog dialog = BookingDialog.showDraftDialog(mainFrame, system,
                authController, bookingController, vertrag);

            if (dialog != null) {
                dialog.setVisible(true);
                loadData();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Buchung: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private int addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field, Font labelFont) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        if (labelFont != null) {
            lbl.setFont(labelFont);
        }
        form.add(lbl, gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
        return row + 1;
    }

    private int addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, String value, Font labelFont, boolean readOnly) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        if (labelFont != null) {
            lbl.setFont(labelFont);
        }
        form.add(lbl, gbc);

        gbc.gridx = 1;
        JTextField field = new JTextField(value);
        field.setFont(new Font("Arial", Font.PLAIN, 22));
        field.setColumns(18);
        field.setEditable(!readOnly);
        if (readOnly) {
            field.setBackground(new Color(240, 240, 240));
        }
        form.add(field, gbc);
        return row + 1;
    }

    private void setBuchungButtonState(boolean enabled) {
        if (neueBuchungButton == null) {
            return;
        }
        neueBuchungButton.setEnabled(enabled);
        if (enabled) {
            neueBuchungButton.setBackground(new Color(70, 130, 180));
        } else {
            neueBuchungButton.setBackground(new Color(200, 200, 200));
        }
    }

    /**
     * 更新可用的过滤选项。
     */
    private void updateAvailableFilterOptions() {
        try {
            List<Fahrzeug> fahrzeuge = bookingController.getVerfuegbareFahrzeugeInZeitraum(
                startDate != null ? startDate : LocalDate.now().plusDays(1),
                endDate != null ? endDate : LocalDate.now().plusDays(8));

            availableKategorien.clear();
            Set<String> kategorieSet = new HashSet<>();

            for (Fahrzeug f : fahrzeuge) {
                if (f.getFahrzeugtyp() != null) {
                    kategorieSet.add(f.getFahrzeugtyp().getKategorie());
                }
            }

            availableKategorien.addAll(kategorieSet);
            Collections.sort(availableKategorien);

            updateFilterValues();
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Filteroptionen: " + e.getMessage());
        }
    }

    /**
     * 根据选择的过滤类型更新过滤值下拉框。
     */
    private void updateFilterValues() {
        filterValueCombo.removeAllItems();
        filterValueCombo.addItem("Alle");
        for (String kategorie : availableKategorien) {
            filterValueCombo.addItem(kategorie);
        }
    }

    /**
     * 应用车辆过滤。
     */
    private void applyVehicleFilter() {
        String filterValue = (String) filterValueCombo.getSelectedItem();

        if (filterValue == null || filterValue.equals("Alle")) {
            performAvailabilitySearch();
            return;
        }

        LocalDate[] range = getValidSelectedDates();
        if (range == null) {
            return;
        }

        List<Fahrzeug> filteredFahrzeuge = bookingController.getVerfuegbareFahrzeugeInZeitraum(range[0], range[1]);
        fahrzeugTableModel.setRowCount(0);

        for (Fahrzeug f : filteredFahrzeuge) {
            if (f.getFahrzeugtyp() == null) {
                continue;
            }

            if (filterValue.equals(f.getFahrzeugtyp().getKategorie())) {
                Object[] row = {
                    f.getId(),
                    f.getFahrzeugtyp().getHersteller(),
                    f.getFahrzeugtyp().getModellBezeichnung(),
                    f.getFahrzeugtyp().getKategorie(),
                    f.getFahrzeugtyp().getSitzplaetze(),
                    String.format("%.2f", f.getFahrzeugtyp().getStandardTagesPreis())
                };
                fahrzeugTableModel.addRow(row);
            }
        }

        setBuchungButtonState(false);
    }

    /**
     * 显示密码修改对话框。
     */
    private void showPasswordChangeDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Passwort ändern", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        int row = 0;

        JLabel titleLabel = new JLabel("Passwort ändern");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        dialog.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel newLabel = new JLabel("Neues Passwort:");
        newLabel.setFont(labelFont);
        dialog.add(newLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField newPasswordField = new JPasswordField(40);
        newPasswordField.setFont(fieldFont);
        newPasswordField.setMinimumSize(new Dimension(250, 25));
        newPasswordField.setPreferredSize(new Dimension(280, 30));
        dialog.add(newPasswordField, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel confirmLabel = new JLabel("Passwort bestätigen:");
        confirmLabel.setFont(labelFont);
        dialog.add(confirmLabel, gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(40);
        confirmPasswordField.setFont(fieldFont);
        confirmPasswordField.setMinimumSize(new Dimension(250, 25));
        confirmPasswordField.setPreferredSize(new Dimension(280, 30));
        dialog.add(confirmPasswordField, gbc);

        row++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 16));
        okButton.addActionListener(e -> {
            char[] newPass = newPasswordField.getPassword();
            char[] confirmPass = confirmPasswordField.getPassword();

            String newPassword = new String(newPass);
            String confirmPassword = new String(confirmPass);

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Das Passwort darf nicht leer sein.",
                    "Eingabefehler",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog,
                    "Die Passwörter stimmen nicht überein.",
                    "Eingabefehler",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Kunde kunde = authController.getCurrentKunde();
                kunde.setPasswort(newPassword);
                system.getKundeDao().update(kunde);

                JOptionPane.showMessageDialog(dialog,
                    "Passwort erfolgreich geändert.",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();

                Arrays.fill(newPass, '\0');
                Arrays.fill(confirmPass, '\0');
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Fehler beim Ändern des Passworts: " + ex.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setSize(500, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * 显示Buchungdetails。
     */
    private void showBuchungDetails() {
        int selectedRow = buchungTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie eine Buchung aus der Liste.",
                "Keine Buchung ausgewählt",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mietnummer = (String) buchungTableModel.getValueAt(selectedRow, 0);

        try {
            Optional<Mietvertrag> vertragOpt = system.getMietvertragDao().findByMietnummer(mietnummer);

            if (vertragOpt.isPresent()) {
                Mietvertrag vertrag = vertragOpt.get();

                JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Vertragsdetails", true);
                dialog.setLayout(new BorderLayout(20, 20));
                dialog.setSize(800, 600);
                dialog.setLocationRelativeTo(this);

                JPanel contentPanel = new JPanel();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

                StringBuilder details = new StringBuilder();
                details.append("<html><style>");
                details.append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; }");
                details.append(".title { font-size: 24px; font-weight: bold; color: #000000; margin-bottom: 20px; }");
                details.append(".section-title { font-size: 18px; font-weight: bold; color: #003366; margin-top: 20px; margin-bottom: 10px; border-bottom: 2px solid #003366; padding-bottom: 5px; }");
                details.append(".info { font-size: 14px; margin: 5px 0; color: #000000; }");
                details.append(".info-label { font-weight: bold; color: #333333; }");
                details.append(".price-highlight { font-size: 16px; font-weight: bold; color: #006633; }");
                details.append(".total-price { font-size: 24px; font-weight: bold; color: #CC0000; margin-top: 25px; }");
                details.append("</style>");

                details.append("<div class='title'>Vertragsnummer: ").append(vertrag.getMietnummer()).append("</div>");

                details.append("<div class='section-title'>Kunde</div>");
                details.append("<div class='info'><span class='info-label'>Kunden-ID:</span>     ").append(vertrag.getKunde().getId()).append("</div>");
                details.append("<div class='info'><span class='info-label'>Vorname:</span>       ").append(vertrag.getKunde().getVorname()).append("</div>");
                details.append("<div class='info'><span class='info-label'>Nachname:</span>      ").append(vertrag.getKunde().getNachname()).append("</div>");
                details.append("<div class='info'><span class='info-label'>Email:</span>         ").append(vertrag.getKunde().getEmail()).append("</div>");

                details.append("<div class='section-title'>Fahrzeug</div>");
                details.append("<div class='info'><span class='info-label'>Kennzeichen:</span>  ").append(vertrag.getFahrzeug().getKennzeichen()).append("</div>");

                // DEBUG OUTPUT - 检查Fahrzeugtyp信息
                System.out.println("DEBUG Vertragsdetails:");
                System.out.println("  Mietnummer: " + vertrag.getMietnummer());
                System.out.println("  Fahrzeug Kennzeichen: " + vertrag.getFahrzeug().getKennzeichen());
                System.out.println("  Fahrzeugtyp: " + (vertrag.getFahrzeug().getFahrzeugtyp() != null ? "NOT NULL" : "NULL"));
                if (vertrag.getFahrzeug().getFahrzeugtyp() != null) {
                    com.carrental.model.Fahrzeugtyp typ = vertrag.getFahrzeug().getFahrzeugtyp();
                    System.out.println("    Hersteller: " + typ.getHersteller());
                    System.out.println("    Modell: " + typ.getModellBezeichnung());
                    System.out.println("    Kategorie: " + typ.getKategorie());
                    System.out.println("    Antriebsart: " + typ.getAntriebsart());
                    System.out.println("    Sitzplätze: " + typ.getSitzplaetze());
                    System.out.println("    Tagespreis: " + typ.getStandardTagesPreis());
                }
                // END DEBUG OUTPUT

                if (vertrag.getFahrzeug().getFahrzeugtyp() != null) {
                    com.carrental.model.Fahrzeugtyp typ = vertrag.getFahrzeug().getFahrzeugtyp();
                    details.append("<div class='info'><span class='info-label'>Hersteller:</span>   ").append(typ.getHersteller()).append("</div>");
                    details.append("<div class='info'><span class='info-label'>Modell:</span>       ").append(typ.getModellBezeichnung()).append("</div>");
                    details.append("<div class='info'><span class='info-label'>Kategorie:</span>    ").append(typ.getKategorie()).append("</div>");
                    details.append("<div class='info'><span class='info-label'>Antriebsart:</span>  ").append(typ.getAntriebsart()).append("</div>");
                    details.append("<div class='info'><span class='info-label'>Sitzplätze:</span>   ").append(typ.getSitzplaetze()).append("</div>");
                    details.append("<div class='info price-highlight'><span class='info-label'>Tagespreis:</span>    ").append(String.format("%.2f €", typ.getStandardTagesPreis())).append("</div>");
                }

                details.append("<div class='section-title'>Mietzeitraum</div>");
                details.append("<div class='info'><span class='info-label'>Startdatum:</span>   ").append(vertrag.getStartDatum()).append("</div>");
                details.append("<div class='info'><span class='info-label'>Enddatum:</span>     ").append(vertrag.getEndDatum()).append("</div>");

                details.append("<div class='section-title'>Zusatzoptionen</div>");
                if (!vertrag.getZusatzoptionen().isEmpty()) {
                    for (Zusatzoption opt : vertrag.getZusatzoptionen()) {
                        details.append("<div class='info'>- ").append(opt.getBezeichnung())
                               .append(" (").append(String.format("%.2f €", opt.getAufpreis())).append(")</div>");
                    }
                } else {
                    details.append("<div class='info'>Keine Zusatzoptionen gewählt</div>");
                }

                details.append("<div class='section-title'>Status</div>");
                details.append("<div class='info' style='font-size: 16px; font-weight: bold;'>").append(vertrag.getStatus()).append("</div>");

                details.append("<div class='total-price'>Gesamtpreis: ").append(String.format("%.2f €", vertrag.getGesamtPreis())).append("</div>");

                details.append("</html>");

                JLabel label = new JLabel(details.toString());
                label.setFont(new Font("Arial", Font.PLAIN, 14));

                JScrollPane scrollPane = new JScrollPane(label);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                contentPanel.add(scrollPane);

                JButton closeButton = new JButton("Schließen");
                closeButton.setFont(new Font("Arial", Font.BOLD, 16));
                closeButton.setPreferredSize(new Dimension(140, 45));
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
}
