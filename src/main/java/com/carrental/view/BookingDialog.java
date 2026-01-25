package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.BookingController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.model.Fahrzeug;
import com.carrental.model.Mietvertrag;
import com.carrental.model.Zusatzoption;

import javax.swing.*;
import javax.swing.SpinnerDateModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.event.ChangeListener;

/**
 * Dialog für die Erstellung einer neuen Buchung.
 * Ermöglicht die Auswahl von Zeitraum und Zusatzoptionen.
 */
public class BookingDialog extends JDialog {

    private final CarRentalSystem system;
    private final AuthController authController;
    private final BookingController bookingController;
    private final Fahrzeug fahrzeug;
    
    private JSpinner startDatumSpinner;
    private JSpinner endDatumSpinner;
    private JList<String> zusatzoptionList;
    private DefaultListModel<String> zusatzoptionListModel;
    private JLabel preisLabel;
    private JButton buchenButton;
    private JButton abbrechenButton;
    
    private List<Zusatzoption> verfuegbareOptionen;
    private double berechneterPreis = 0.0;

    /**
     * Konstruktor für den Buchungsdialog.
     */
    public BookingDialog(JFrame parent, CarRentalSystem system, AuthController authController,
                         BookingController bookingController, Fahrzeug fahrzeug) {
        super(parent, "Fahrzeug buchen", true);
        
        this.system = system;
        this.authController = authController;
        this.bookingController = bookingController;
        this.fahrzeug = fahrzeug;
        
        initializeUI();
        loadZusatzoptionen();
        installAutoRecalcListeners();
        updatePreisUndVerfuegbarkeit();
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
    }

    /**
     * Initialisiert die Benutzeroberfläche.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String fahrzeugInfo = String.format("%s %s (%s)", 
            fahrzeug.getFahrzeugtyp() != null ? fahrzeug.getFahrzeugtyp().getHersteller() : "",
            fahrzeug.getFahrzeugtyp() != null ? fahrzeug.getFahrzeugtyp().getModellBezeichnung() : "",
            fahrzeug.getKennzeichen());
        
        JLabel headerLabel = new JLabel("Buchung: " + fahrzeugInfo);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Formular
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Fahrzeugdetails
        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Fahrzeugdetails"));
        
        detailsPanel.add(new JLabel("Kategorie:"));
        detailsPanel.add(new JLabel(fahrzeug.getFahrzeugtyp() != null ? 
            fahrzeug.getFahrzeugtyp().getKategorie() : "-"));
        
        detailsPanel.add(new JLabel("Tagespreis:"));
        detailsPanel.add(new JLabel(fahrzeug.getFahrzeugtyp() != null ? 
            String.format("%.2f €", fahrzeug.getFahrzeugtyp().getStandardTagesPreis()) : "-"));
        
        detailsPanel.add(new JLabel("Antrieb:"));
        detailsPanel.add(new JLabel(fahrzeug.getFahrzeugtyp() != null && 
            fahrzeug.getFahrzeugtyp().getAntriebsart() != null ? 
            fahrzeug.getFahrzeugtyp().getAntriebsart().toString() : "-"));
        
        formPanel.add(detailsPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Zeitraum
        JPanel zeitraumPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        zeitraumPanel.setBorder(BorderFactory.createTitledBorder("Mietzeitraum"));

        zeitraumPanel.add(new JLabel("Startdatum:"));
        startDatumSpinner = createDateSpinner(LocalDate.now().plusDays(1));
        zeitraumPanel.add(startDatumSpinner);

        zeitraumPanel.add(new JLabel("Enddatum:"));
        endDatumSpinner = createDateSpinner(LocalDate.now().plusDays(8));
        zeitraumPanel.add(endDatumSpinner);
        
        formPanel.add(zeitraumPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Zusatzoptionen
        JPanel optionenPanel = new JPanel(new BorderLayout());
        optionenPanel.setBorder(BorderFactory.createTitledBorder("Zusatzoptionen (optional)"));
        
        zusatzoptionListModel = new DefaultListModel<>();
        zusatzoptionList = new JList<>(zusatzoptionListModel);
        zusatzoptionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane optionenScroll = new JScrollPane(zusatzoptionList);
        optionenScroll.setPreferredSize(new Dimension(400, 100));
        optionenPanel.add(optionenScroll, BorderLayout.CENTER);
        
        formPanel.add(optionenPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Preis
        JPanel preisPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        preisPanel.setBorder(BorderFactory.createTitledBorder("Gesamtpreis"));
        
        preisLabel = new JLabel("Bitte Preis berechnen");
        preisLabel.setFont(new Font("Arial", Font.BOLD, 16));
        preisPanel.add(preisLabel);
        
        formPanel.add(preisPanel);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        buchenButton = new JButton("Jetzt buchen");
        buchenButton.setFont(new Font("Arial", Font.BOLD, 13));
        buchenButton.setBackground(new Color(70, 130, 180));
        buchenButton.setForeground(Color.WHITE);
        buchenButton.addActionListener(e -> buchungDurchfuehren());
        buttonPanel.add(buchenButton);
        
        abbrechenButton = new JButton("Abbrechen");
        abbrechenButton.addActionListener(e -> dispose());
        buttonPanel.add(abbrechenButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Lädt verfügbare Zusatzoptionen.
     */
    private void loadZusatzoptionen() {
        try {
            verfuegbareOptionen = system.getZusatzoptionDao().findAll();

            for (Zusatzoption option : verfuegbareOptionen) {
                String display = String.format("%s (%.2f € / Tag)",
                    option.getBezeichnung(), option.getAufpreis());
                zusatzoptionListModel.addElement(display);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Zusatzoptionen: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aktualisiert Preis und Verfügbarkeit automatisch, sobald sich Zeitraum oder Optionen ändern.
     */
    private void updatePreisUndVerfuegbarkeit() {
        LocalDate start = getDate(startDatumSpinner);
        LocalDate end = getDate(endDatumSpinner);

        if (start == null || end == null) {
            preisLabel.setText("Bitte Zeitraum wählen");
            buchenButton.setEnabled(false);
            return;
        }

        if (!end.isAfter(start)) {
            preisLabel.setText("Enddatum nach Startdatum wählen");
            buchenButton.setEnabled(false);
            return;
        }

        if (start.isBefore(LocalDate.now())) {
            preisLabel.setText("Startdatum darf nicht in der Vergangenheit liegen");
            buchenButton.setEnabled(false);
            return;
        }

        if (!bookingController.isFahrzeugVerfuegbar(fahrzeug, start, end)) {
            preisLabel.setText("Im Zeitraum nicht verfügbar");
            buchenButton.setEnabled(false);
            return;
        }

        Mietvertrag tempVertrag = new Mietvertrag(authController.getCurrentKunde(),
            fahrzeug, start, end);

        int[] selectedIndices = zusatzoptionList.getSelectedIndices();
        for (int index : selectedIndices) {
            tempVertrag.addZusatzoption(verfuegbareOptionen.get(index));
        }

        berechneterPreis = bookingController.calculateGesamtpreis(tempVertrag);

        long tage = ChronoUnit.DAYS.between(start, end) + 1;
        preisLabel.setText(String.format("%.2f € (%d Tage)", berechneterPreis, tage));
        buchenButton.setEnabled(true);
    }

    /**
     * Führt die Buchung durch.
     */
    private void buchungDurchfuehren() {
        updatePreisUndVerfuegbarkeit();
        if (!buchenButton.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                "Bitte gültigen Zeitraum wählen (verfügbar) bevor Sie buchen.",
                "Zeitraum prüfen",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate start = getDate(startDatumSpinner);
        LocalDate end = getDate(endDatumSpinner);

        List<Zusatzoption> optionen = new ArrayList<>();
        int[] selectedIndices = zusatzoptionList.getSelectedIndices();
        for (int index : selectedIndices) {
            optionen.add(verfuegbareOptionen.get(index));
        }

        Mietvertrag vertrag = bookingController.buchungErstellen(
            authController.getCurrentKunde(),
            fahrzeug,
            start,
            end,
            optionen
        );

        if (vertrag != null) {
            JOptionPane.showMessageDialog(this,
                String.format("Buchung erfolgreich!\n\nMietnummer: %s\nGesamtpreis: %.2f €",
                    vertrag.getMietnummer(), vertrag.getGesamtPreis()),
                "Buchung erfolgreich",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Buchung konnte nicht erstellt werden. Bitte versuchen Sie es erneut.",
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JSpinner createDateSpinner(LocalDate initialDate) {
        Date initial = Date.from(initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel model = new SpinnerDateModel(initial, null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(10);
        return spinner;
    }

    private LocalDate getDate(JSpinner spinner) {
        Object value = spinner.getValue();
        if (value instanceof Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    private void installAutoRecalcListeners() {
        ChangeListener changeListener = e -> updatePreisUndVerfuegbarkeit();
        startDatumSpinner.addChangeListener(changeListener);
        endDatumSpinner.addChangeListener(changeListener);
        zusatzoptionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreisUndVerfuegbarkeit();
            }
        });
    }

    public void setZeitraum(LocalDate start, LocalDate end) {
        if (start != null) {
            startDatumSpinner.setValue(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (end != null) {
            endDatumSpinner.setValue(Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        updatePreisUndVerfuegbarkeit();
    }
}
