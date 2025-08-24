package com.mechbuilder.ui.components;

import com.mechbuilder.data.WeaponRepository;
import com.mechbuilder.model.WeaponComponent;
import com.opencsv.exceptions.CsvValidationException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Panel containing the weapon arsenal with collapsible category folders
 * for better organization and less clutter.
 */
public class WeaponArsenalPanel extends JPanel {
    
    private final WeaponRepository weaponRepository;
    private final JPanel mainPanel;
    private final JScrollPane scrollPane;
    private final Map<String, CollapsibleCategory> categoryPanels;
    
    public WeaponArsenalPanel() throws IOException, CsvValidationException {
        this.weaponRepository = new WeaponRepository();
        this.categoryPanels = new HashMap<>();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2), 
            "Weapon Arsenal", 
            TitledBorder.CENTER, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        
        // Create main panel for categories
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        
        // Add scroll pane
        scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(250, 600));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Load weapons organized by category
        loadWeaponsByCategory();
    }
    
    private void loadWeaponsByCategory() throws IOException, CsvValidationException {
        mainPanel.removeAll();
        
        // Get all weapons
        List<WeaponComponent> allWeapons = weaponRepository.loadAll();
        
        // Group weapons by type
        Map<String, List<WeaponComponent>> weaponsByType = new HashMap<>();
        for (WeaponComponent weapon : allWeapons) {
            String type = weapon.getType();
            weaponsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(weapon);
        }
        
        // Create collapsible panels for each category
        for (String weaponType : weaponsByType.keySet()) {
            List<WeaponComponent> weapons = weaponsByType.get(weaponType);
            CollapsibleCategory categoryPanel = new CollapsibleCategory(weaponType, weapons);
            categoryPanels.put(weaponType, categoryPanel);
            mainPanel.add(categoryPanel);
            mainPanel.add(Box.createVerticalStrut(2));
        }
        
        // Add flexible space at the bottom
        mainPanel.add(Box.createVerticalGlue());
        
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    /**
     * Collapsible category panel for weapon types
     */
    private class CollapsibleCategory extends JPanel {
        private final String categoryName;
        private final List<WeaponComponent> weapons;
        private final JPanel contentPanel;
        private JLabel toggleLabel;
        private boolean isExpanded;
        
        public CollapsibleCategory(String categoryName, List<WeaponComponent> weapons) {
            this.categoryName = categoryName;
            this.weapons = weapons;
            this.isExpanded = true; // Start expanded
            
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            setBackground(Color.WHITE);
            
            // Create header panel with toggle button
            JPanel headerPanel = createHeaderPanel();
            add(headerPanel, BorderLayout.NORTH);
            
            // Create content panel for weapons
            contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            
            // Add weapons to content panel
            for (WeaponComponent weapon : weapons) {
                WeaponPanel weaponPanel = new WeaponPanel(weapon);
                contentPanel.add(weaponPanel);
                contentPanel.add(Box.createVerticalStrut(1));
            }
            
            add(contentPanel, BorderLayout.CENTER);
            
            // Set initial state
            updateExpansionState();
        }
        
        private JPanel createHeaderPanel() {
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(new Color(240, 240, 240));
            header.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
            header.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Toggle label with arrow and category name
            toggleLabel = new JLabel();
            toggleLabel.setFont(new Font("Arial", Font.BOLD, 12));
            updateToggleLabel();
            
            // Category count
            JLabel countLabel = new JLabel("(" + weapons.size() + ")");
            countLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            countLabel.setForeground(Color.DARK_GRAY);
            
            header.add(toggleLabel, BorderLayout.WEST);
            header.add(countLabel, BorderLayout.EAST);
            
            // Add click listener to toggle expansion
            header.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    toggleExpansion();
                }
            });
            
            return header;
        }
        
        private void updateToggleLabel() {
            String arrow = isExpanded ? "▼ " : "▶ ";
            toggleLabel.setText(arrow + categoryName);
        }
        
        private void toggleExpansion() {
            isExpanded = !isExpanded;
            updateExpansionState();
        }
        
        private void updateExpansionState() {
            contentPanel.setVisible(isExpanded);
            updateToggleLabel();
            revalidate();
            repaint();
        }
    }
    
    public void refreshWeapons() {
        try {
            loadWeaponsByCategory();
        } catch (IOException | CsvValidationException ex) {
            showError("Error refreshing weapons: " + ex.getMessage());
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}