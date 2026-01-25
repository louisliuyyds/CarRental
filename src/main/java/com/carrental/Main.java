package com.carrental;

import com.carrental.controller.AuthController;
import com.carrental.controller.CarRentalSystem;
import com.carrental.view.MainFrame;

import javax.swing.*;

/**
 * Hauptklasse für die Autovermietungsanwendung.
 * Startet die GUI-Anwendung.
 */
public class Main {

    /**
     * Main-Methode - Einstiegspunkt der Anwendung.
     * 
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
        // Systemweite Exception-Handler einrichten
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Unbehandelter Fehler in Thread " + thread.getName() + ":");
            throwable.printStackTrace();
            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "Ein unerwarteter Fehler ist aufgetreten:\n" + throwable.getMessage() +
                    "\n\nBitte starten Sie die Anwendung neu.",
                    "Kritischer Fehler",
                    JOptionPane.ERROR_MESSAGE);
            });
        });
        
        // GUI im Event Dispatch Thread starten
        SwingUtilities.invokeLater(() -> {
            try {
                // Look and Feel setzen (System-Standard verwenden)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Singleton-System initialisieren
                CarRentalSystem system = CarRentalSystem.getInstance();
                
                // AuthController erstellen
                AuthController authController = new AuthController(system);
                
                // Hauptfenster erstellen und anzeigen
                MainFrame mainFrame = new MainFrame(system, authController);
                mainFrame.setVisible(true);
                
                System.out.println("Autovermietungssystem gestartet.");
                
            } catch (Exception e) {
                System.err.println("Fehler beim Starten der Anwendung:");
                e.printStackTrace();
                
                JOptionPane.showMessageDialog(null,
                    "Die Anwendung konnte nicht gestartet werden:\n" + e.getMessage() +
                    "\n\nBitte überprüfen Sie die Konfiguration und Datenbankverbindung.",
                    "Startfehler",
                    JOptionPane.ERROR_MESSAGE);
                
                System.exit(1);
            }
        });
    }
}
