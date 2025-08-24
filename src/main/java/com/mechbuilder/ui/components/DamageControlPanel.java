package com.mechbuilder.ui.components;

import com.mechbuilder.model.MechSection;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel for controlling damage application and field repairs for mech sections
 */
public class DamageControlPanel extends JPanel {
    
    private final JSpinner damageSpinner;
    private final JSpinner repairSpinner;
    private final JButton applyDamageButton;
    private final JButton fieldRepairButton;
    private final JLabel statusLabel;
    private final JTextArea damageLog;
    
    private MechSection currentSection;
    
    public DamageControlPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
            "Damage Control",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        setPreferredSize(new Dimension(300, 250));
        setBackground(Color.WHITE);
        
        // Damage application panel
        JPanel damagePanel = new JPanel(new GridBagLayout());
        damagePanel.setBackground(new Color(255, 240, 240));
        damagePanel.setBorder(BorderFactory.createTitledBorder("Apply Damage"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Damage amount spinner
        gbc.gridx = 0; gbc.gridy = 0;
        damagePanel.add(new JLabel("Damage Amount:"), gbc);
        
        gbc.gridx = 1;
        damageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        damageSpinner.setPreferredSize(new Dimension(80, 25));
        damagePanel.add(damageSpinner, gbc);
        
        // Apply damage button
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        applyDamageButton = new JButton("Apply Damage");
        applyDamageButton.setBackground(new Color(255, 200, 200));
        applyDamageButton.addActionListener(e -> applyDamage());
        damagePanel.add(applyDamageButton, gbc);
        
        // Field repair panel
        JPanel repairPanel = new JPanel(new GridBagLayout());
        repairPanel.setBackground(new Color(240, 255, 240));
        repairPanel.setBorder(BorderFactory.createTitledBorder("Field Repair"));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Repair amount spinner
        gbc.gridx = 0; gbc.gridy = 0;
        repairPanel.add(new JLabel("Repair Amount:"), gbc);
        
        gbc.gridx = 1;
        repairSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 5));
        repairSpinner.setPreferredSize(new Dimension(80, 25));
        repairPanel.add(repairSpinner, gbc);
        
        // Field repair button
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        fieldRepairButton = new JButton("Apply Field Repair");
        fieldRepairButton.setBackground(new Color(200, 255, 200));
        fieldRepairButton.addActionListener(e -> applyFieldRepair());
        repairPanel.add(fieldRepairButton, gbc);
        
        // Status label
        statusLabel = new JLabel("No section selected");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 11));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusLabel.setBackground(new Color(240, 240, 240));
        statusLabel.setOpaque(true);
        
        // Damage log
        damageLog = new JTextArea();
        damageLog.setEditable(false);
        damageLog.setFont(new Font("Monospaced", Font.PLAIN, 10));
        damageLog.setBackground(new Color(250, 250, 250));
        JScrollPane logScrollPane = new JScrollPane(damageLog);
        logScrollPane.setPreferredSize(new Dimension(280, 100));
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Damage Log"));
        
        // Layout
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        topPanel.add(damagePanel);
        topPanel.add(repairPanel);
        
        add(topPanel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);
        
        // Initially disable buttons
        setSection(null);
    }
    
    /**
     * Set the current section for damage control
     */
    public void setSection(MechSection section) {
        this.currentSection = section;
        
        boolean hasSection = section != null;
        applyDamageButton.setEnabled(hasSection);
        fieldRepairButton.setEnabled(hasSection);
        
        if (hasSection) {
            updateStatus();
        } else {
            statusLabel.setText("No section selected");
            damageLog.setText("");
        }
    }
    
    /**
     * Update the status display
     */
    public void updateStatus() {
        if (currentSection != null) {
            statusLabel.setText("<html><b>" + currentSection.getName() + "</b><br/>" + 
                              currentSection.getDamageStatus() + "</html>");
        }
    }
    
    /**
     * Apply damage to the current section
     */
    private void applyDamage() {
        if (currentSection == null) return;
        
        int damage = (Integer) damageSpinner.getValue();
        int remainingDamage = currentSection.applyDamage(damage);
        
        // Log the damage
        String logEntry = String.format("[%s] Applied %d damage to %s\n", 
            getCurrentTime(), damage, currentSection.getName());
        if (remainingDamage > 0) {
            logEntry += String.format("  Section destroyed! %d damage overflow\n", remainingDamage);
        }
        logEntry += String.format("  New status: %s\n", currentSection.getDamageStatus());
        
        appendToLog(logEntry);
        updateStatus();
        
        // Notify parent that damage was applied
        firePropertyChange("damageApplied", null, currentSection);
    }
    
    /**
     * Apply field repair to the current section
     */
    private void applyFieldRepair() {
        if (currentSection == null) return;
        
        int repairAmount = (Integer) repairSpinner.getValue();
        currentSection.applyFieldRepair(repairAmount);
        
        // Log the repair
        String logEntry = String.format("[%s] Applied %d field repair armor to %s\n", 
            getCurrentTime(), repairAmount, currentSection.getName());
        logEntry += String.format("  New status: %s\n", currentSection.getDamageStatus());
        
        appendToLog(logEntry);
        updateStatus();
        
        // Notify parent that repair was applied
        firePropertyChange("repairApplied", null, currentSection);
    }
    
    /**
     * Append text to the damage log
     */
    private void appendToLog(String text) {
        damageLog.append(text);
        damageLog.setCaretPosition(damageLog.getDocument().getLength());
    }
    
    /**
     * Get current time string for logging
     */
    private String getCurrentTime() {
        return java.time.LocalTime.now().toString().substring(0, 8);
    }
    
    /**
     * Clear the damage log
     */
    public void clearLog() {
        damageLog.setText("");
    }
    
    /**
     * Get the current section
     */
    public MechSection getCurrentSection() {
        return currentSection;
    }
} 