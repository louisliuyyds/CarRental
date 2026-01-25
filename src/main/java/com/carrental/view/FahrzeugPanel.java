package com.carrental.view;

import com.carrental.controller.CarRentalSystem;
import com.carrental.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
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
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
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
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.addActionListener(e -> loadFahrzeuge());
        toolbar.add(refreshButton);
        
        JButton addButton = new JButton("Hinzufügen");
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addFahrzeug());
        toolbar.add(addButton);
        
        JButton editButton = new JButton("Bearbeiten");
        editButton.addActionListener(e -> editFahrzeug());
        toolbar.add(editButton);
        
        JButton deleteButton = new JButton("Löschen");
        deleteButton.addActionListener(e -> deleteFahrzeug());
        toolbar.add(deleteButton);
        
        JButton zustandButton = new JButton("Zustand ändern");
        zustandButton.addActionListener(e -> changeZustand());
        toolbar.add(zustandButton);
        
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
        fahrzeugTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(fahrzeugTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Erstellt das Fahrzeugtypen-Panel.
     */
    private JPanel createFahrzeugtypPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Aktualisieren");
        refreshButton.addActionListener(e -> loadFahrzeugtypen());
        toolbar.add(refreshButton);
        
        JButton addButton = new JButton("Hinzufügen");
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addFahrzeugtyp());
        toolbar.add(addButton);
        
        JButton editButton = new JButton("Bearbeiten");
        editButton.addActionListener(e -> editFahrzeugtyp());
        toolbar.add(editButton);
        
        JButton deleteButton = new JButton("Löschen");
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
        fahrzeugtypTable.setRowHeight(25);
        
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
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Kennzeichen
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Kennzeichen:"), gbc);
        
        gbc.gridx = 1;
        JTextField kennzeichenField = new JTextField(15);
        dialog.add(kennzeichenField, gbc);
        
        // Fahrzeugtyp
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Fahrzeugtyp:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<Fahrzeugtyp> typCombo = new JComboBox<>();
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
        dialog.add(new JLabel("Zustand:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<FahrzeugZustand> zustandCombo = new JComboBox<>(FahrzeugZustand.values());
        dialog.add(zustandCombo, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton saveButton = new JButton("Speichern");
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
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
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
        
        Long id = (Long) fahrzeugTableModel.getValueAt(selectedRow, 0);
        
        try {
            Optional<Fahrzeug> fahrzeugOpt = system.getFahrzeugDao().findById(id.intValue());
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
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Kennzeichen
            gbc.gridx = 0;
            gbc.gridy = 0;
            dialog.add(new JLabel("Kennzeichen:"), gbc);
            
            gbc.gridx = 1;
            JTextField kennzeichenField = new JTextField(fahrzeug.getKennzeichen(), 15);
            dialog.add(kennzeichenField, gbc);
            
            // Zustand
            gbc.gridx = 0;
            gbc.gridy = 1;
            dialog.add(new JLabel("Zustand:"), gbc);
            
            gbc.gridx = 1;
            JComboBox<FahrzeugZustand> zustandCombo = new JComboBox<>(FahrzeugZustand.values());
            zustandCombo.setSelectedItem(fahrzeug.getZustand());
            dialog.add(zustandCombo, gbc);
            
            // Buttons
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            JButton saveButton = new JButton("Speichern");
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
            cancelButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(cancelButton);
            
            dialog.add(buttonPanel, gbc);
            
            dialog.pack();
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
        
        Long id = (Long) fahrzeugTableModel.getValueAt(selectedRow, 0);
        
        try {
            Optional<Fahrzeug> fahrzeugOpt = system.getFahrzeugDao().findById(id.intValue());
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
                system.getFahrzeugDao().update(fahrzeug);
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
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fields
        JTextField herstellerField = new JTextField(15);
        JTextField modellField = new JTextField(15);
        JTextField kategorieField = new JTextField(15);
        JComboBox<Antriebsart> antriebCombo = new JComboBox<>(Antriebsart.values());
        JSpinner sitzplaetzeSpinner = new JSpinner(new SpinnerNumberModel(5, 2, 50, 1));
        JTextField preisField = new JTextField("50.00", 15);
        
        // Layout
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Hersteller:"), gbc);
        gbc.gridx = 1;
        dialog.add(herstellerField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Modell:"), gbc);
        gbc.gridx = 1;
        dialog.add(modellField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Kategorie:"), gbc);
        gbc.gridx = 1;
        dialog.add(kategorieField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Antriebsart:"), gbc);
        gbc.gridx = 1;
        dialog.add(antriebCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Sitzplätze:"), gbc);
        gbc.gridx = 1;
        dialog.add(sitzplaetzeSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Tagespreis (€):"), gbc);
        gbc.gridx = 1;
        dialog.add(preisField, gbc);
        
        // Buttons
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton saveButton = new JButton("Speichern");
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
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
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
        
        JOptionPane.showMessageDialog(this,
            "Bearbeiten von Fahrzeugtypen ist aktuell nicht implementiert.\n" +
            "Bitte löschen Sie den Typ und erstellen Sie einen neuen.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
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
                Long id = (Long) fahrzeugtypTableModel.getValueAt(selectedRow, 0);
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
}
