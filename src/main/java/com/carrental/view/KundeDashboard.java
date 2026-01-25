package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.BookingController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.model.Fahrzeug;
import com.carrental.model.Kunde;
import com.carrental.model.Mietvertrag;

import javax.swing.*;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
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
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
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

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        filterPanel.add(new JLabel("Start:"));
        startDateSpinner = createDateSpinner(LocalDate.now().plusDays(1));
        filterPanel.add(startDateSpinner);

        filterPanel.add(new JLabel("Ende:"));
        endDateSpinner = createDateSpinner(LocalDate.now().plusDays(8));
        filterPanel.add(endDateSpinner);

        verfuegbarkeitButton = new JButton("Verfügbarkeit anzeigen");
        verfuegbarkeitButton.addActionListener(e -> performAvailabilitySearch());
        filterPanel.add(verfuegbarkeitButton);

        JLabel hintLabel = new JLabel("Bitte zuerst Zeitraum wählen, dann Fahrzeug wählen.");
        hintLabel.setForeground(Color.DARK_GRAY);
        filterPanel.add(hintLabel);

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
                setBuchungButtonState(selected);
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
        JSpinner geburtstagSpinner = createDateSpinner(geburtstag);

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
        JComponent spinnerComp = geburtstagSpinner.getEditor();
        if (spinnerComp instanceof JSpinner.DefaultEditor editor) {
            editor.getTextField().setFont(fieldFont);
        }
        form.add(geburtstagSpinner, gbc);

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
                kunde.setGeburtstag(getDate(geburtstagSpinner));

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
        LocalDate start = getDate(startDateSpinner);
        LocalDate end = getDate(endDateSpinner);

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
            
            BookingDialog dialog = new BookingDialog(mainFrame, system, authController, 
                                                     bookingController, fahrzeug);
            dialog.setZeitraum(range[0], range[1]);
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

    private JSpinner createDateSpinner(LocalDate initial) {
        Date date = Date.from(initial.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel model = new SpinnerDateModel(date, null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(9);
        ChangeListener changeListener = e -> setBuchungButtonState(false);
        spinner.addChangeListener(changeListener);
        return spinner;
    }

    private LocalDate getDate(JSpinner spinner) {
        Object value = spinner.getValue();
        if (value instanceof Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
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
