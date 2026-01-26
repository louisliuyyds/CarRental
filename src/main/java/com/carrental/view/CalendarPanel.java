package com.carrental.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarPanel extends JPanel {
    private YearMonth displayedMonth;
    private LocalDate selectedDate;
    private LocalDate today;
    private JPanel calendarGrid;
    private JLabel monthYearLabel;
    private javax.swing.event.ChangeListener changeListener;

    // Colors
    private static final Color HEADER_COLOR = new Color(70, 130, 180);
    private static final Color SELECTED_COLOR = new Color(255, 193, 7);
    private static final Color TODAY_COLOR = new Color(200, 230, 255);
    private static final Color DISABLED_COLOR = new Color(220, 220, 220);
    private static final Color TEXT_COLOR = Color.BLACK;

    // German month names
    private static final String[] GERMAN_MONTHS = {
            "Januar", "Februar", "März", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November", "Dezember"
    };

    private static final String[] WEEKDAY_NAMES = {"So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"};

    public CalendarPanel() {
        this.today = LocalDate.now();
        this.displayedMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();
        this.changeListener = null;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Navigation panel
        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.NORTH);

        // Calendar grid panel
        calendarGrid = new JPanel();
        calendarGrid.setBackground(Color.WHITE);
        updateCalendarGrid();
        add(calendarGrid, BorderLayout.CENTER);
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);

        JButton prevButton = new JButton("◀ Vorheriger");
        prevButton.setFont(new Font("Arial", Font.BOLD, 12));
        prevButton.addActionListener(e -> previousMonth());

        JButton nextButton = new JButton("Nächster ▶");
        nextButton.setFont(new Font("Arial", Font.BOLD, 12));
        nextButton.addActionListener(e -> nextMonth());

        monthYearLabel = new JLabel();
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthYearLabel.setHorizontalAlignment(JLabel.CENTER);
        updateMonthYearLabel();

        navPanel.add(prevButton, BorderLayout.WEST);
        navPanel.add(monthYearLabel, BorderLayout.CENTER);
        navPanel.add(nextButton, BorderLayout.EAST);

        return navPanel;
    }

    private void updateMonthYearLabel() {
        String monthName = GERMAN_MONTHS[displayedMonth.getMonthValue() - 1];
        monthYearLabel.setText(monthName + " " + displayedMonth.getYear());
    }

    private void updateCalendarGrid() {
        calendarGrid.removeAll();
        calendarGrid.setLayout(new GridLayout(7, 7, 2, 2));

        // Add weekday headers
        for (String weekday : WEEKDAY_NAMES) {
            JLabel label = new JLabel(weekday);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setBackground(HEADER_COLOR);
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
            calendarGrid.add(label);
        }

        // Calculate first day of month and number of days
        LocalDate firstDay = displayedMonth.atDay(1);
        int daysInMonth = displayedMonth.lengthOfMonth();
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // 0 = Sunday

        // Add empty cells before first day
        for (int i = 0; i < startDayOfWeek; i++) {
            JPanel emptyCell = new JPanel();
            emptyCell.setBackground(DISABLED_COLOR);
            calendarGrid.add(emptyCell);
        }

        // Add date buttons
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(displayedMonth.getYear(), displayedMonth.getMonthValue(), day);
            createDateButton(date);
        }

        // Add empty cells after last day to complete grid
        int totalCells = startDayOfWeek + daysInMonth;
        int remainingCells = 42 - totalCells; // 6 rows × 7 cols = 42 cells
        for (int i = 0; i < remainingCells; i++) {
            JPanel emptyCell = new JPanel();
            emptyCell.setBackground(DISABLED_COLOR);
            calendarGrid.add(emptyCell);
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private void createDateButton(LocalDate date) {
        JButton dateButton = new JButton(String.valueOf(date.getDayOfMonth()));
        dateButton.setFont(new Font("Arial", Font.BOLD, 13));
        dateButton.setFocusPainted(false);
        dateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Set colors based on date state
        if (date.equals(selectedDate)) {
            dateButton.setBackground(SELECTED_COLOR);
            dateButton.setForeground(TEXT_COLOR);
        } else if (date.equals(today)) {
            dateButton.setBackground(TODAY_COLOR);
            dateButton.setForeground(TEXT_COLOR);
        } else {
            dateButton.setBackground(Color.WHITE);
            dateButton.setForeground(TEXT_COLOR);
        }

        dateButton.setOpaque(true);
        dateButton.setBorderPainted(true);
        dateButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        dateButton.addActionListener(e -> {
            selectedDate = date;
            updateCalendarGrid();
            if (changeListener != null) {
                changeListener.stateChanged(null);
            }
        });

        calendarGrid.add(dateButton);
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate date) {
        if (date != null) {
            this.selectedDate = date;
            this.displayedMonth = YearMonth.from(date);
            updateMonthYearLabel();
            updateCalendarGrid();
        }
    }

    public void setDisplayedMonth(YearMonth yearMonth) {
        if (yearMonth != null) {
            this.displayedMonth = yearMonth;
            updateMonthYearLabel();
            updateCalendarGrid();
        }
    }

    public void addChangeListener(javax.swing.event.ChangeListener listener) {
        this.changeListener = listener;
    }

    private void previousMonth() {
        displayedMonth = displayedMonth.minusMonths(1);
        updateMonthYearLabel();
        updateCalendarGrid();
    }

    private void nextMonth() {
        displayedMonth = displayedMonth.plusMonths(1);
        updateMonthYearLabel();
        updateCalendarGrid();
    }
}
