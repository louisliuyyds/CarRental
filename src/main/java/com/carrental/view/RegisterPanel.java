package com.carrental.view;

import com.carrental.controller.AuthController;
import com.carrental.model.Kunde;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * Registrierungs-Panel für neue Kunden.
 * Ermöglicht die Erstellung eines neuen Kundenkontos.
 */
public class RegisterPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthController authController;
    
    private JTextField accountNameField;
    private JPasswordField passwortField;
    private JPasswordField passwortConfirmField;
    private JTextField vornameField;
    private JTextField nachnameField;
    private JTextField emailField;
    private JTextField geburtstagsField; // Format: YYYY-MM-DD
    private JTextField fuehrerscheinField;
    private JButton registerButton;
    private JButton backButton;

    /**
     * Konstruktor für das Registrierungs-Panel.
     */
    public RegisterPanel(MainFrame mainFrame, AuthController authController) {
        this.mainFrame = mainFrame;
        this.authController = authController;
        
        initializeUI();
    }

    /**
     * Initialisiert die Benutzeroberfläche.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Titel-Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Neues Kundenkonto erstellen");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Formular-Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Benutzername
        addFormField(formPanel, gbc, row++, "Benutzername *:", 
            accountNameField = new JTextField(20));
        
        // Passwort
        addFormField(formPanel, gbc, row++, "Passwort * (min. 6 Zeichen):", 
            passwortField = new JPasswordField(20));
        
        // Passwort bestätigen
        addFormField(formPanel, gbc, row++, "Passwort bestätigen *:", 
            passwortConfirmField = new JPasswordField(20));
        
        // Vorname
        addFormField(formPanel, gbc, row++, "Vorname:", 
            vornameField = new JTextField(20));
        
        // Nachname
        addFormField(formPanel, gbc, row++, "Nachname:", 
            nachnameField = new JTextField(20));
        
        // Email
        addFormField(formPanel, gbc, row++, "E-Mail:", 
            emailField = new JTextField(20));
        
        // Geburtstag
        addFormField(formPanel, gbc, row++, "Geburtstag * (YYYY-MM-DD):", 
            geburtstagsField = new JTextField(20));
        geburtstagsField.setToolTipText("Format: 2000-01-31");
        
        // Führerscheinnummer
        addFormField(formPanel, gbc, row++, "Führerscheinnummer *:", 
            fuehrerscheinField = new JTextField(20));
        
        // Hinweis
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel hinweisLabel = new JLabel("* Pflichtfelder");
        hinweisLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(hinweisLabel, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        registerButton = new JButton("Registrieren");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> performRegistration());
        buttonPanel.add(registerButton);
        
        backButton = new JButton("Zurück zum Login");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.showLoginPanel());
        buttonPanel.add(backButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Hilfsmethode zum Hinzufügen von Formularfeldern.
     */
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, 
                             String labelText, JTextField field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(field, gbc);
    }

    /**
     * Führt die Registrierung durch.
     */
    private void performRegistration() {
        // Eingaben lesen
        String accountName = accountNameField.getText().trim();
        String passwort = new String(passwortField.getPassword());
        String passwortConfirm = new String(passwortConfirmField.getPassword());
        String vorname = vornameField.getText().trim();
        String nachname = nachnameField.getText().trim();
        String email = emailField.getText().trim();
        String geburtstagsStr = geburtstagsField.getText().trim();
        String fuehrerschein = fuehrerscheinField.getText().trim();
        
        // Validierung
        if (accountName.isEmpty() || passwort.isEmpty() || geburtstagsStr.isEmpty() || fuehrerschein.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Bitte füllen Sie alle Pflichtfelder (*) aus.",
                "Eingabefehler",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!passwort.equals(passwortConfirm)) {
            JOptionPane.showMessageDialog(this,
                "Die Passwörter stimmen nicht überein.",
                "Eingabefehler",
                JOptionPane.WARNING_MESSAGE);
            passwortField.setText("");
            passwortConfirmField.setText("");
            return;
        }
        
        // Geburtstag parsen
        LocalDate geburtstag;
        try {
            geburtstag = LocalDate.parse(geburtstagsStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Ungültiges Datumsformat. Bitte verwenden Sie YYYY-MM-DD (z.B. 2000-01-31).",
                "Eingabefehler",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Registrierung durchführen
        Kunde kunde = authController.registrieren(accountName, passwort, vorname, nachname, 
                                                  email, geburtstag, fuehrerschein);
        
        if (kunde != null) {
            JOptionPane.showMessageDialog(this,
                "Registrierung erfolgreich! Sie können sich jetzt anmelden.",
                "Erfolg",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Felder leeren
            clearFields();
            
            // Zurück zum Login
            mainFrame.showLoginPanel();
        } else {
            JOptionPane.showMessageDialog(this,
                "Registrierung fehlgeschlagen. Bitte überprüfen Sie Ihre Angaben.",
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Leert alle Eingabefelder.
     */
    private void clearFields() {
        accountNameField.setText("");
        passwortField.setText("");
        passwortConfirmField.setText("");
        vornameField.setText("");
        nachnameField.setText("");
        emailField.setText("");
        geburtstagsField.setText("");
        fuehrerscheinField.setText("");
    }
}
