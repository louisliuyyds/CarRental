package com.carrental.view;

import com.carrental.controller.AuthController;

import javax.swing.*;
import java.awt.*;

/**
 * Login-Panel für Benutzeranmeldung.
 * Ermöglicht Login für Kunden und Mitarbeiter.
 */
public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthController authController;
    
    private JTextField accountNameField;
    private JPasswordField passwortField;
    private JButton loginButton;
    private JButton registerButton;

    /**
     * Konstruktor für das Login-Panel.
     */
    public LoginPanel(MainFrame mainFrame, AuthController authController) {
        this.mainFrame = mainFrame;
        this.authController = authController;
        
        initializeUI();
    }

    /**
     * Initialisiert die Benutzeroberfläche.
     */
    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255)); // Alice Blue
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Titel
        JLabel titleLabel = new JLabel("CarRental Autovermietung");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);
        
        // Untertitel
        JLabel subtitleLabel = new JLabel("Bitte melden Sie sich an");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        add(subtitleLabel, gbc);
        
        // Abstand
        gbc.gridy = 2;
        add(Box.createVerticalStrut(20), gbc);
        
        // Account-Name Label
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel accountLabel = new JLabel("Benutzername:");
        accountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(accountLabel, gbc);
        
        // Account-Name Feld
        gbc.gridx = 1;
        accountNameField = new JTextField(20);
        accountNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(accountNameField, gbc);
        
        // Passwort Label
        gbc.gridy = 4;
        gbc.gridx = 0;
        JLabel passwortLabel = new JLabel("Passwort:");
        passwortLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(passwortLabel, gbc);
        
        // Passwort Feld
        gbc.gridx = 1;
        passwortField = new JPasswordField(20);
        passwortField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(passwortField, gbc);
        
        // Login Button
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        loginButton = new JButton("Anmelden");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> performLogin());
        add(loginButton, gbc);
        
        // Register Button
        gbc.gridy = 6;
        registerButton = new JButton("Neues Konto erstellen");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.setBackground(new Color(144, 238, 144)); // Light Green
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> mainFrame.showRegisterPanel());
        add(registerButton, gbc);
        
        // Enter-Taste für Login
        passwortField.addActionListener(e -> performLogin());
    }

    /**
     * Führt den Login-Vorgang aus.
     */
    private void performLogin() {
        String accountName = accountNameField.getText().trim();
        String passwort = new String(passwortField.getPassword());
        
        if (accountName.isEmpty() || passwort.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Bitte füllen Sie alle Felder aus.",
                "Eingabefehler",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Login durchführen
        boolean success = authController.login(accountName, passwort);
        
        if (success) {
            // Felder leeren
            accountNameField.setText("");
            passwortField.setText("");
            
            // Zum entsprechenden Dashboard weiterleiten
            if (authController.isKunde()) {
                mainFrame.showKundeDashboard();
            } else if (authController.isMitarbeiter()) {
                mainFrame.showMitarbeiterDashboard();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Benutzername oder Passwort ist falsch.",
                "Login fehlgeschlagen",
                JOptionPane.ERROR_MESSAGE);
            passwortField.setText("");
        }
    }
}
