package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.BookingController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.model.Fahrzeug;
import com.carrental.model.Mietvertrag;
import com.carrental.model.Zusatzoption;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog für die Erstellung einer neuen Buchung.
 * Ermöglicht die Auswahl von Zeitraum und Zusatzoptionen.
 */
public class BookingDialog extends JDialog {

    private final CarRentalSystem system;
    private final AuthController authController;
    private final BookingController bookingController;
    private final Fahrzeug fahrzeug;
    
    private JTextField startDatumField;
    private JTextField endDatumField;
    private JList<String> zusatzoptionList;
    private DefaultListModel<String> zusatzoptionListModel;
    private JLabel preisLabel;
    private JButton berechnenButton;
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
        
        zeitraumPanel.add(new JLabel("Startdatum (YYYY-MM-DD):"));
        startDatumField = new JTextField(LocalDate.now().plusDays(1).toString());
        zeitraumPanel.add(startDatumField);
        
        zeitraumPanel.add(new JLabel("Enddatum (YYYY-MM-DD):"));
        endDatumField = new JTextField(LocalDate.now().plusDays(8).toString());
        zeitraumPanel.add(endDatumField);
        
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
        
        berechnenButton = new JButton("Preis berechnen");
        berechnenButton.setFont(new Font("Arial", Font.PLAIN, 13));
        berechnenButton.addActionListener(e -> berechnePreis());
        buttonPanel.add(berechnenButton);
        
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
     * Berechnet den Gesamtpreis.
     */
    private void berechnePreis() {
        try {
            LocalDate start = LocalDate.parse(startDatumField.getText().trim());
            LocalDate end = LocalDate.parse(endDatumField.getText().trim());
            
            // Validierung
            if (end.isBefore(start) || end.isEqual(start)) {
                JOptionPane.showMessageDialog(this,
                    "Enddatum muss nach dem Startdatum liegen.",
                    "Ungültiger Zeitraum",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (start.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this,
                    "Startdatum darf nicht in der Vergangenheit liegen.",
                    "Ungültiger Zeitraum",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Verfügbarkeit prüfen
            if (!bookingController.isFahrzeugVerfuegbar(fahrzeug, start, end)) {
                JOptionPane.showMessageDialog(this,
                    "Fahrzeug ist im gewählten Zeitraum nicht verfügbar.",
                    "Nicht verfügbar",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Temporären Vertrag erstellen für Preisberechnung
            Mietvertrag tempVertrag = new Mietvertrag(authController.getCurrentKunde(), 
                                                       fahrzeug, start, end);
            
            // Ausgewählte Zusatzoptionen hinzufügen
            int[] selectedIndices = zusatzoptionList.getSelectedIndices();
            for (int index : selectedIndices) {
                tempVertrag.addZusatzoption(verfuegbareOptionen.get(index));
            }
            
            // Preis berechnen
            berechneterPreis = bookingController.calculateGesamtpreis(tempVertrag);
            
            long tage = ChronoUnit.DAYS.between(start, end) + 1;
            preisLabel.setText(String.format("%.2f € (%d Tage)", berechneterPreis, tage));
            
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Ungültiges Datumsformat. Verwenden Sie YYYY-MM-DD.",
                "Eingabefehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Führt die Buchung durch.
     */
    private void buchungDurchfuehren() {
        if (berechneterPreis == 0.0) {
            JOptionPane.showMessageDialog(this,
                "Bitte berechnen Sie zuerst den Preis.",
                "Preis nicht berechnet",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            LocalDate start = LocalDate.parse(startDatumField.getText().trim());
            LocalDate end = LocalDate.parse(endDatumField.getText().trim());
            
            // Ausgewählte Zusatzoptionen sammeln
            List<Zusatzoption> optionen = new ArrayList<>();
            int[] selectedIndices = zusatzoptionList.getSelectedIndices();
            for (int index : selectedIndices) {
                optionen.add(verfuegbareOptionen.get(index));
            }
            
            // Buchung erstellen
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
            
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Ungültiges Datumsformat. Verwenden Sie YYYY-MM-DD.",
                "Eingabefehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
