package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.controller.BookingController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.model.Fahrzeug;
import com.carrental.model.Mietvertrag;
import com.carrental.model.Zusatzoption;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dialog für die Erstellung einer neuen Buchung.
 * Ermöglicht die Vorschau des Vertrags und die Auswahl von Zusatzoptionen.
 */
public class BookingDialog extends JDialog {

    private final CarRentalSystem system;
    private final AuthController authController;
    private final BookingController bookingController;
    private final Fahrzeug fahrzeug;
    
    private JLabel startDatumLabel;
    private JLabel endDatumLabel;
    private JList<String> zusatzoptionList;
    private DefaultListModel<String> zusatzoptionListModel;
    private JLabel preisLabel;
    private JButton buchenButton;
    
    private List<Zusatzoption> verfuegbareOptionen;
    private Set<Integer> ausgewaehlteOptionen;
    private double berechneterPreis = 0.0;
    private LocalDate startDatum;
    private LocalDate endDatum;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Font GROSSE_SCHRIFT = new Font("Arial", Font.PLAIN, 18);
    private static final Font GROSSE_TITEL_SCHRIFT = new Font("Arial", Font.BOLD, 22);
    private static final Font GROSSER_BUTTON_SCHRIFT = new Font("Arial", Font.BOLD, 16);

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
        this.ausgewaehlteOptionen = new HashSet<>();
        
        initializeUI();
        loadZusatzoptionen();
        
        pack();
        setSize(650, 700);
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
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        String fahrzeugInfo = String.format("%s %s (%s)", 
            fahrzeug.getFahrzeugtyp() != null ? fahrzeug.getFahrzeugtyp().getHersteller() : "",
            fahrzeug.getFahrzeugtyp() != null ? fahrzeug.getFahrzeugtyp().getModellBezeichnung() : "",
            fahrzeug.getKennzeichen());
        
        JLabel headerLabel = new JLabel("Vertragsvorschau: " + fahrzeugInfo);
        headerLabel.setFont(GROSSE_TITEL_SCHRIFT);
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Formular
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setBackground(Color.WHITE);
        
        // Fahrzeugdetails
        JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Fahrzeugdetails"));
        detailsPanel.setBackground(Color.WHITE);
        
        JLabel kategorieLabel = new JLabel("Kategorie: " + (fahrzeug.getFahrzeugtyp() != null ? 
            fahrzeug.getFahrzeugtyp().getKategorie() : "-"));
        kategorieLabel.setFont(GROSSE_SCHRIFT);
        detailsPanel.add(kategorieLabel);
        
        JLabel preisLabelInit = new JLabel("Tagespreis: " + (fahrzeug.getFahrzeugtyp() != null ? 
            String.format("%.2f €", fahrzeug.getFahrzeugtyp().getStandardTagesPreis()) : "-"));
        preisLabelInit.setFont(GROSSE_SCHRIFT);
        detailsPanel.add(preisLabelInit);
        
        JLabel antriebLabel = new JLabel("Antrieb: " + (fahrzeug.getFahrzeugtyp() != null && 
            fahrzeug.getFahrzeugtyp().getAntriebsart() != null ? 
            fahrzeug.getFahrzeugtyp().getAntriebsart().toString() : "-"));
        antriebLabel.setFont(GROSSE_SCHRIFT);
        detailsPanel.add(antriebLabel);
        
        formPanel.add(detailsPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Zeitraum (nur Anzeige, nicht editierbar)
        JPanel zeitraumPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        zeitraumPanel.setBorder(BorderFactory.createTitledBorder("Mietzeitraum"));
        zeitraumPanel.setBackground(Color.WHITE);
        
        startDatumLabel = new JLabel("Startdatum: -");
        startDatumLabel.setFont(GROSSE_SCHRIFT);
        zeitraumPanel.add(startDatumLabel);
        
        endDatumLabel = new JLabel("Enddatum: -");
        endDatumLabel.setFont(GROSSE_SCHRIFT);
        zeitraumPanel.add(endDatumLabel);
        
        formPanel.add(zeitraumPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Zusatzoptionen
        JPanel optionenPanel = new JPanel(new BorderLayout());
        optionenPanel.setBorder(BorderFactory.createTitledBorder("Zusatzoptionen (optional)"));
        optionenPanel.setBackground(Color.WHITE);
        
        zusatzoptionListModel = new DefaultListModel<>();
        zusatzoptionList = new JList<>(zusatzoptionListModel);
        zusatzoptionList.setFont(GROSSE_SCHRIFT);
        zusatzoptionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        zusatzoptionList.setFixedCellHeight(30);
        
        JScrollPane optionenScroll = new JScrollPane(zusatzoptionList);
        optionenScroll.setPreferredSize(new Dimension(450, 120));
        optionenPanel.add(optionenScroll, BorderLayout.CENTER);
        
        // Buttons für Zusatzoptionen
        JPanel optionenButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        optionenButtonPanel.setBackground(Color.WHITE);
        
        JButton auswaehlenButton = new JButton("Auswählen");
        auswaehlenButton.setFont(GROSSER_BUTTON_SCHRIFT);
        auswaehlenButton.addActionListener(e -> zusatzoptionAuswaehlen());
        optionenButtonPanel.add(auswaehlenButton);
        
        JButton abwaehlenButton = new JButton("Abwählen");
        abwaehlenButton.setFont(GROSSER_BUTTON_SCHRIFT);
        abwaehlenButton.addActionListener(e -> auswahlAbwaehlen());
        optionenButtonPanel.add(abwaehlenButton);
        
        optionenPanel.add(optionenButtonPanel, BorderLayout.SOUTH);
        
        formPanel.add(optionenPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Preis
        JPanel preisPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        preisPanel.setBorder(BorderFactory.createTitledBorder("Gesamtpreis"));
        preisPanel.setBackground(Color.WHITE);
        
        preisLabel = new JLabel("Bitte Zeitraum wählen");
        preisLabel.setFont(new Font("Arial", Font.BOLD, 22));
        preisPanel.add(preisLabel);
        
        formPanel.add(preisPanel);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 15, 20, 15));

        buchenButton = new JButton("Jetzt buchen");
        buchenButton.setFont(GROSSER_BUTTON_SCHRIFT);
        buchenButton.setBackground(new Color(70, 130, 180));
        buchenButton.setForeground(Color.BLACK);
        buchenButton.setEnabled(false);
        buchenButton.setPreferredSize(new Dimension(180, 45));
        buchenButton.addActionListener(e -> buchungDurchfuehren());
        buttonPanel.add(buchenButton);
        
        JButton abbrechenButton = new JButton("Abbrechen");
        abbrechenButton.setFont(GROSSER_BUTTON_SCHRIFT);
        abbrechenButton.setPreferredSize(new Dimension(130, 45));
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
                String display = String.format("%s (+%.2f € / Tag)",
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
     * Wählt eine Zusatzoption aus.
     */
    private void zusatzoptionAuswaehlen() {
        int selectedIndex = zusatzoptionList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie eine Zusatzoption aus der Liste aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!ausgewaehlteOptionen.contains(selectedIndex)) {
            ausgewaehlteOptionen.add(selectedIndex);
            String currentText = zusatzoptionListModel.get(selectedIndex);
            if (!currentText.startsWith("✓ ")) {
                zusatzoptionListModel.set(selectedIndex, "✓ " + currentText);
            }
            updatePreis();
        }
    }

    /**
     * Hebt die Auswahl einer Zusatzoption auf.
     */
    private void auswahlAbwaehlen() {
        int selectedIndex = zusatzoptionList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie eine Zusatzoption aus der Liste aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (ausgewaehlteOptionen.remove(selectedIndex)) {
            String currentText = zusatzoptionListModel.get(selectedIndex);
            if (currentText.startsWith("✓ ")) {
                zusatzoptionListModel.set(selectedIndex, currentText.substring(2));
            }
            updatePreis();
        }
    }

    /**
     * Aktualisiert den Gesamtpreis.
     */
    private void updatePreis() {
        if (preisLabel == null) {
            return;
        }
        
        if (startDatum == null || endDatum == null) {
            preisLabel.setText("Bitte Zeitraum wählen");
            buchenButton.setEnabled(false);
            return;
        }

        if (!endDatum.isAfter(startDatum)) {
            preisLabel.setText("Enddatum nach Startdatum wählen");
            buchenButton.setEnabled(false);
            return;
        }

        if (startDatum.isBefore(LocalDate.now())) {
            preisLabel.setText("Startdatum darf nicht in der Vergangenheit liegen");
            buchenButton.setEnabled(false);
            return;
        }

        if (!bookingController.isFahrzeugVerfuegbar(fahrzeug, startDatum, endDatum)) {
            preisLabel.setText("Im Zeitraum nicht verfügbar");
            buchenButton.setEnabled(false);
            return;
        }

        Mietvertrag tempVertrag = new Mietvertrag(authController.getCurrentKunde(),
            fahrzeug, startDatum, endDatum);

        for (Integer index : ausgewaehlteOptionen) {
            if (index < verfuegbareOptionen.size()) {
                tempVertrag.addZusatzoption(verfuegbareOptionen.get(index));
            }
        }

        berechneterPreis = bookingController.calculateGesamtpreis(tempVertrag);

        long tage = ChronoUnit.DAYS.between(startDatum, endDatum);
        preisLabel.setText(String.format("%.2f € (%d Tage)", berechneterPreis, tage));
        buchenButton.setEnabled(true);
    }

    /**
     * Führt die Buchung durch.
     */
    private void buchungDurchfuehren() {
        if (startDatum == null || endDatum == null) {
            JOptionPane.showMessageDialog(this,
                "Bitte gültigen Zeitraum wählen.",
                "Zeitraum prüfen",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Zusatzoption> optionen = new ArrayList<>();
        for (Integer index : ausgewaehlteOptionen) {
            if (index < verfuegbareOptionen.size()) {
                optionen.add(verfuegbareOptionen.get(index));
            }
        }

        Mietvertrag vertrag = bookingController.buchungErstellen(
            authController.getCurrentKunde(),
            fahrzeug,
            startDatum,
            endDatum,
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

    /**
     * Setzt den Mietzeitraum.
     */
    public void setZeitraum(LocalDate start, LocalDate end) {
        this.startDatum = start;
        this.endDatum = end;
        
        if (start != null) {
            startDatumLabel.setText("Startdatum: " + start.format(DATE_FORMATTER));
        }
        if (end != null) {
            endDatumLabel.setText("Enddatum: " + end.format(DATE_FORMATTER));
        }
        
        updatePreis();
    }
}
