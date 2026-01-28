package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.BookingController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.model.Fahrzeug;
import com.carrental.model.FahrzeugZustand;
import com.carrental.model.Kunde;
import com.carrental.model.Mietvertrag;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
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
    private LocalDate startDate;
    private LocalDate endDate;
    private JButton startDateButton;
    private JButton endDateButton;
    private JButton verfuegbarkeitButton;
    private JTabbedPane tabbedPane;

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

        tabbedPane.addTab("Verfügbare Fahrzeuge", createFahrzeugPanel());
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
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
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
        
        verfuegbarkeitButton = new JButton("Verfügbarkeit anzeigen");
        verfuegbarkeitButton.setFont(new Font("Arial", Font.BOLD, 13));
        verfuegbarkeitButton.setBackground(new Color(70, 130, 180));
        verfuegbarkeitButton.setForeground(Color.BLACK);
        verfuegbarkeitButton.setPreferredSize(new Dimension(200, 30));
        verfuegbarkeitButton.addActionListener(e -> performAvailabilitySearch());
        filterPanel.add(verfuegbarkeitButton);

        panel.add(filterPanel, BorderLayout.NORTH);
        
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

        JButton fortsetzenButton = new JButton("Buchung fortsetzen");
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

        panel.add(form, BorderLayout.CENTER);

        JButton saveButton = new JButton("Speichern");
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

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
            FahrzeugZustand anzeigeZustand = FahrzeugZustand.VERFUEGBAR;
            Object[] row = {
                f.getId(),
                f.getKennzeichen(),
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getHersteller() : "-",
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getModellBezeichnung() : "-",
                f.getFahrzeugtyp() != null ? f.getFahrzeugtyp().getKategorie() : "-",
                f.getFahrzeugtyp() != null ? String.format("%.2f", f.getFahrzeugtyp().getStandardTagesPreis()) : "-",
                anzeigeZustand
            };
            fahrzeugTableModel.addRow(row);
        }
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
}
