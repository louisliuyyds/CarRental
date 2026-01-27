package com.carrental.view;

import com.carrental.controller.CarRentalSystem;
import com.carrental.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Locale;
import java.util.List;
import java.util.Optional;

/**
 * Panel für die Verwaltung von Fahrzeugen und Fahrzeugtypen.
 * Ermöglicht das Hinzufügen, Bearbeiten und Löschen von Fahrzeugen.
 */
public class FahrzeugPanel extends JPanel {

    private final CarRentalSystem system;
    
    private JTabbedPane tabbedPane;
    
    // Fahrzeuge Tab
    private DefaultTableModel fahrzeugTableModel;
    private JTable fahrzeugTable;
    private JButton editFahrzeugButton;
    private JButton deleteFahrzeugButton;
    private JButton zustandFahrzeugButton;
    
    // Fahrzeugtypen Tab
    private DefaultTableModel fahrzeugtypTableModel;
    private JTable fahrzeugtypTable;

    /**
     * Konstruktor für das Fahrzeugverwaltungs-Panel.
     */
    public FahrzeugPanel(CarRentalSystem system) {
        this.system = system;
        
        initializeUI();
        loadData();
    }

    /**
     * Initialisiert die Benutzeroberfläche.
     */
    private static final Font GROSSE_TAB_SCHRIFT = new Font("Arial", Font.BOLD, 18);
    private static final Font GROSSE_BUTTON_SCHRIFT = new Font("Arial", Font.BOLD, 16);
    private static final Font GROSSE_DIALOG_SCHRIFT = new Font("Arial", Font.PLAIN, 16);
    private static final Font GROSSE_LABEL_SCHRIFT = new Font("Arial", Font.BOLD, 16);
    private static final Font GROSSE_FIELD_SCHRIFT = new Font("Arial", Font.PLAIN, 16);
    private static final Dimension GROSSE_BUTTON_GROESSE = new Dimension(160, 45);
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(GROSSE_TAB_SCHRIFT);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        
        // Tab 1: Fahrzeuge
        JPanel fahrzeugePanel = createFahrzeugePanel();
        tabbedPane.addTab("Fahrzeuge", fahrzeugePanel);
        
        // Tab 2: Fahrzeugtypen
        JPanel fahrzeugtypPanel = createFahrzeugtypPanel();
        tabbedPane.addTab("Fahrzeugtypen", fahrzeugtypPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Erstellt das Fahrzeuge-Panel.
     */
    private JPanel createFahrzeugePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.setFont(GROSSE_BUTTON_SCHRIFT);
        refreshButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        refreshButton.addActionListener(e -> loadFahrzeuge());
        toolbar.add(refreshButton);
        
        JButton addButton = new JButton("Hinzufügen");
        addButton.setFont(GROSSE_BUTTON_SCHRIFT);
        addButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(e -> addFahrzeug());
        toolbar.add(addButton);
        
        editFahrzeugButton = new JButton("Bearbeiten");
        editFahrzeugButton.setFont(GROSSE_BUTTON_SCHRIFT);
        editFahrzeugButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        editFahrzeugButton.addActionListener(e -> editFahrzeug());
        toolbar.add(editFahrzeugButton);
        
        deleteFahrzeugButton = new JButton("Löschen");
        deleteFahrzeugButton.setFont(GROSSE_BUTTON_SCHRIFT);
        deleteFahrzeugButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        deleteFahrzeugButton.addActionListener(e -> deleteFahrzeug());
        toolbar.add(deleteFahrzeugButton);
        
        zustandFahrzeugButton = new JButton("Zustand ändern");
        zustandFahrzeugButton.setFont(GROSSE_BUTTON_SCHRIFT);
        zustandFahrzeugButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        zustandFahrzeugButton.addActionListener(e -> changeZustand());
        toolbar.add(zustandFahrzeugButton);
        
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
        fahrzeugTable.setRowHeight(40);
        fahrzeugTable.setFont(GROSSE_FIELD_SCHRIFT);
        fahrzeugTable.getTableHeader().setFont(GROSSE_LABEL_SCHRIFT);
        fahrzeugTable.getSelectionModel().addListSelectionListener(e -> updateFahrzeugButtonState());

        updateFahrzeugButtonState();
        
        JScrollPane scrollPane = new JScrollPane(fahrzeugTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void updateFahrzeugButtonState() {
        boolean hasSelection = fahrzeugTable != null && fahrzeugTable.getSelectedRow() != -1;
        if (editFahrzeugButton != null) editFahrzeugButton.setEnabled(hasSelection);
        if (deleteFahrzeugButton != null) deleteFahrzeugButton.setEnabled(hasSelection);
        if (zustandFahrzeugButton != null) zustandFahrzeugButton.setEnabled(hasSelection);

        if (!hasSelection) {
            if (editFahrzeugButton != null) editFahrzeugButton.setToolTipText("Bitte zuerst ein Fahrzeug auswählen.");
            if (deleteFahrzeugButton != null) deleteFahrzeugButton.setToolTipText("Bitte zuerst ein Fahrzeug auswählen.");
            if (zustandFahrzeugButton != null) zustandFahrzeugButton.setToolTipText("Bitte zuerst ein Fahrzeug auswählen.");
        } else {
            if (editFahrzeugButton != null) editFahrzeugButton.setToolTipText(null);
            if (deleteFahrzeugButton != null) deleteFahrzeugButton.setToolTipText(null);
            if (zustandFahrzeugButton != null) zustandFahrzeugButton.setToolTipText(null);
        }
    }

    /**
     * Erstellt das Fahrzeugtypen-Panel.
     */
    private JPanel createFahrzeugtypPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.setFont(GROSSE_BUTTON_SCHRIFT);
        refreshButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        refreshButton.addActionListener(e -> loadFahrzeugtypen());
        toolbar.add(refreshButton);
        
        JButton addButton = new JButton("Hinzufügen");
        addButton.setFont(GROSSE_BUTTON_SCHRIFT);
        addButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(e -> addFahrzeugtyp());
        toolbar.add(addButton);
        
        JButton editButton = new JButton("Bearbeiten");
        editButton.setFont(GROSSE_BUTTON_SCHRIFT);
        editButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        editButton.addActionListener(e -> editFahrzeugtyp());
        toolbar.add(editButton);
        
        JButton deleteButton = new JButton("Löschen");
        deleteButton.setFont(GROSSE_BUTTON_SCHRIFT);
        deleteButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        deleteButton.addActionListener(e -> deleteFahrzeugtyp());
        toolbar.add(deleteButton);
        
        panel.add(toolbar, BorderLayout.NORTH);
        
        // Tabelle
        String[] columnNames = {"ID", "Hersteller", "Modell", "Kategorie", 
                                "Antriebsart", "Sitzplätze", "Tagespreis"};
        fahrzeugtypTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        fahrzeugtypTable = new JTable(fahrzeugtypTableModel);
        fahrzeugtypTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fahrzeugtypTable.getTableHeader().setReorderingAllowed(false);
        fahrzeugtypTable.setRowHeight(40);
        fahrzeugtypTable.setFont(GROSSE_FIELD_SCHRIFT);
        fahrzeugtypTable.getTableHeader().setFont(GROSSE_LABEL_SCHRIFT);
        
        JScrollPane scrollPane = new JScrollPane(fahrzeugtypTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Lädt alle Daten.
     */
    private void loadData() {
        loadFahrzeuge();
        loadFahrzeugtypen();
    }

    /**
     * Public refresher used by embedding dashboards.
     */
    public void refreshData() {
        loadData();
    }

    /**
     * Refreshes only the vehicle list.
     */
    public void refreshFahrzeuge() {
        loadFahrzeuge();
    }

    /**
     * Refreshes only the vehicle type list.
     */
    public void refreshFahrzeugtypen() {
        loadFahrzeugtypen();
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
     * Lädt die Fahrzeugtypenliste.
     */
    private void loadFahrzeugtypen() {
        fahrzeugtypTableModel.setRowCount(0);
        
        try {
            List<Fahrzeugtyp> typen = system.getFahrzeugDao().findAllFahrzeugtypen();
            
            for (Fahrzeugtyp t : typen) {
                Object[] row = {
                    t.getId(),
                    t.getHersteller(),
                    t.getModellBezeichnung(),
                    t.getKategorie(),
                    t.getAntriebsart(),
                    t.getSitzplaetze(),
                    String.format("%.2f €", t.getStandardTagesPreis())
                };
                fahrzeugtypTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Fahrzeugtypen: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fügt ein neues Fahrzeug hinzu.
     */
    private void addFahrzeug() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                     "Fahrzeug hinzufügen", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Kennzeichen
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel kennzeichenLabel = new JLabel("Kennzeichen:");
        kennzeichenLabel.setFont(GROSSE_LABEL_SCHRIFT);
        dialog.add(kennzeichenLabel, gbc);
        
        gbc.gridx = 1;
        JTextField kennzeichenField = new JTextField(20);
        kennzeichenField.setFont(GROSSE_FIELD_SCHRIFT);
        dialog.add(kennzeichenField, gbc);
        
        // Fahrzeugtyp
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel typLabel = new JLabel("Fahrzeugtyp:");
        typLabel.setFont(GROSSE_LABEL_SCHRIFT);
        dialog.add(typLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<Fahrzeugtyp> typCombo = new JComboBox<>();
        typCombo.setFont(GROSSE_FIELD_SCHRIFT);
        try {
            List<Fahrzeugtyp> typen = system.getFahrzeugDao().findAllFahrzeugtypen();
            for (Fahrzeugtyp t : typen) {
                typCombo.addItem(t);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog,
                "Fehler beim Laden der Fahrzeugtypen: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
        typCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                         int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Fahrzeugtyp) {
                    Fahrzeugtyp t = (Fahrzeugtyp) value;
                    setText(t.getHersteller() + " " + t.getModellBezeichnung());
                }
                return this;
            }
        });
        dialog.add(typCombo, gbc);
        
        // Zustand
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel zustandLabel = new JLabel("Zustand:");
        zustandLabel.setFont(GROSSE_LABEL_SCHRIFT);
        dialog.add(zustandLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<FahrzeugZustand> zustandCombo = new JComboBox<>(FahrzeugZustand.values());
        zustandCombo.setFont(GROSSE_FIELD_SCHRIFT);
        dialog.add(zustandCombo, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton saveButton = new JButton("Speichern");
        saveButton.setFont(GROSSE_BUTTON_SCHRIFT);
        saveButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        saveButton.addActionListener(e -> {
            String kennzeichen = kennzeichenField.getText().trim();
            Fahrzeugtyp typ = (Fahrzeugtyp) typCombo.getSelectedItem();
            FahrzeugZustand zustand = (FahrzeugZustand) zustandCombo.getSelectedItem();
            
            if (kennzeichen.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Bitte geben Sie ein Kennzeichen ein.",
                    "Eingabefehler",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (typ == null) {
                JOptionPane.showMessageDialog(dialog,
                    "Bitte wählen Sie einen Fahrzeugtyp aus.",
                    "Eingabefehler",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                Fahrzeug fahrzeug = new Fahrzeug(kennzeichen, typ, zustand);
                system.getFahrzeugDao().save(fahrzeug);
                
                loadFahrzeuge();
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this,
                    "Fahrzeug erfolgreich hinzugefügt.",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Fehler beim Speichern: " + ex.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);
        
        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.setFont(GROSSE_BUTTON_SCHRIFT);
        cancelButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Bearbeitet das ausgewählte Fahrzeug.
     */
    private void editFahrzeug() {
        int selectedRow = fahrzeugTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie ein Fahrzeug aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = ((Number) fahrzeugTableModel.getValueAt(selectedRow, 0)).intValue();
        
        try {
            Optional<Fahrzeug> fahrzeugOpt = system.getFahrzeugDao().findById(id);
            if (!fahrzeugOpt.isPresent()) {
                JOptionPane.showMessageDialog(this,
                    "Fahrzeug nicht gefunden.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Fahrzeug fahrzeug = fahrzeugOpt.get();
            
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                         "Fahrzeug bearbeiten", true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(12, 15, 12, 15);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Kennzeichen
            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel kennzeichenLabel = new JLabel("Kennzeichen:");
            kennzeichenLabel.setFont(GROSSE_LABEL_SCHRIFT);
            dialog.add(kennzeichenLabel, gbc);
            
            gbc.gridx = 1;
            JTextField kennzeichenField = new JTextField(fahrzeug.getKennzeichen(), 20);
            kennzeichenField.setFont(GROSSE_FIELD_SCHRIFT);
            dialog.add(kennzeichenField, gbc);
            
            // Zustand
            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel zustandLabel = new JLabel("Zustand:");
            zustandLabel.setFont(GROSSE_LABEL_SCHRIFT);
            dialog.add(zustandLabel, gbc);
            
            gbc.gridx = 1;
            JComboBox<FahrzeugZustand> zustandCombo = new JComboBox<>(FahrzeugZustand.values());
            zustandCombo.setFont(GROSSE_FIELD_SCHRIFT);
            zustandCombo.setSelectedItem(fahrzeug.getZustand());
            dialog.add(zustandCombo, gbc);
            
            // Buttons
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            
            JButton saveButton = new JButton("Speichern");
            saveButton.setFont(GROSSE_BUTTON_SCHRIFT);
            saveButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
            saveButton.addActionListener(e -> {
                String kennzeichen = kennzeichenField.getText().trim();
                FahrzeugZustand zustand = (FahrzeugZustand) zustandCombo.getSelectedItem();
                
                if (kennzeichen.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Bitte geben Sie ein Kennzeichen ein.",
                        "Eingabefehler",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                fahrzeug.setKennzeichen(kennzeichen);
                fahrzeug.setZustand(zustand);
                
                try {
                    system.getFahrzeugDao().update(fahrzeug);
                    loadFahrzeuge();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(this,
                        "Fahrzeug erfolgreich aktualisiert.",
                        "Erfolg",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Fehler beim Speichern: " + ex.getMessage(),
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(saveButton);
            
            JButton cancelButton = new JButton("Abbrechen");
            cancelButton.setFont(GROSSE_BUTTON_SCHRIFT);
            cancelButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
            cancelButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(cancelButton);
            
            dialog.add(buttonPanel, gbc);
            
            dialog.pack();
            dialog.setSize(450, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden des Fahrzeugs: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Löscht das ausgewählte Fahrzeug.
     */
    private void deleteFahrzeug() {
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
                int id = ((Number) fahrzeugTableModel.getValueAt(selectedRow, 0)).intValue();
                system.getFahrzeugDao().delete(id);
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
     * Ändert den Zustand eines Fahrzeugs.
     */
    private void changeZustand() {
        int selectedRow = fahrzeugTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie ein Fahrzeug aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = ((Number) fahrzeugTableModel.getValueAt(selectedRow, 0)).intValue();
        
        try {
            Optional<Fahrzeug> fahrzeugOpt = system.getFahrzeugDao().findById(id);
            if (!fahrzeugOpt.isPresent()) {
                return;
            }
            
            Fahrzeug fahrzeug = fahrzeugOpt.get();
            
            FahrzeugZustand neuerZustand = (FahrzeugZustand) JOptionPane.showInputDialog(this,
                "Neuen Zustand wählen:",
                "Zustand ändern",
                JOptionPane.QUESTION_MESSAGE,
                null,
                FahrzeugZustand.values(),
                fahrzeug.getZustand());
            
            if (neuerZustand != null) {
                fahrzeug.setZustand(neuerZustand);
                system.getFahrzeugDao().updateStatusAndKilometerstand(fahrzeug);
                loadFahrzeuge();
                
                JOptionPane.showMessageDialog(this,
                    "Zustand erfolgreich geändert.",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Ändern des Zustands: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fügt einen neuen Fahrzeugtyp hinzu.
     */
    private void addFahrzeugtyp() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                     "Fahrzeugtyp hinzufügen", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fields
        JTextField herstellerField = new JTextField(20);
        herstellerField.setFont(GROSSE_FIELD_SCHRIFT);
        JTextField modellField = new JTextField(20);
        modellField.setFont(GROSSE_FIELD_SCHRIFT);
        JTextField kategorieField = new JTextField(20);
        kategorieField.setFont(GROSSE_FIELD_SCHRIFT);
        JComboBox<Antriebsart> antriebCombo = new JComboBox<>(Antriebsart.values());
        antriebCombo.setFont(GROSSE_FIELD_SCHRIFT);
        JSpinner sitzplaetzeSpinner = new JSpinner(new SpinnerNumberModel(5, 2, 50, 1));
        sitzplaetzeSpinner.setFont(GROSSE_FIELD_SCHRIFT);
        ((JSpinner.DefaultEditor) sitzplaetzeSpinner.getEditor()).getTextField().setFont(GROSSE_FIELD_SCHRIFT);
        JTextField preisField = new JTextField("50.00", 20);
        preisField.setFont(GROSSE_FIELD_SCHRIFT);
        
        // Layout
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel herstellerLabel = new JLabel("Hersteller:");
        herstellerLabel.setFont(GROSSE_LABEL_SCHRIFT);
        dialog.add(herstellerLabel, gbc);
        gbc.gridx = 1;
        dialog.add(herstellerField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel modellLabel = new JLabel("Modell:");
        modellLabel.setFont(GROSSE_LABEL_SCHRIFT);
        dialog.add(modellLabel, gbc);
        gbc.gridx = 1;
        dialog.add(modellField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel kategorieLabel = new JLabel("Kategorie:");
        kategorieLabel.setFont(GROSSE_LABEL_SCHRIFT);
        dialog.add(kategorieLabel, gbc);
        gbc.gridx = 1;
        dialog.add(kategorieField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel antriebLabel = new JLabel("Antriebsart:");
        antriebLabel.setFont(GROSSE_LABEL_SCHRIFT);
        dialog.add(antriebLabel, gbc);
        gbc.gridx = 1;
        dialog.add(antriebCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel sitzplaetzeLabel = new JLabel("Sitzplätze:");
        sitzplaetzeLabel.setFont(GROSSE_LABEL_SCHRIFT);
        dialog.add(sitzplaetzeLabel, gbc);
        gbc.gridx = 1;
        dialog.add(sitzplaetzeSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel preisLabel = new JLabel("Tagespreis (€):");
        preisLabel.setFont(GROSSE_LABEL_SCHRIFT);
        dialog.add(preisLabel, gbc);
        gbc.gridx = 1;
        dialog.add(preisField, gbc);
        
        // Buttons
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton saveButton = new JButton("Speichern");
        saveButton.setFont(GROSSE_BUTTON_SCHRIFT);
        saveButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        saveButton.addActionListener(e -> {
            try {
                String hersteller = herstellerField.getText().trim();
                String modell = modellField.getText().trim();
                String kategorie = kategorieField.getText().trim();
                Antriebsart antrieb = (Antriebsart) antriebCombo.getSelectedItem();
                int sitzplaetze = (Integer) sitzplaetzeSpinner.getValue();
                double preis = Double.parseDouble(preisField.getText().trim());
                
                if (hersteller.isEmpty() || modell.isEmpty() || kategorie.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Bitte füllen Sie alle Felder aus.",
                        "Eingabefehler",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Fahrzeugtyp typ = new Fahrzeugtyp(hersteller, modell, kategorie, 
                                                   antrieb, sitzplaetze, preis);
                system.getFahrzeugDao().saveFahrzeugtyp(typ);
                
                loadFahrzeugtypen();
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this,
                    "Fahrzeugtyp erfolgreich hinzugefügt.",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Ungültiger Preis.",
                    "Eingabefehler",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Fehler beim Speichern: " + ex.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);
        
        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.setFont(GROSSE_BUTTON_SCHRIFT);
        cancelButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Bearbeitet den ausgewählten Fahrzeugtyp.
     */
    private void editFahrzeugtyp() {
        int selectedRow = fahrzeugtypTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie einen Fahrzeugtyp aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = ((Number) fahrzeugtypTableModel.getValueAt(selectedRow, 0)).intValue();
        
        try {
            List<Fahrzeugtyp> typen = system.getFahrzeugDao().findAllFahrzeugtypen();
            Fahrzeugtyp typ = typen.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
            
            if (typ == null) {
                JOptionPane.showMessageDialog(this,
                    "Fahrzeugtyp nicht gefunden.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                         "Fahrzeugtyp bearbeiten", true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(12, 15, 12, 15);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Fields
            JTextField herstellerField = new JTextField(typ.getHersteller(), 20);
            herstellerField.setFont(GROSSE_FIELD_SCHRIFT);
            JTextField modellField = new JTextField(typ.getModellBezeichnung(), 20);
            modellField.setFont(GROSSE_FIELD_SCHRIFT);
            JTextField kategorieField = new JTextField(typ.getKategorie(), 20);
            kategorieField.setFont(GROSSE_FIELD_SCHRIFT);
            JComboBox<Antriebsart> antriebCombo = new JComboBox<>(Antriebsart.values());
            antriebCombo.setFont(GROSSE_FIELD_SCHRIFT);
            antriebCombo.setSelectedItem(typ.getAntriebsart());
            
            int sitzplaetzeValue = typ.getSitzplaetze();
            if (sitzplaetzeValue < 2) sitzplaetzeValue = 2;
            if (sitzplaetzeValue > 50) sitzplaetzeValue = 50;
            JSpinner sitzplaetzeSpinner = new JSpinner(new SpinnerNumberModel(sitzplaetzeValue, 2, 50, 1));
            sitzplaetzeSpinner.setFont(GROSSE_FIELD_SCHRIFT);
            ((JSpinner.DefaultEditor) sitzplaetzeSpinner.getEditor()).getTextField().setFont(GROSSE_FIELD_SCHRIFT);
            JTextField preisField = new JTextField(String.format(Locale.US, "%.2f", typ.getStandardTagesPreis()), 20);
            preisField.setFont(GROSSE_FIELD_SCHRIFT);
            
            // Layout
            int row = 0;
            gbc.gridx = 0; gbc.gridy = row;
            JLabel herstellerLabel = new JLabel("Hersteller:");
            herstellerLabel.setFont(GROSSE_LABEL_SCHRIFT);
            dialog.add(herstellerLabel, gbc);
            gbc.gridx = 1;
            dialog.add(herstellerField, gbc);
            
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            JLabel modellLabel = new JLabel("Modell:");
            modellLabel.setFont(GROSSE_LABEL_SCHRIFT);
            dialog.add(modellLabel, gbc);
            gbc.gridx = 1;
            dialog.add(modellField, gbc);
            
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            JLabel kategorieLabel = new JLabel("Kategorie:");
            kategorieLabel.setFont(GROSSE_LABEL_SCHRIFT);
            dialog.add(kategorieLabel, gbc);
            gbc.gridx = 1;
            dialog.add(kategorieField, gbc);
            
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            JLabel antriebLabel = new JLabel("Antriebsart:");
            antriebLabel.setFont(GROSSE_LABEL_SCHRIFT);
            dialog.add(antriebLabel, gbc);
            gbc.gridx = 1;
            dialog.add(antriebCombo, gbc);
            
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            JLabel sitzplaetzeLabel = new JLabel("Sitzplätze:");
            sitzplaetzeLabel.setFont(GROSSE_LABEL_SCHRIFT);
            dialog.add(sitzplaetzeLabel, gbc);
            gbc.gridx = 1;
            dialog.add(sitzplaetzeSpinner, gbc);
            
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            JLabel preisLabel = new JLabel("Tagespreis (€):");
            preisLabel.setFont(GROSSE_LABEL_SCHRIFT);
            dialog.add(preisLabel, gbc);
            gbc.gridx = 1;
            dialog.add(preisField, gbc);
            
            // Buttons
            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            
            JButton saveButton = new JButton("Speichern");
            saveButton.setFont(GROSSE_BUTTON_SCHRIFT);
            saveButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
            saveButton.addActionListener(e -> {
                try {
                    String hersteller = herstellerField.getText().trim();
                    String modell = modellField.getText().trim();
                    String kategorie = kategorieField.getText().trim();
                    Antriebsart antrieb = (Antriebsart) antriebCombo.getSelectedItem();
                    int sitzplaetze = (Integer) sitzplaetzeSpinner.getValue();
                    double preis = Double.parseDouble(preisField.getText().trim());
                    
                    if (hersteller.isEmpty() || modell.isEmpty() || kategorie.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog,
                            "Bitte füllen Sie alle Felder aus.",
                            "Eingabefehler",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    typ.setHersteller(hersteller);
                    typ.setModellBezeichnung(modell);
                    typ.setKategorie(kategorie);
                    typ.setAntriebsart(antrieb);
                    typ.setSitzplaetze(sitzplaetze);
                    typ.setStandardTagesPreis(preis);
                    
                    system.getFahrzeugDao().updateFahrzeugtyp(typ);
                    
                    loadFahrzeugtypen();
                    loadFahrzeuge();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(this,
                        "Fahrzeugtyp erfolgreich aktualisiert.",
                        "Erfolg",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Ungültiger Preis.",
                        "Eingabefehler",
                        JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Fehler beim Speichern: " + ex.getMessage(),
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(saveButton);
            
            JButton cancelButton = new JButton("Abbrechen");
            cancelButton.setFont(GROSSE_BUTTON_SCHRIFT);
            cancelButton.setPreferredSize(GROSSE_BUTTON_GROESSE);
            cancelButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(cancelButton);
            
            dialog.add(buttonPanel, gbc);
            
            dialog.pack();
            dialog.setSize(500, 450);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden des Fahrzeugtyps: " + e.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Löscht den ausgewählten Fahrzeugtyp.
     */
    private void deleteFahrzeugtyp() {
        int selectedRow = fahrzeugtypTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie einen Fahrzeugtyp aus.",
                "Keine Auswahl",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Möchten Sie diesen Fahrzeugtyp wirklich löschen?\n" +
            "Dies wird alle zugehörigen Fahrzeuge beeinträchtigen.",
            "Löschen bestätigen",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = ((Number) fahrzeugtypTableModel.getValueAt(selectedRow, 0)).longValue();
                system.getFahrzeugDao().deleteFahrzeugtyp(id);
                loadFahrzeugtypen();
                loadFahrzeuge(); // Refresh Fahrzeuge auch
                
                JOptionPane.showMessageDialog(this,
                    "Fahrzeugtyp erfolgreich gelöscht.",
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
     * Aktualisiert die Anzeige.
     */
    public void refresh() {
        loadData();
    }

    /**
     * Bearbeitet das ausgewählte Fahrzeug (öffentlich für externe Aufrufe).
     */
    public void editSelectedFahrzeug() {
        editFahrzeug();
    }

    /**
     * Löscht das ausgewählte Fahrzeug (öffentlich für externe Aufrufe).
     */
    public void deleteSelectedFahrzeug() {
        deleteFahrzeug();
    }

    /**
     * Ändert den Zustand des ausgewählten Fahrzeugs (öffentlich für externe Aufrufe).
     */
    public void changeSelectedZustand() {
        changeZustand();
    }
}
