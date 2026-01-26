package com.carrental.view;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Custom calendar-based date chooser with year, month, and day selection.
 * Provides a more accessible date selection interface than standard date spinners.
 */
public class CalendarDateChooser extends JPanel {

    private LocalDate selectedDate;
    private final ChangeListener changeListener;
    
    private JSpinner yearSpinner;
    private JComboBox<Integer> monthCombo;
    private JSpinner daySpinner;
    private JButton todayButton;
    private JLabel datePreviewLabel;

    /**
     * Constructor for CalendarDateChooser.
     *
     * @param initialDate the initial date to display
     * @param changeListener callback when date changes
     */
    public CalendarDateChooser(LocalDate initialDate, ChangeListener changeListener) {
        this.selectedDate = initialDate != null ? initialDate : LocalDate.now();
        this.changeListener = changeListener;
        
        initializeUI();
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBackground(new Color(240, 248, 255));
        
        // Year spinner
        JLabel yearLabel = new JLabel("Jahr:");
        yearLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(yearLabel);
        
        yearSpinner = new JSpinner(
            new SpinnerNumberModel(selectedDate.getYear(), 1900, 2100, 1));
        yearSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) yearSpinner.getEditor()).getTextField().setColumns(5);
        yearSpinner.addChangeListener(e -> updateDate());
        add(yearSpinner);
        
        // Month combo
        JLabel monthLabel = new JLabel("Monat:");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(monthLabel);
        
        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        monthCombo = new JComboBox<>(months);
        monthCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        monthCombo.setSelectedItem(selectedDate.getMonthValue());
        monthCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                         int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Integer) {
                    int monthNum = (Integer) value;
                    String[] monthNames = {"Januar", "Februar", "März", "April", "Mai", "Juni",
                        "Juli", "August", "September", "Oktober", "November", "Dezember"};
                    value = monthNames[monthNum - 1];
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        monthCombo.addActionListener(e -> updateDate());
        add(monthCombo);
        
        // Day spinner
        JLabel dayLabel = new JLabel("Tag:");
        dayLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(dayLabel);
        
        daySpinner = new JSpinner(
            new SpinnerNumberModel(selectedDate.getDayOfMonth(), 1, 31, 1));
        daySpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) daySpinner.getEditor()).getTextField().setColumns(3);
        daySpinner.addChangeListener(e -> updateDate());
        add(daySpinner);
        
        // Today button
        todayButton = new JButton("Heute");
        todayButton.setFont(new Font("Arial", Font.BOLD, 14));
        todayButton.setBackground(new Color(144, 238, 144));
        todayButton.setForeground(Color.BLACK);
        todayButton.addActionListener(e -> setToday());
        add(todayButton);
        
        // Date preview
        datePreviewLabel = new JLabel();
        datePreviewLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        updatePreview();
        add(datePreviewLabel);
    }

    /**
     * Updates the selected date based on spinner/combo values.
     */
    private void updateDate() {
        try {
            int year = (Integer) yearSpinner.getValue();
            int month = (Integer) monthCombo.getSelectedItem();
            int day = (Integer) daySpinner.getValue();
            
            // Validate day is within month's range
            YearMonth yearMonth = YearMonth.of(year, month);
            int maxDay = yearMonth.lengthOfMonth();
            if (day > maxDay) {
                day = maxDay;
                daySpinner.setValue(day);
            }
            
            selectedDate = LocalDate.of(year, month, day);
            updatePreview();
            
            if (changeListener != null) {
                changeListener.stateChanged(null);
            }
        } catch (Exception e) {
            // Ignore invalid state during updates
        }
    }

    /**
     * Sets the date to today.
     */
    private void setToday() {
        selectedDate = LocalDate.now();
        yearSpinner.setValue(selectedDate.getYear());
        monthCombo.setSelectedItem(selectedDate.getMonthValue());
        daySpinner.setValue(selectedDate.getDayOfMonth());
        updatePreview();
        
        if (changeListener != null) {
            changeListener.stateChanged(null);
        }
    }

    /**
     * Updates the date preview label.
     */
    private void updatePreview() {
        String[] dayNames = {"So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"};
        int dayOfWeek = selectedDate.getDayOfWeek().getValue() % 7;
        String preview = String.format("(%s, %02d.%02d.%04d)",
            dayNames[dayOfWeek],
            selectedDate.getDayOfMonth(),
            selectedDate.getMonthValue(),
            selectedDate.getYear());
        datePreviewLabel.setText(preview);
    }

    /**
     * Returns the selected date.
     *
     * @return the selected LocalDate
     */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    /**
     * Sets the selected date.
     *
     * @param date the date to select
     */
    public void setSelectedDate(LocalDate date) {
        if (date != null) {
            this.selectedDate = date;
            yearSpinner.setValue(date.getYear());
            monthCombo.setSelectedItem(date.getMonthValue());
            daySpinner.setValue(date.getDayOfMonth());
            updatePreview();
        }
    }
}
