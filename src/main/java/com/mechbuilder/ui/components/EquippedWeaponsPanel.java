package com.mechbuilder.ui.components;

import com.mechbuilder.model.WeaponComponent;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Panel displaying all currently equipped weapons across all mech sections
 * for easy reference and loadout overview.
 */
public class EquippedWeaponsPanel extends JPanel {
    
    private final JPanel contentPanel;
    private final JLabel totalWeaponsLabel;
    private final Map<String, List<WeaponComponent>> equippedWeaponsBySection;
    
    public EquippedWeaponsPanel() {
        this.equippedWeaponsBySection = new HashMap<>();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
            "Equipped Weapons",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        setPreferredSize(new Dimension(280, 200));
        setBackground(Color.WHITE);
        
        // Create header with total count
        totalWeaponsLabel = new JLabel("Total Weapons: 0");
        totalWeaponsLabel.setFont(new Font("Arial", Font.BOLD, 11));
        totalWeaponsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        totalWeaponsLabel.setBackground(new Color(240, 240, 240));
        totalWeaponsLabel.setOpaque(true);
        
        // Create content panel for weapon listings
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        // Add scroll pane for content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        add(totalWeaponsLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Initialize with empty state
        updateDisplay();
    }
    
    /**
     * Update the display with current equipped weapons
     */
    public void updateEquippedWeapons(Map<String, List<WeaponComponent>> weaponsBySection) {
        this.equippedWeaponsBySection.clear();
        this.equippedWeaponsBySection.putAll(weaponsBySection);
        updateDisplay();
    }
    
    /**
     * Update the panel with total tonnage information
     */
    public void updateTotalTonnage(double weaponTonnage, double armorTonnage, double totalTonnage, double maxTonnage) {
        // Update the total weapons label to show tonnage info
        StringBuilder tonnageInfo = new StringBuilder();
        tonnageInfo.append(String.format("Weapons: %.1ft | Armor: %.2ft | Total: %.2f/%.1ft", 
            weaponTonnage, armorTonnage, totalTonnage, maxTonnage));
        
        // Color code based on overage
        if (totalTonnage > maxTonnage) {
            totalWeaponsLabel.setForeground(Color.RED);
            tonnageInfo.append(" ⚠️ OVERWEIGHT");
        } else if (totalTonnage > maxTonnage * 0.95) {
            totalWeaponsLabel.setForeground(Color.ORANGE);
            tonnageInfo.append(" ⚠️ NEAR LIMIT");
        } else {
            totalWeaponsLabel.setForeground(Color.BLACK);
        }
        
        totalWeaponsLabel.setText(tonnageInfo.toString());
        totalWeaponsLabel.revalidate();
        totalWeaponsLabel.repaint();
    }
    
    /**
     * Update the visual display
     */
    private void updateDisplay() {
        contentPanel.removeAll();
        
        int totalWeapons = 0;
        
        // Add weapons by section
        for (String sectionName : equippedWeaponsBySection.keySet()) {
            List<WeaponComponent> weapons = equippedWeaponsBySection.get(sectionName);
            if (weapons != null && !weapons.isEmpty()) {
                totalWeapons += weapons.size();
                
                // Create section header
                JPanel sectionHeader = createSectionHeader(sectionName, weapons.size());
                contentPanel.add(sectionHeader);
                
                // Add weapons in this section
                for (WeaponComponent weapon : weapons) {
                    JPanel weaponRow = createWeaponRow(weapon, sectionName);
                    contentPanel.add(weaponRow);
                    contentPanel.add(Box.createVerticalStrut(1));
                }
                
                contentPanel.add(Box.createVerticalStrut(3));
            }
        }
        
        // Update total count
        totalWeaponsLabel.setText("Total Weapons: " + totalWeapons);
        
        // Add flexible space at bottom
        contentPanel.add(Box.createVerticalGlue());
        
        // Show message if no weapons equipped
        if (totalWeapons == 0) {
            JLabel noWeaponsLabel = new JLabel("No weapons equipped");
            noWeaponsLabel.setFont(new Font("Arial", Font.ITALIC, 10));
            noWeaponsLabel.setForeground(Color.GRAY);
            noWeaponsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(noWeaponsLabel);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Create a section header for a mech section
     */
    private JPanel createSectionHeader(String sectionName, int weaponCount) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 220, 220));
        header.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        JLabel nameLabel = new JLabel(sectionName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 10));
        
        JLabel countLabel = new JLabel("(" + weaponCount + ")");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        countLabel.setForeground(Color.DARK_GRAY);
        
        header.add(nameLabel, BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Create a row displaying weapon information
     */
    private JPanel createWeaponRow(WeaponComponent weapon, String sectionName) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(1, 15, 1, 5));
        
        // Weapon name and type
        JLabel weaponLabel = new JLabel(weapon.getName());
        weaponLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        
        // Weapon type indicator
        JLabel typeLabel = new JLabel(weapon.getType());
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 8));
        typeLabel.setForeground(Color.DARK_GRAY);
        
        // Create left panel for weapon info
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(weaponLabel, BorderLayout.WEST);
        leftPanel.add(typeLabel, BorderLayout.SOUTH);
        
        row.add(leftPanel, BorderLayout.WEST);
        
        return row;
    }
    
    /**
     * Clear all equipped weapons
     */
    public void clearWeapons() {
        equippedWeaponsBySection.clear();
        updateDisplay();
    }
} 